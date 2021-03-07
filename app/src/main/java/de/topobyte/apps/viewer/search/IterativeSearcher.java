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

package de.topobyte.apps.viewer.search;

import android.graphics.Point;
import android.util.Log;

import com.slimjars.dist.gnu.trove.set.TIntSet;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.android.misc.utils.AndroidTimeUtil;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.nomioc.luqe.dao.MatchMode;
import de.topobyte.nomioc.luqe.model.SqEntity;
import de.topobyte.sqlitespatial.spatialindex.access.SpatialIndex;

public abstract class IterativeSearcher<T extends SqEntity>
{

  private static final String LOG_TAG = "search";

  public List<T> findIteratively(IConnection ldb, String simplified,
                                 MatchMode matchMode, Point position, SpatialIndex spatialIndex,
                                 TypeSelection typeSelection, int LIMIT) throws QueryException
  {
    int area = 256;
    int n = 0;

    TIntSet sids = getSpatialIds(spatialIndex, position, area);
    List<T> candidates = query(ldb, simplified, matchMode, sids,
        typeSelection, 1000, 0);
    List<T> pois = filter(candidates, position, area);
    n = pois.size();

    while (n < LIMIT) {
      Log.i(LOG_TAG, "Trying: " + area);
      AndroidTimeUtil.time("find iteration");

      int oldArea = area;
      area = (int) Math.ceil(area * 1.5);
      TIntSet sidsInner = getSpatialIds(spatialIndex, position, oldArea);
      TIntSet sidsOuter = getSpatialIds(spatialIndex, position, area);
      Log.i(LOG_TAG, "inner: " + sidsInner);
      Log.i(LOG_TAG, "outer: " + sidsOuter);
      sidsOuter.removeAll(sidsInner);
      Log.i(LOG_TAG, "after: " + sidsOuter);

      if (sids.size() > 0) {
        List<T> nextCandidates = query(ldb, simplified, matchMode,
            sidsOuter, typeSelection, 1000, 0);
        candidates.addAll(nextCandidates);
      }
      pois = filter(candidates, position, area);

      n = pois.size();
      AndroidTimeUtil.time("find iteration", LOG_TAG,
          "Time to get items i: %d, got: " + n);
    }

    return pois;
  }

  protected abstract List<T> query(IConnection ldb, String simplified,
                                   MatchMode matchMode, TIntSet sids, TypeSelection typeSelection,
                                   int limit, int offset) throws QueryException;

  private TIntSet getSpatialIds(SpatialIndex index, Point position, int area)
  {
    int minX = position.x - area;
    int maxX = position.x + area;
    int minY = position.y - area;
    int maxY = position.y + area;
    return index.getSpatialIndexIds(minX, maxX, minY, maxY);
  }

  private List<T> filter(List<T> candidates, Point position, int area)
  {
    List<T> matches = new ArrayList<>();
    for (T poi : candidates) {
      if (poi.getX() > position.x + area
          || poi.getX() < position.x - area
          || poi.getY() > position.y + area
          || poi.getY() < position.y - area) {
        continue;
      }
      matches.add(poi);
    }
    return matches;
  }

}
