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

package de.topobyte.apps.viewer.coordinatesystems;

import static de.topobyte.apps.viewer.coordinatesystems.CoordinateSystem.UTM;
import static de.topobyte.apps.viewer.coordinatesystems.CoordinateSystem.WGS84;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import java.util.Locale;

public class CoordinateFormatter
{

  private CoordinateSystem coordinateSystem = CoordinateSystemConfig.DEFAULT;

  private CoordinateTransform wgsToUtm = null;

  private CoordinateTransform getWgsToUtm()
  {
    if (wgsToUtm == null) {
      CRSFactory crsFactory = new CRSFactory();
      CoordinateReferenceSystem WGS84 =
          crsFactory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");
      CoordinateReferenceSystem UTM =
          crsFactory.createFromParameters("UTM",
              "+proj=utm +zone=33 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");

      CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
      wgsToUtm = ctFactory.createTransform(WGS84, UTM);
    }
    return wgsToUtm;
  }

  public CoordinateSystem getCoordinateSystem()
  {
    return coordinateSystem;
  }

  public void setCoordinateSystem(CoordinateSystem coordinateSystem)
  {
    this.coordinateSystem = coordinateSystem;
  }

  public String format(String description, double lon, double lat)
  {
    if (coordinateSystem == WGS84) {
      return String.format(Locale.GERMAN, "%s (lon/lat): %f %f", description, lon, lat);
    } else if (coordinateSystem == UTM) {
      CoordinateTransform wgsToUtm = getWgsToUtm();
      ProjCoordinate result = new ProjCoordinate();
      wgsToUtm.transform(new ProjCoordinate(lon, lat), result);
      return String.format(Locale.GERMAN, "%s: E %.2f N %.2f", description, result.x, result.y);
    }
    return null;
  }

}
