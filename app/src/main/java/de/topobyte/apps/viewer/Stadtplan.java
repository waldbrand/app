// Copyright 2021 Sebastian Kuerten
//
// This file is part of stadtplan-app.
//
// stadtplan-app is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// stadtplan-app is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with stadtplan-app. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.apps.viewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.locationtech.jts.geom.Coordinate;

import de.topobyte.android.loader3.TaskFragment;
import de.topobyte.android.misc.utils.Toaster;
import de.topobyte.apps.viewer.drawer.DrawerList;
import de.topobyte.apps.viewer.drawer.HelpDrawerDialog;
import de.topobyte.apps.viewer.map.MapActivity;
import de.topobyte.apps.viewer.map.MapFragment;
import de.topobyte.apps.viewer.map.MapViewWithOverlays;
import de.topobyte.apps.viewer.overlay.OverlayListener;
import de.topobyte.apps.viewer.overlay.ThemeSelectorDialog;
import de.topobyte.apps.viewer.search.SearchActivity;
import de.topobyte.mercatorcoordinates.GeoConv;
import de.waldbrandapp.R;

public class Stadtplan extends MapActivity implements OverlayListener,
    MapFragment.OnViewCreatedListener, MapFragment.HasToaster
{

  private static final int INTENT_SEARCH = 1;

  private static final String LOG_LIFECYCLE = "lifecycle";

  // bundle keys
  private static final String BUNDLE_IS_DRAWER_VISIBLE = "drawerVisible";
  private static final String BUNDLE_STARTED = "started";

  // message display
  private Toaster toaster;

  private boolean boot = true;

  private MapFragment mapFragment;

  private boolean loaded = false;

  @Override
  public TaskFragment createTaskFragment()
  {
    return new InitFragment();
  }

  @Override
  public void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);

    Log.i(LOG_LIFECYCLE, String.format("onCreate: have bundle? %b", bundle != null));

    if (bundle != null) {
      boot = bundle.getBoolean(BUNDLE_STARTED);

      drawerPartiallyOrCompletelyVisible = bundle
          .getBoolean(BUNDLE_IS_DRAWER_VISIBLE);
    }

    Log.i(LOG_LIFECYCLE, String.format(
        "onCreate: loaded: %b, initializationDone: %b, initializationSucceeded: %b",
        loaded, initializationDone, initializationSucceeded));

    if (!loaded && initializationDone) {
      init();
    }
  }

  @Override
  public void loaderFinished()
  {
    super.loaderFinished();

    if (!loaded) {
      init();
    }
  }

  private void init()
  {
    loaded = true;

    if (!initializationSucceeded) {
      ErrorDialog dialog = new ErrorDialog();
      dialog.show(getSupportFragmentManager(), null);
    } else {
      toaster = new Toaster(this);

      Bundle mapArgs = new Bundle();
      mapArgs.putBoolean(MapFragment.ARG_USE_INTENT, boot);

      SharedPreferences preferences = PreferenceManager
          .getDefaultSharedPreferences(this);

      if (boot) {
        boot = false;
        boolean showTips = preferences.getBoolean(
            Constants.PREF_TIPS_AT_STARTUP,
            Constants.DEFAULT_TIPS_AT_STARTUP);
        if (showTips) {
          TipsAndTricks.showTipsAndTricks(this);
        }
      }

      setupDrawer();

      String mapTag = "map";

      FragmentManager fm = getSupportFragmentManager();
      mapFragment = (MapFragment) fm.findFragmentByTag(mapTag);
      if (mapFragment == null) {
        mapFragment = new MapFragment();
        mapFragment.setArguments(mapArgs);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, mapFragment, mapTag);
        transaction.commit();
      }

      drawerToggle.syncState();
    }
  }

  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle drawerToggle;
  private boolean drawerPartiallyOrCompletelyVisible = false;
  private View drawer;
  private DrawerList drawerList;

  private void setupDrawer()
  {
    drawerLayout = findViewById(R.id.drawer_layout);
    drawer = findViewById(R.id.left_drawer);
    drawerList = drawer.findViewById(R.id.drawer_list);

    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
        R.string.open_drawer, R.string.close_drawer)
    {

      @Override
      public void onDrawerClosed(View view)
      {
        super.onDrawerClosed(view);
        Log.i("slide", "closed");
        updateMenuWithDrawerState(false);
      }

      @Override
      public void onDrawerOpened(View drawerView)
      {
        super.onDrawerOpened(drawerView);
        Log.i("slide", "opened");
        updateMenuWithDrawerState(true);
      }

      @Override
      public void onDrawerSlide(View drawerView, float slideOffset)
      {
        super.onDrawerSlide(drawerView, slideOffset);
        Log.i("slide", "offset: " + slideOffset);
        // this is necessary for the case where we pull out the drawer
        // only partially and let it slide back, onDrawerClosed won't be
        // called then.
        boolean state = slideOffset > 0;
        updateMenuWithDrawerState(state);
      }
    };

    // Set the drawer toggle as the DrawerListener
    drawerLayout.setDrawerListener(drawerToggle);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    DrawerList list = findViewById(R.id.drawer_list);
    list.setStadtplan(this);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    super.onConfigurationChanged(newConfig);
    if (drawerToggle != null) {
      drawerToggle.onConfigurationChanged(newConfig);
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle bundle)
  {
    super.onSaveInstanceState(bundle);

    Log.i(LOG_LIFECYCLE, String.format(
        "onSaveInstanceState: loaded: %b, initializationDone: %b, initializationSucceeded: %b",
        loaded, initializationDone, initializationSucceeded));

    bundle.putBoolean(BUNDLE_IS_DRAWER_VISIBLE,
        drawerPartiallyOrCompletelyVisible);

    bundle.putBoolean(BUNDLE_STARTED, boot);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    Log.d("menu", "onCreateOptionsMenu");
    if (!drawerPartiallyOrCompletelyVisible) {
      getMenuInflater().inflate(R.menu.options_menu, menu);
    } else {
      getMenuInflater().inflate(R.menu.options_menu_drawer, menu);
    }

    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    Log.d("menu", "onPrepareOptionsMenu");

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    boolean handled = CommonMenu.handleMenuItemSelected(this, item);
    handled |= handleMenuItemSelected(item);

    return handled;
  }

  private boolean handleMenuItemSelected(MenuItem item)
  {
    switch (item.getItemId()) {

      case R.id.menu_search:
        startSearch();
        return true;

      case R.id.menu_help_drawer:
        HelpDrawerDialog helpDialog = new HelpDrawerDialog();
        helpDialog.show(getSupportFragmentManager(), null);
        return true;

      case R.id.menu_select_all:
        drawerList.selectAll();
        return true;

      case R.id.menu_select_none:
        drawerList.selectNone();
        return true;

      default:
        return false;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if (keyCode == KeyEvent.KEYCODE_SEARCH) {
      startSearch();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public void onBackPressed()
  {
    if (drawerPartiallyOrCompletelyVisible) {
      drawerLayout.closeDrawers();
    } else {
      boolean handled = false;
      if (mapFragment != null) {
        handled = mapFragment.onBackPressed();
      }
      if (!handled) {
        moveTaskToBack(true);
      }
    }
  }

  @Override
  public boolean onTrackballEvent(MotionEvent event)
  {
    // forward the event to the MapView
    mapFragment.getMap().onTrackballEvent(event);
    return true;
  }

  @Override
  public void onPause()
  {
    super.onPause();

    if (toaster != null) {
      toaster.cancel();
    }
  }

  @Override
  public void onResume()
  {
    super.onResume();

    Bundle extras = getIntent().getExtras();
    if (extras != null && extras.containsKey("lat")
        && extras.containsKey("lon")) {
      double lat = extras.getDouble("lat");
      double lon = extras.getDouble("lon");
      Log.i("config", "lat: " + lat + " lon: " + lon);
    }

    if (!loaded && initializationDone) {
      init();
    }

    if (loaded && initializationSucceeded) {
      drawerToggle.syncState();
      setupDebugOptions();
    }
  }

  private void setupDebugOptions()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(this);
    boolean showGrid =
        preferences.getBoolean(Constants.PREF_SHOW_GRID, Constants.DEFAULT_SHOW_GRID);
    boolean showZoomLevel =
        preferences.getBoolean(Constants.PREF_SHOW_ZOOM_LEVEL, Constants.DEFAULT_SHOW_ZOOM_LEVEL);
    boolean showCoordinates =
        preferences.getBoolean(Constants.PREF_SHOW_COORDINATES, Constants.DEFAULT_SHOW_COORDINATES);

    MapViewWithOverlays map = mapFragment.getMap();
    map.setDrawGrid(showGrid);
    map.setDrawZoomLevel(showZoomLevel);
    map.setDrawPosition(showCoordinates);
  }

  /**
   * Show the search activity.
   */
  private void startSearch()
  {
    MapViewWithOverlays map = mapFragment.getMap();
    int mx = GeoConv.mercatorFromLongitude(map.getMapWindow()
        .getCenterLon());
    int my = GeoConv
        .mercatorFromLatitude(map.getMapWindow().getCenterLat());
    Intent intent = new Intent(this, SearchActivity.class);
    intent.putExtra(SearchActivity.EXTRA_X, mx);
    intent.putExtra(SearchActivity.EXTRA_Y, my);
    startActivityForResult(intent, INTENT_SEARCH);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if (requestCode == INTENT_SEARCH) {
      if (resultCode == RESULT_OK) {
        if (data != null && data.hasExtra("lon")
            && data.hasExtra("lat")) {
          double lon = data.getDoubleExtra("lon", 0.0);
          double lat = data.getDoubleExtra("lat", 0.0);
          int zoom = 16;

          Coordinate c = new Coordinate(lon, lat);
          mapFragment.showResultPoint(c, true);
          mapFragment.moveMapToLocation(c, zoom, true);
        }
      }
    }
  }

  void updateMenu()
  {
    supportInvalidateOptionsMenu();
  }

  void updateMenuWithDrawerState(boolean drawerVisibility)
  {
    if (drawerVisibility == drawerPartiallyOrCompletelyVisible) {
      return;
    }
    drawerPartiallyOrCompletelyVisible = drawerVisibility;
    supportInvalidateOptionsMenu();
  }

  @Override
  public void selectTheme()
  {
    ThemeSelectorDialog dialog = new ThemeSelectorDialog();
    dialog.show(getSupportFragmentManager(), null);
  }

  @Override
  public void selectLayers()
  {
    drawerLayout.openDrawer(drawer);
  }

  public void updateLayers()
  {
    mapFragment.updateLayers();
  }

  @Override
  public void mapFragmentCreated()
  {
    mapFragment.getOverlayGroup().setOverlayListener(this);
  }

  @Override
  public void mapFragmentViewCreated()
  {
    Log.i("orientation", "is: "
        + getResources().getConfiguration().orientation);

    setupDebugOptions();
  }

  @Override
  public Toaster getToaster()
  {
    return toaster;
  }

}
