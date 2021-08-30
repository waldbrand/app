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

package de.topobyte.apps.viewer;

import android.Manifest;

import java.util.Arrays;
import java.util.List;

public class Constants
{

  public static final String PREF_HAS_WAKE_LOCK = "wakeLock";
  public static final String PREF_MOVE_SPEED = "moveSpeed";
  public static final String PREF_RENDER_THEME = "renderTheme";
  public static final String PREF_SHOW_SCALE_BAR = "showScaleBar";
  public static final String PREF_MAGNIFICATION = "magnification";
  public static final String PREF_MIN_MAGNIFICATION = "minMagnification";
  public static final String PREF_MAX_MAGNIFICATION = "maxMagnification";
  public static final String PREF_TIPS_AT_STARTUP = "showTips";
  public static final String PREF_CONSENT_GIVEN = "privacyPolicyAccepted1";
  public static final String PREF_APP_INFO = "appInfo";
  public static final String PREF_LOCATION_SOURCES = "locationSources";
  public static final String PREF_CAT_PRIVACY = "privacy";
  public static final String PREF_SHOW_GRID = "showGrid";
  public static final String PREF_SHOW_ZOOM_LEVEL = "showZoomLevel";
  public static final String PREF_SHOW_COORDINATES = "showCoordinates";

  public static final int DEFAULT_MOVE_SPEED = 100;
  public static final int MAX_MOVE_SPEED = 500;

  public static final boolean DEFAULT_HAS_SCALE_BAR = true;

  public static final int DEFAULT_MAGNIFICATION = 100;

  public static boolean DEFAULT_TIPS_AT_STARTUP = true;

  public static boolean DEFAULT_PERSONALIZED_ADS = true;

  public static String[] PERMS_LOCATION_ARRAY = {Manifest.permission.ACCESS_FINE_LOCATION};
  public static List<String> PERMS_LOCATION_LIST = Arrays.asList(PERMS_LOCATION_ARRAY);
  public static final int RC_LOCATION_UPDATES = 1;
  public static final int RC_LAST_LOCATION = 2;

  public static boolean DEFAULT_SHOW_GRID = false;
  public static boolean DEFAULT_SHOW_ZOOM_LEVEL = false;
  public static boolean DEFAULT_SHOW_COORDINATES = false;

}
