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

import org.locationtech.jts.geom.Envelope;

public class AppConstants
{
  public static final boolean HAS_POSITION = true;
  public static final boolean HAS_ZOOM = true;

  public static final boolean USE_MAPFILE_POSITION = false;
  public static final double STARTUP_LON = 13.387012;
  public static final double STARTUP_LAT = 52.520138;
  public static final int STARTUP_ZOOM = 14;

  public static final boolean HAS_MIN_ZOOM = true;
  public static final boolean HAS_MAX_ZOOM = true;

  public static final int MIN_ZOOM = 10;
  public static final int MAX_ZOOM = 22;

  public static final Envelope BBOX = new Envelope(13.068373, 13.780818, 52.318095, 52.695454);

  public static final String CITY_NAME = "Berlin";
  public static final String ISO3 = "DEU";

  public static final String PACKAGE_NETZPLAN = "de.topobyte.transportation.plan.deu.berlin";

}