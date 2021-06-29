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

import android.util.Log;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.sqlitespatial.spatialindex.access.SpatialIndex;

public class AppData
{

  private SpatialIndex spatialIndexStreets;
  private SpatialIndex spatialIndexPois;

  private static AppData instance = null;

  public static AppData getInstance(IConnection db)
  {
    if (instance == null) {
      instance = new AppData();
      instance.initInstance(db);
    }
    return instance;
  }

  private void initInstance(IConnection db)
  {
    try {
      spatialIndexStreets = new SpatialIndex(db, "si_streets");
      Log.i("labels", "Number of entries in spatial index: "
          + spatialIndexStreets.getSize());
    } catch (QueryException e) {
      Log.e("labels", "Error while retrieving spatial index for streets",
          e);
    }
    try {
      spatialIndexPois = new SpatialIndex(db, "si_pois");
      Log.i("labels", "Number of entries in spatial index: "
          + spatialIndexPois.getSize());
    } catch (QueryException e) {
      Log.e("labels", "Error while retrieving spatial index for pois", e);
    }
  }

  public SpatialIndex getSpatialIndexStreets()
  {
    return spatialIndexStreets;
  }

  public SpatialIndex getSpatialIndexPois()
  {
    return spatialIndexPois;
  }

}
