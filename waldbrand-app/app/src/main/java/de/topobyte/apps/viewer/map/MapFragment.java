// Copyright 2021 Sebastian Kuerten
//
// This file is part of waldbrand-app.
//
// waldbrand-app is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// waldbrand-app is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with waldbrand-app. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.apps.viewer.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ZoomControls;

import androidx.fragment.app.Fragment;

import org.locationtech.jts.geom.Coordinate;

import java.util.List;

import de.topobyte.android.maps.utils.MagnificationConfig;
import de.topobyte.android.maps.utils.MapZoomControls;
import de.topobyte.android.maps.utils.view.MapPosition;
import de.topobyte.android.misc.utils.Toaster;
import de.topobyte.apps.viewer.AppConstants;
import de.topobyte.apps.viewer.Constants;
import de.topobyte.apps.viewer.NoLocationSourceDialog;
import de.topobyte.apps.viewer.label.Poi;
import de.topobyte.apps.viewer.location.LocationOverlay;
import de.topobyte.apps.viewer.location.MapLocationListener;
import de.topobyte.apps.viewer.overlay.OverlayGps;
import de.topobyte.apps.viewer.overlay.OverlayGroup;
import de.topobyte.jeography.core.mapwindow.MapWindow;
import de.topobyte.mapocado.android.mapfile.MapfileOpener;
import de.topobyte.mapocado.android.style.MapRenderConfig;
import de.waldbrandapp.PoiClickListener;
import de.waldbrandapp.PoiDetailsFragment;
import de.waldbrandapp.R;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MapFragment extends Fragment
    implements RenderThemeListener, EasyPermissions.PermissionCallbacks, PoiClickListener
{

  public final static String ARG_USE_INTENT = "use-intent";

  @Override
  public void onPoiClicked(Poi poi)
  {
    PoiDetailsFragment poiDetails = new PoiDetailsFragment();
    poiDetails.show(getFragmentManager(), PoiDetailsFragment.TAG);
  }

  public interface OnViewCreatedListener
  {

    void mapFragmentCreated();

    void mapFragmentViewCreated();

  }

  public interface HasToaster
  {

    Toaster getToaster();

  }

  private static final String LOG_TAG = "mapfragment";

  private static final int DEFAULT_MIN_ZOOM = 10;
  private static final int DEFAULT_MAX_ZOOM = 21;

  // bundle keys
  private static final String BUNDLE_IS_SHOW_MY_LOCATION = "showMyLocation";
  private static final String BUNDLE_IS_SNAP_TO_LOCATION = "snapToLocation";
  private static final String BUNDLE_IS_MOVE_TO_LOCATION_ONCE = "moveToLocationOnce";

  private Global global;

  protected MapfileOpener opener;

  private MapViewWithOverlays map;
  private OverlayGroup overlay;
  private ZoomControls zoomControls;

  private MagnificationConfig magnificationConfig;

  private final PositionHistory history = new PositionHistory();

  // location management
  private LocationManager locationManager;
  private MapLocationListener locationListener;
  private OverlayGps overlayGps;

  private boolean showMyLocation = false;
  private boolean snapToLocation = false;
  private boolean moveToLocationOnce = false;

  // location overlay
  private LocationOverlay locationOverlay;

  // search stuff
  private boolean resultMarkerShown = false;
  private MarkerOverlay resultOverlay;

  private Toaster toaster;

  @Override
  public void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);
    Log.i(LOG_TAG, "MapFragment.onCreate() " + (bundle != null));

    global = Global.getInstance(getActivity());

    setHasOptionsMenu(true);
  }

  @Override
  public void onActivityCreated(Bundle bundle)
  {
    super.onActivityCreated(bundle);
    try {
      OnViewCreatedListener listener = (OnViewCreatedListener) getActivity();
      listener.mapFragmentCreated();
    } catch (ClassCastException e) {
      Log.w(LOG_TAG, "Unable to cast activity to OnViewCreatedListener");
    }
    try {
      HasToaster hasToaster = (HasToaster) getActivity();
      toaster = hasToaster.getToaster();
    } catch (ClassCastException e) {
      Log.w(LOG_TAG, "Unable to cast activity to HasToaster");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle bundle)
  {
    super.onCreateView(inflater, container, bundle);
    Log.i(LOG_TAG, "MapFragment.onCreateView()");

    View view = inflater.inflate(R.layout.map, container, false);

    global.addRenderThemeListener(this);

    magnificationConfig = MagnificationConfig
        .getMagnificationConfig(getActivity());

    map = view.findViewById(R.id.map);
    overlay = view.findViewById(R.id.overlay);
    zoomControls = view.findViewById(R.id.overlay_zoom);

    try {
      OnViewCreatedListener listener = (OnViewCreatedListener) getActivity();
      listener.mapFragmentViewCreated();
    } catch (ClassCastException e) {
      Log.w(LOG_TAG, "Unable to cast activity to OnViewCreatedListener");
    }

    // setup zoom bounds
    MapWindow mapWindow = map.getMapWindow();
    if (AppConstants.HAS_MIN_ZOOM) {
      mapWindow.setMinZoom(AppConstants.MIN_ZOOM);
    } else {
      mapWindow.setMinZoom(DEFAULT_MIN_ZOOM);
    }
    if (AppConstants.HAS_MAX_ZOOM) {
      mapWindow.setMaxZoom(AppConstants.MAX_ZOOM);
    } else {
      mapWindow.setMaxZoom(DEFAULT_MAX_ZOOM);
    }

    // set map file
    try {
      map.setMapFile(global.getMapFileOpener(), global.getMapFileOpenerWaldbrand());
    } catch (Exception e) {
      Log.e(LOG_TAG, "Error while setting mapfile", e);
    }

    // setup result marker overlay
    resultOverlay = new MarkerOverlay(global.getDensity());
    map.addOnDrawListener(resultOverlay);

    // setup location overlay
    locationManager = (LocationManager) getActivity().getSystemService(
        Context.LOCATION_SERVICE);
    locationListener = new MapLocationListener(this, global.getLocation());

    overlayGps = overlay.getOverlayGps();
    overlayGps.getSnapGpsButton().setOnClickListener(v -> overlayGpsClicked());

    locationOverlay = new LocationOverlay(map.getMapWindow(),
        global.getDensity());
    locationOverlay.setLocation(global.getLocation());
    map.addOnDrawListener(locationOverlay);

    // setup zoom controls
    MapZoomControls<MapView> mapZoomControls = new MapZoomControls<>(map, zoomControls);
    map.setOnTouchListener(mapZoomControls);
    map.getMapWindow().addZoomListener(mapZoomControls);

    // add POI click listener
    map.setPoiClickListener(this);

    // set map position
    MapPreferenceAbstraction mpa = new MapPreferenceAbstraction(
        getActivity(), global);
    MapPosition position = mpa.getMapPosition();

    Bundle arguments = getArguments();
    boolean useIntent = false;
    if (arguments != null) {
      useIntent = arguments.getBoolean(ARG_USE_INTENT);
      arguments.clear();
    }
    Log.i(LOG_TAG, "use intent? " + useIntent);
    if (useIntent) {
      Intent intent = getActivity().getIntent();
      Bundle extras = intent.getExtras();
      if (extras != null && extras.containsKey("lon")
          && extras.containsKey("lat")) {
        double lat = extras.getDouble("lat");
        double lon = extras.getDouble("lon");
        Log.i(LOG_TAG, "position: " + lon + " " + lat);
        position.setLat(lat);
        position.setLon(lon);
        if (extras.containsKey("min-zoom")) {
          int minZoom = extras.getInt("min-zoom");
          if (position.getZoom() < minZoom) {
            position.setZoom(minZoom);
          }
        }
      }
    }

    map.getMapWindow().gotoLonLat(position.getLon(), position.getLat());
    map.getSteplessMapWindow().zoom(position.getZoom());

    renderThemeChanged(global.getRenderConfig());

    map.postInvalidate();

    if (bundle == null) {
      return view;
    }

    showMyLocation = bundle.getBoolean(BUNDLE_IS_SHOW_MY_LOCATION);
    boolean snap = bundle.getBoolean(BUNDLE_IS_SNAP_TO_LOCATION);
    moveToLocationOnce = bundle.getBoolean(BUNDLE_IS_MOVE_TO_LOCATION_ONCE);
    locationOverlay.setEnabled(showMyLocation);
    setSnapToLocation(snap);

    return view;
  }

  protected void overlayGpsClicked()
  {
    setSnapToLocation(overlayGps.getSnapGpsButton().getStatus());
    if (snapToLocation) {
      toaster.toastLong(getString(R.string.snap_to_location_enabled));
    } else {
      toaster.toastLong(getString(R.string.snap_to_location_disabled));
    }
  }

  @Override
  public void onDestroyView()
  {
    super.onDestroyView();

    global.removeRenderThemeListener(this);

    if (map != null) {
      map.destroy();
    }
  }

  public MapViewWithOverlays getMap()
  {
    return map;
  }

  public OverlayGroup getOverlayGroup()
  {
    return overlay;
  }

  @Override
  public void onSaveInstanceState(Bundle bundle)
  {
    super.onSaveInstanceState(bundle);

    bundle.putBoolean(BUNDLE_IS_SHOW_MY_LOCATION, showMyLocation);
    bundle.putBoolean(BUNDLE_IS_SNAP_TO_LOCATION, snapToLocation);
    bundle.putBoolean(BUNDLE_IS_MOVE_TO_LOCATION_ONCE, moveToLocationOnce);
  }

  @Override
  public void onPause()
  {
    super.onPause();
    Log.i(LOG_TAG, "MapFragment.onPause()");

    // save the map position and zoom level
    MapWindow mapWindow = map.getMapWindow();
    double lon = mapWindow.getCenterLon();
    double lat = mapWindow.getCenterLat();
    double zoom = mapWindow.getZoom();

    MapPreferenceAbstraction mpa = new MapPreferenceAbstraction(
        getActivity(), global);

    mpa.storePosition(lon, lat, zoom);

    if (showMyLocation) {
      disconnectLocationManager();
    }
  }

  @Override
  public void onResume()
  {
    super.onResume();
    Log.i(LOG_TAG, "MapFragment.onResume()");

    loadMagnificationSettingsAndFixOutOfBounds();

    if (showMyLocation) {
      if (hasLocationPermission()) {
        connectLocationManagerWithGrantedPermission();
      } else {
        disableShowMyLocation(true);
      }
    }
    ensureSnapToLocationButtonState();

    // marker
    MapPreferenceAbstraction mpa = new MapPreferenceAbstraction(
        getActivity(), global);
    if (mpa.hasMarker()) {
      Coordinate c = mpa.getMarker();
      Log.d("marker", String.format("onResume: %f, %f", c.x, c.y));
      showResultPoint(c, false);
    }

    // move speed
    int moveSpeed = mpa.getMoveSpeed();
    setMoveSpeed(moveSpeed);

    // scale bar
    boolean hasScaleBar = mpa.hasScaleBar();
    setHasScaleBar(hasScaleBar);
  }

  private void setHasScaleBar(boolean hasScaleBar)
  {
    map.getScaleDrawer().setEnabled(hasScaleBar);
  }

  private void setMoveSpeed(int speed)
  {
    if (speed < 0) {
      speed = 0;
    }
    if (speed > Constants.MAX_MOVE_SPEED) {
      speed = Constants.MAX_MOVE_SPEED;
    }
    float moveSpeedFactor = speed / 100f;
    map.setMoveSpeed(moveSpeedFactor);
  }

  private void loadMagnificationSettingsAndFixOutOfBounds()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());

    // magnification
    int magnification100 = Constants.DEFAULT_MAGNIFICATION;
    if (preferences.contains(Constants.PREF_MAGNIFICATION)) {
      magnification100 = preferences.getInt(Constants.PREF_MAGNIFICATION,
          Constants.DEFAULT_MAGNIFICATION);
      boolean changePreference = false;
      if (magnification100 < magnificationConfig.min) {
        magnification100 = magnificationConfig.min;
        changePreference = true;
      } else if (magnification100 > magnificationConfig.max) {
        magnification100 = magnificationConfig.max;
        changePreference = true;
      }
      if (changePreference) {
        Editor editor = preferences.edit();
        editor.putInt(Constants.PREF_MAGNIFICATION, magnification100);
        editor.commit();
      }
    }

    float magnificationSet = magnification100 / 100f;
    float magnificationBase = magnificationConfig.base / 100f;
    float magnification = magnificationSet * magnificationBase;

    map.setMagnification(magnification);

    float tileScaleFactor = map.getTileScaleFactor();
    float userScaleFactor = map.getUserScaleFactor();

    global.setMagnification(tileScaleFactor, userScaleFactor);

    // This is the tile size
    int worldScale = map.getMapWindow().getWorldScale();
  }

  public boolean onBackPressed()
  {
    if (!history.isEmpty()) {
      Coordinate c = history.pop();
      moveMapToLocation(c, false);
      return true;
    }
    return false;
  }

  private void moveMapToLocation(Coordinate c, boolean storeOldInHistory)
  {
    moveMapToLocation(c, map.getMapWindow().getZoom(), storeOldInHistory);
  }

  public void moveMapToLocation(Coordinate c, double zoom,
                                boolean storeOldInHistory)
  {
    if (snapToLocation) {
      setSnapToLocation(false);
      ensureSnapToLocationButtonState();
    }

    if (storeOldInHistory) {
      double clon = map.getMapWindow().getCenterLon();
      double clat = map.getMapWindow().getCenterLat();
      Coordinate old = new Coordinate(clon, clat);
      double distance = old.distance(c);
      Log.i("history", "distance:" + distance);
      if (distance > PositionHistory.THRESHOLD) {
        history.push(old);
      }
    }

    map.getMapWindow().gotoLonLat(c.x, c.y);
    map.getSteplessMapWindow().zoom(zoom);
    map.postInvalidate();
  }

  /*
   * Location stuff
   */

  private void setSnapToLocation(boolean snap)
  {
    snapToLocation = snap;

    map.getEventManager().setAllowTrackball(!snapToLocation);
    map.getEventManager().setAllowTouchMovement(!snapToLocation);
    map.getEventManager().setAllowZoomAtPosition(!snapToLocation);

    if (snapToLocation && locationOverlay.isValid()) {
      Location location = locationOverlay.getLocation();
      if (location != null) {
        gotoLocation(location);
      }
    }
  }

  private void enableShowMyLocation(boolean gotoLocationOnFirstUpdate)
  {
    if (showMyLocation) {
      return;
    }

    boolean permitted = requestLocationPermission(Constants.RC_LOCATION_UPDATES);
    if (!permitted) {
      return;
    }

    boolean success = connectLocationManagerWithGrantedPermission();
    if (!success) {
      return;
    }

    showMyLocation = true;
    moveToLocationOnce = true;
    ensureSnapToLocationButtonState();
    locationOverlay.setLocation(null);
    locationOverlay.setEnabled(true);
    updateMenu();
  }

  private void disableShowMyLocation(boolean disconnectLocationManager)
  {
    if (!this.showMyLocation) {
      return;
    }

    if (disconnectLocationManager) {
      disconnectLocationManager();
    }

    showMyLocation = false;
    moveToLocationOnce = false;
    setSnapToLocation(false);
    ensureSnapToLocationButtonState();
    locationOverlay.setEnabled(false);

    updateMenu();
    map.postInvalidate();
  }

  private boolean hasLocationPermission()
  {
    return EasyPermissions.hasPermissions(getActivity(), Constants.PERMS_LOCATION_ARRAY);
  }

  private boolean requestLocationPermission(int requestCode)
  {
    if (!EasyPermissions.hasPermissions(getActivity(), Constants.PERMS_LOCATION_ARRAY)) {
      Log.i("permissions", "asking for location permissions");
      EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_permission),
          requestCode, Constants.PERMS_LOCATION_ARRAY);
      return false;
    }
    return true;
  }

  @SuppressWarnings({"MissingPermission"})
  private boolean connectLocationManagerWithGrantedPermission()
  {
    boolean connected = false;
    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
      locationManager
          .requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
              1000, 0, locationListener);
      connected = true;
    }
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      locationManager.requestLocationUpdates(
          LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
      connected = true;
    }

    if (!connected) {
      NoLocationSourceDialog dialog = new NoLocationSourceDialog();
      dialog.show(getActivity().getSupportFragmentManager(), null);
    }

    return connected;
  }

  @SuppressWarnings({"MissingPermission"})
  private void disconnectLocationManager()
  {
    locationManager.removeUpdates(locationListener);
  }

  private void ensureSnapToLocationButtonState()
  {
    overlayGps.getSnapGpsButton().setStatus(snapToLocation);
    if (showMyLocation) {
      overlayGps.setVisibility(View.VISIBLE);
    } else {
      overlayGps.setVisibility(View.GONE);
    }
  }

  private void gotoLastKnownPosition()
  {
    boolean permitted = requestLocationPermission(Constants.RC_LAST_LOCATION);
    if (!permitted) {
      return;
    }

    gotoLastKnownPositionWithGrantedPermission();
  }

  @SuppressWarnings({"MissingPermission"})
  private void gotoLastKnownPositionWithGrantedPermission()
  {
    Location bestLocation = null;
    for (String provider : locationManager.getProviders(true)) {
      Log.i("maploc", "get last known location from: " + provider);
      Location location = locationManager.getLastKnownLocation(provider);
      if (location == null) {
        continue;
      }
      Log.i("maploc", "time: " + location.getTime());
      Log.i("maploc", "accuracy: " + location.getAccuracy());
      if (bestLocation == null) {
        bestLocation = location;
        continue;
      }
      if (location.getTime() > bestLocation.getTime()) {
        bestLocation = location;
      }
    }

    if (bestLocation == null) {
      toaster.toastLong(getString(R.string.error_last_location_unknown));
    } else {
      gotoLocation(bestLocation);
    }
  }

  public void updateLocation(Location location)
  {
    showLocation(location);
    global.setLocation(location);
    if (snapToLocation || moveToLocationOnce) {
      moveToLocationOnce = false;
      gotoLocation(location);
    } else {
      map.postInvalidate();
    }
  }

  private void gotoLocation(Location location)
  {
    double lat = location.getLatitude();
    double lon = location.getLongitude();

    if (!AppConstants.BBOX.contains(lon, lat)) {
      return;
    }

    map.getMapWindow().gotoLonLat(lon, lat);
    map.postInvalidate();
  }

  private void showLocation(Location location)
  {
    double lat = location.getLatitude();
    double lon = location.getLongitude();
    float accuracy = location.getAccuracy();

    Log.i("location", String.format("lon: %f, lat: %f, accuracy: %f", lon,
        lat, accuracy));

    locationOverlay.setLocation(location);
  }

  /*
   * Menu
   */

  @Override
  public void onPrepareOptionsMenu(Menu menu)
  {
    Log.d("menu", "onPrepareOptionsMenu");

    menuState(menu, R.id.menu_position_my_location_enable, !showMyLocation);
    menuState(menu, R.id.menu_position_my_location_disable, showMyLocation);

    menuState(menu, R.id.menu_search_remove, resultMarkerShown);
  }

  private void menuState(Menu menu, int itemId, boolean state)
  {
    MenuItem item = menu.findItem(itemId);
    if (item != null) {
      item.setVisible(state);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId()) {
      case R.id.menu_search_remove:
        hideResultPoint();
        return true;

      case R.id.menu_position:
        return true;

      case R.id.menu_position_my_location_enable:
        enableShowMyLocation(true);
        return true;

      case R.id.menu_position_my_location_disable:
        disableShowMyLocation(true);
        return true;

      case R.id.menu_position_last_known:
        gotoLastKnownPosition();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /*
   * Marker
   */

  public void showResultPoint(Coordinate c, boolean store)
  {
    resultMarkerShown = true;
    resultOverlay.setEnabled(true);
    resultOverlay.setMarkerPosition(c);

    Log.d("marker", String.format("showResultPoint: %f, %f", c.x, c.y));

    if (store) {
      new MapPreferenceAbstraction(getActivity(), global).storeMarker(
          c.x, c.y);
    }

    updateMenu();
  }

  private void hideResultPoint()
  {
    resultMarkerShown = false;
    resultOverlay.setEnabled(false);

    new MapPreferenceAbstraction(getActivity(), global).removeMarker();

    updateMenu();
    map.postInvalidate();
  }

  private void updateMenu()
  {
    getActivity().supportInvalidateOptionsMenu();
  }

  /*
   * Render theme, layers
   */

  @Override
  public void renderThemeChanged(MapRenderConfig mapRenderConfig)
  {
    map.setRenderConfig(mapRenderConfig);
    locationOverlay.setColors(mapRenderConfig);
    resultOverlay.setMarker(mapRenderConfig);
  }

  public void updateLayers()
  {
    map.getLabelDrawer().reloadVisibility(getActivity());
    map.getLabelDrawer().layersChanged();
    map.postInvalidate();
  }

  /*
   * Permissions
   */

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
  {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  @Override
  public void onPermissionsGranted(int requestCode, List<String> list)
  {
    Log.i("permissions", "permission granted for request code: " + requestCode);
    if (requestCode == Constants.RC_LOCATION_UPDATES) {
      enableShowMyLocation(true);
    } else if (requestCode == Constants.RC_LAST_LOCATION) {
      gotoLastKnownPositionWithGrantedPermission();
    }
  }

  @Override
  public void onPermissionsDenied(int requestCode, List<String> list)
  {
    Log.i("permissions", "permission denied for request code: " + requestCode);
    if (requestCode == Constants.RC_LOCATION_UPDATES || requestCode == Constants.RC_LAST_LOCATION) {
      if (EasyPermissions.somePermissionPermanentlyDenied(this, Constants.PERMS_LOCATION_LIST)) {
        new AppSettingsDialog.Builder(this)
            .setTitle(R.string.title_location_permission)
            .setRationale(R.string.rationale_location_permission_denied)
            .setOpenInNewTask(true).build().show();
      }
    }
  }

}
