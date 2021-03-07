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

package de.topobyte.apps.viewer.map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

import de.topobyte.android.fullscreen.FullscreenUtil;
import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.AppConstants;
import de.topobyte.apps.viewer.Constants;
import de.topobyte.apps.viewer.LoaderActivity;
import de.topobyte.apps.viewer.ResourceConstants;
import de.topobyte.apps.viewer.theme.ThemeConfig;
import de.topobyte.mapocado.android.mapfile.AssetMapFileOpener;
import de.topobyte.mapocado.android.mapfile.MapFileOpener;
import de.topobyte.mapocado.mapformat.Mapfile;

public abstract class MapActivity extends LoaderActivity
{

  private static final String LOG_TAG_MA = "map-activity";

  protected Global global;

  protected MapFileOpener opener;

  private boolean loaded = false;

  @Override
  public void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);

    setupFullScreen();

    if (!loaded && initializationDone && initializationSucceeded) {
      init();
    }
  }

  @Override
  public void loaderFinished()
  {
    if (!loaded && initializationSucceeded) {
      init();
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    Log.i("config", "MapActivity.onResume()");

    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(this);

    if (loaded) {
      updateThemeInformation();
    } else {
      if (initializationDone && initializationSucceeded) {
        init();
      }
    }

    setupFullScreen();

    // wakelock
    if (preferences.getBoolean(Constants.PREF_HAS_WAKE_LOCK, false)) {
      getWindow()
          .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      getWindow().clearFlags(
          WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  }

  private void init()
  {
    Log.i("config", "MapActivity.postLoadCreate()");

    loaded = true;

    setContentView(R.layout.main);

    global = Global.getInstance(this);

    // open map file
    Log.i(LOG_TAG_MA, "Opening map file");
    opener = new AssetMapFileOpener(this, ResourceConstants.ASS_MAP_FILE);

    if (AppConstants.USE_MAPFILE_POSITION) {
      try {
        Mapfile mapfile = opener.open();
        Point point = mapfile.getMetadata().getStart();
        mapfile.close();
        global.setStartPosition(point.getCoordinate());
      } catch (Exception e) {
        e.printStackTrace();
        Log.e(LOG_TAG_MA, "Error opening the mapfile", e);
      }
    } else {
      global.setStartPosition(new Coordinate(AppConstants.STARTUP_LON,
          AppConstants.STARTUP_LAT));
    }

    try {
      global.setMapFile(opener);
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(LOG_TAG_MA, "Error setting the mapfile", e);
    }

    updateThemeInformation();
  }

  private void setupFullScreen()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(this);
    FullscreenUtil.setupFullScreen(this, preferences, getWindow());
  }

  private void updateThemeInformation()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(this);

    // theme
    ThemeConfig themeConfig = new ThemeConfig();

    boolean storeThemeKey = !preferences
        .contains(Constants.PREF_RENDER_THEME);
    String renderThemeKey = preferences.getString(
        Constants.PREF_RENDER_THEME, themeConfig.getDefaultThemeKey());

    boolean loadedTheme = false;
    try {
      loadedTheme = global.setRenderTheme(this, renderThemeKey);
    } catch (IOException e) {
      Log.e("config", "unable to load render theme");
    }
    if (!loadedTheme
        && !renderThemeKey.equals(themeConfig.getDefaultThemeKey())) {
      renderThemeKey = themeConfig.getDefaultThemeKey();
      storeThemeKey = true;
      try {
        loadedTheme = global.setRenderTheme(this, renderThemeKey);
      } catch (IOException e) {
        Log.e("config", "unable to load default render theme");
      }
    }

    if (!themeConfig.getThemeKeySet().contains(renderThemeKey)) {
      renderThemeKey = themeConfig.getDefaultThemeKey();
      storeThemeKey = true;
    }

    if (storeThemeKey) {
      Editor editor = preferences.edit();
      editor.putString(Constants.PREF_RENDER_THEME, renderThemeKey);
      editor.commit();
    }
  }

}
