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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import org.locationtech.jts.geom.Coordinate;

import de.topobyte.android.maps.utils.view.MapPosition;
import de.topobyte.apps.viewer.AppConstants;
import de.topobyte.apps.viewer.Constants;

public class MapPreferenceAbstraction
{

  private static final String PREF_KEY_HAVE_DATA = "haveData";
  private static final String PREF_KEY_ZOOM = "zoom";
  private static final String PREF_KEY_LON = "lon";
  private static final String PREF_KEY_LAT = "lat";

  private static final String PREF_KEY_HAS_MARKER = "hasMarker";
  private static final String PREF_KEY_MARKER_LON = "markerLon";
  private static final String PREF_KEY_MARKER_LAT = "markerLat";

  private final SharedPreferences preferences;
  private final Global global;

  public MapPreferenceAbstraction(Context context, Global global)
  {
    this.global = global;
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public boolean havePosition()
  {
    return preferences.getBoolean(PREF_KEY_HAVE_DATA, false);
  }

  public MapPosition getMapPosition()
  {
    Coordinate c = global.getStartupPosition();
    double lon = preferences.getFloat(PREF_KEY_LON, (float) c.x);
    double lat = preferences.getFloat(PREF_KEY_LAT, (float) c.y);
    double zoom = preferences.getFloat(PREF_KEY_ZOOM, AppConstants.STARTUP_ZOOM);
    return new MapPosition(lon, lat, zoom);
  }

  public void clearPosition()
  {
    Editor editor = preferences.edit();
    editor.putBoolean(PREF_KEY_HAVE_DATA, false);
    editor.remove(PREF_KEY_LAT);
    editor.remove(PREF_KEY_LON);
    editor.remove(PREF_KEY_ZOOM);
    editor.commit();
  }

  public void storePosition(double lon, double lat, double zoom)
  {
    Editor editor = preferences.edit();

    // set the have-data flag
    editor.putBoolean(PREF_KEY_HAVE_DATA, true);

    editor.putFloat(PREF_KEY_LON, (float) lon);
    editor.putFloat(PREF_KEY_LAT, (float) lat);
    editor.putFloat(PREF_KEY_ZOOM, (float) zoom);

    Log.i("config", "store zoom: " + zoom);
    Log.i("config", "store lon, lat: " + lon + ", " + lat);

    editor.commit();
  }

  public boolean hasMarker()
  {
    return preferences.getBoolean(PREF_KEY_HAS_MARKER, false);
  }

  public void removeMarker()
  {
    Editor editor = preferences.edit();
    editor.putBoolean(PREF_KEY_HAS_MARKER, false);
    editor.remove(PREF_KEY_MARKER_LAT);
    editor.remove(PREF_KEY_MARKER_LON);
    editor.commit();
  }

  public void storeMarker(double lon, double lat)
  {
    Editor editor = preferences.edit();
    editor.putBoolean(PREF_KEY_HAS_MARKER, true);
    editor.putLong(PREF_KEY_MARKER_LON, Double.doubleToLongBits(lon));
    editor.putLong(PREF_KEY_MARKER_LAT, Double.doubleToLongBits(lat));
    editor.commit();
  }

  public Coordinate getMarker()
  {
    long lonL = preferences.getLong(PREF_KEY_MARKER_LON, 0);
    long latL = preferences.getLong(PREF_KEY_MARKER_LAT, 0);
    double lon = Double.longBitsToDouble(lonL);
    double lat = Double.longBitsToDouble(latL);
    return new Coordinate(lon, lat);
  }

  public int getMoveSpeed()
  {
    return preferences.getInt(Constants.PREF_MOVE_SPEED,
        Constants.DEFAULT_MOVE_SPEED);
  }

  public boolean hasScaleBar()
  {
    return preferences.getBoolean(Constants.PREF_SHOW_SCALE_BAR,
        Constants.DEFAULT_HAS_SCALE_BAR);
  }
}
