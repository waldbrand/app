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

  public static final boolean HAS_POSITION = false;
  public static final boolean HAS_ZOOM = false;

  public static final boolean USE_MAPFILE_POSITION = true;
  public static final double STARTUP_LON = 0.0;
  public static final double STARTUP_LAT = 0.0;
  public static final int STARTUP_ZOOM = 12;

  public static final boolean HAS_MIN_ZOOM = true;
  public static final boolean HAS_MAX_ZOOM = true;

  public static final int MIN_ZOOM = 10;
  public static final int MAX_ZOOM = 21;

  public static final Envelope BBOX = new Envelope(13.088315, 13.760908,
      52.675476, 52.338076);

  public static final String CITY_NAME = "ATestCity";
  public static final String ISO3 = "DEU";

  public static final String PACKAGE_NETZPLAN = "de.topobyte.transportation.plan.deu.berlin";

}