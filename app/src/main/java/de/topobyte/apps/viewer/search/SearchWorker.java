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

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.Log;

import com.slimjars.dist.gnu.trove.set.TIntSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import de.topobyte.android.misc.utils.AndroidTimeUtil;
import de.topobyte.apps.viewer.diacritic.DiacriticUtil;
import de.topobyte.luqe.android.AndroidConnection;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.dao.MatchMode;
import de.topobyte.nomioc.luqe.dao.SortOrder;
import de.topobyte.nomioc.luqe.model.SqEntity;
import de.topobyte.nomioc.luqe.model.SqPoi;
import de.topobyte.nomioc.luqe.model.SqRoad;
import de.topobyte.sqlitespatial.spatialindex.access.SpatialIndex;

public class SearchWorker implements Runnable
{
  private static final String LOG_TAG = "search";

  private static final int LIMIT = 50;
  private static final int LIMIT2 = 500;

  private static DiacriticUtil diacritic = new DiacriticUtil();

  private boolean destroyed = false;

  private Object sync = new Object();

  private SearchQuery last = null;
  private SearchQuery queue = null;

  private String filename = null;
  protected IConnection ldb = null;
  protected SQLiteDatabase db = null;

  private SearchResultsReceiver receiver;

  private SpatialIndex spatialIndexStreets;
  private SpatialIndex spatialIndexPois;

  public SearchWorker(String filename, SearchResultsReceiver receiver,
                      SpatialIndex spatialIndexStreets, SpatialIndex spatialIndexPois)
  {
    this.filename = filename;
    this.receiver = receiver;
    this.spatialIndexStreets = spatialIndexStreets;
    this.spatialIndexPois = spatialIndexPois;
  }

  public synchronized void openDatabase()
  {
    if (db == null) {
      db = SQLiteDatabase.openOrCreateDatabase(filename, null);
      ldb = new AndroidConnection(db);
    }
  }

  public synchronized void closeDatabase()
  {
    if (db != null) {
      db.close();
      db = null;
    }
  }

  public void queueQuery(SearchQuery query)
  {
    synchronized (sync) {
      queue = query;
      last = query;
      sync.notify();
    }
  }

  public SearchQuery getLastIssuedQuery()
  {
    synchronized (sync) {
      return last;
    }
  }

  @Override
  public void run()
  {
    while (true) {
      SearchQuery myQuery = null;
      synchronized (sync) {
        if (destroyed) {
          break;
        }
        if (queue == null) {
          try {
            sync.wait();
          } catch (InterruptedException e) {
            // continue
          }
          continue;
        }
        myQuery = queue;
        queue = null;
      }
      executeQuery(myQuery);
    }
    closeDatabase();
    Log.i(LOG_TAG, "SearchWorker quit");
  }

  private IterativeSearcher<SqRoad> searcherRoads = new IterativeSearcher<SqRoad>()
  {

    @Override
    protected List<SqRoad> query(IConnection ldb, String querystring,
                                 MatchMode matchMode, TIntSet sids, TypeSelection typeSelection,
                                 int limit, int offset) throws QueryException
    {
      return Dao.getRoadsInIndex(ldb, querystring, matchMode, sids,
          limit, offset);
    }
  };

  private IterativeSearcher<SqPoi> searcherPoi = new IterativeSearcher<SqPoi>()
  {

    @Override
    protected List<SqPoi> query(IConnection ldb, String querystring,
                                MatchMode matchMode, TIntSet sids, TypeSelection typeSelection,
                                int limit, int offset) throws QueryException
    {
      List<SqPoi> pois = null;
      if (typeSelection.getTypeSelection() == PoiTypeSelection.NONE) {
        pois = new ArrayList<>();
      } else if (typeSelection.getTypeSelection() == PoiTypeSelection.ALL) {
        pois = Dao.getPoisInIndex(ldb, querystring, matchMode, sids,
            limit, offset);
      } else {
        pois = Dao.getPoisInIndex(ldb, querystring, matchMode, sids,
            typeSelection.getTypeIds(), limit, offset);
      }
      return pois;
    }
  };

  private void executeQuery(SearchQuery query)
  {
    openDatabase();
    if (db == null) {
      receiver.reportNone(query);
    }

    String simplified = diacritic.simplify(query.getQuery());

    if (query.getResultOrder() == ResultOrder.BY_DISTANCE) {
      Point position = query.getPosition();
      Log.i(LOG_TAG, "position: " + position.x + " " + position.y);
    }

    TypeSelection typeSelection = query.getTypeSelection();
    Log.i(LOG_TAG, "type selection: " + typeSelection.getTypeSelection());

    ResultOrder order = query.getResultOrder();
    SortOrder sortOrder;
    switch (order) {
      default:
      case ALPHABETICALLY:
      case BY_DISTANCE:
        sortOrder = SortOrder.ASCENDING;
        break;
      case ALPHABETICALLY_INVERSE:
        sortOrder = SortOrder.DESCENDING;
        break;
    }

    List<SqPoi> pois = null;
    List<SqRoad> streets = null;

    // POIs

    AndroidTimeUtil.time("search poi query");
    try {
      pois = executePoiQuery(query, simplified, sortOrder, typeSelection);
    } catch (QueryException e) {
      Log.e(LOG_TAG, "Error while fetching pois", e);
      receiver.reportNone(query);
    }

    AndroidTimeUtil.time("fill poi types");
    try {
      pois = Dao.fillTypes(ldb, pois);
    } catch (QueryException e) {
      Log.e(LOG_TAG, "Error while fetching poi types", e);
    }
    AndroidTimeUtil.time("fill poi types", LOG_TAG,
        "Time to fill poi types: %d");

    AndroidTimeUtil.time("search poi query", LOG_TAG,
        "Time to query pois: %d");

    // Streets

    if (!query.getTypeSelection().isIncludeStreets()) {
      streets = new ArrayList<>();
    } else {
      AndroidTimeUtil.time("search street query");
      try {
        streets = executeStreetQuery(query, simplified, sortOrder,
            typeSelection);
      } catch (QueryException e) {
        Log.e(LOG_TAG, "Error while fetching streets", e);
        receiver.reportNone(query);
      }
      AndroidTimeUtil.time("search street query", LOG_TAG,
          "Time to query streets: %d");
    }

    // Merge

    int nPois = pois == null ? 0 : pois.size();
    int nStreets = streets == null ? 0 : streets.size();
    if (nPois == 0 && nStreets == 0) {
      receiver.reportNone(query);
      return;
    }

    List<SqEntity> results;
    if (order == ResultOrder.ALPHABETICALLY) {
      results = mergeAlphabetically(streets, pois, false);
    } else if (order == ResultOrder.ALPHABETICALLY_INVERSE) {
      results = mergeAlphabetically(streets, pois, true);
    } else {
      results = mergeByDistance(streets, pois, query.getPosition());
    }

    receiver.report(query, results);
  }

  private List<SqPoi> executePoiQuery(SearchQuery query, String simplified,
                                      SortOrder sortOrder, TypeSelection typeSelection)
      throws QueryException
  {
    MatchMode matchMode = query.getMatchMode();
    ResultOrder order = query.getResultOrder();

    List<SqPoi> pois = null;
    if (order == ResultOrder.ALPHABETICALLY
        || order == ResultOrder.ALPHABETICALLY_INVERSE) {

      pois = queryPois(simplified, matchMode, sortOrder, typeSelection,
          LIMIT);
    } else {
      AndroidTimeUtil.time("get pois");
      pois = queryPois(simplified, matchMode, SortOrder.ASCENDING,
          typeSelection, LIMIT2 + 1);
      AndroidTimeUtil.time("get pois", LOG_TAG, "Time to get pois: %d");

      if (pois.size() > LIMIT2) {
        AndroidTimeUtil.time("get pois iter");

        pois = searcherPoi.findIteratively(ldb, simplified, matchMode,
            query.getPosition(), spatialIndexPois, typeSelection,
            LIMIT);

        AndroidTimeUtil.time("get pois iter", LOG_TAG,
            "Time to get pois iteratively: %d");
        Log.i(LOG_TAG, "Number of pois: " + pois.size());
      }

      AndroidTimeUtil.time("sort pois");
      sortByDistance(pois, query.getPosition());
      AndroidTimeUtil.time("sort pois", LOG_TAG, "Time to sort pois: %d");

      for (int i = pois.size() - 1; i >= LIMIT; i--) {
        pois.remove(i);
      }
    }

    return pois;
  }

  private List<SqPoi> queryPois(String simplified, MatchMode matchMode,
                                SortOrder sortOrder, TypeSelection typeSelection, int limit)
      throws QueryException
  {
    List<SqPoi> pois = null;
    if (typeSelection.getTypeSelection() == PoiTypeSelection.NONE) {
      pois = new ArrayList<>();
    } else if (typeSelection.getTypeSelection() == PoiTypeSelection.ALL) {
      pois = Dao.getPois(ldb, simplified, matchMode, sortOrder, limit, 0);
    } else {
      pois = Dao.getPois(ldb, simplified, matchMode, sortOrder,
          typeSelection.getTypeIds(), limit, 0);
    }
    return pois;
  }

  private List<SqRoad> executeStreetQuery(SearchQuery query,
                                          String simplified, SortOrder sortOrder, TypeSelection typeSelection)
      throws QueryException
  {
    MatchMode matchMode = query.getMatchMode();
    ResultOrder order = query.getResultOrder();

    List<SqRoad> streets;
    if (order == ResultOrder.ALPHABETICALLY
        || order == ResultOrder.ALPHABETICALLY_INVERSE) {
      streets = Dao.getRoads(ldb, simplified, matchMode, sortOrder,
          LIMIT, 0);
    } else {
      AndroidTimeUtil.time("get streets");
      streets = Dao.getRoads(ldb, simplified, matchMode,
          SortOrder.ASCENDING, LIMIT2 + 1, 0);
      AndroidTimeUtil.time("get streets", LOG_TAG,
          "Time to get streets: %d");
      if (streets.size() > LIMIT2) {
        AndroidTimeUtil.time("get streets iter");

        streets = searcherRoads.findIteratively(ldb, simplified,
            matchMode, query.getPosition(), spatialIndexStreets,
            typeSelection, LIMIT);

        AndroidTimeUtil.time("get streets iter", LOG_TAG,
            "Time to get streets iteratively: %d");
        Log.i(LOG_TAG, "Number of streets: " + streets.size());
      }

      AndroidTimeUtil.time("sort streets");
      sortByDistance(streets, query.getPosition());
      AndroidTimeUtil.time("sort streets", LOG_TAG,
          "Time to sort pois: %d");

      for (int i = streets.size() - 1; i >= LIMIT; i--) {
        streets.remove(i);
      }
    }
    return streets;
  }

  private void sortByDistance(List<? extends SqEntity> pois, final Point point)
  {
    Collections.sort(pois, new Comparator<SqEntity>()
    {

      @Override
      public int compare(SqEntity a, SqEntity b)
      {
        long d = distance(a, point) - distance(b, point);
        if (d == 0) {
          return 0;
        } else if (d < 0) {
          return -1;
        } else {
          return 1;
        }
      }
    });
  }

  private List<SqEntity> mergeAlphabetically(List<SqRoad> streets,
                                             List<SqPoi> pois, boolean inverse)
  {
    List<SqEntity> results = new ArrayList<>();

    int i = 0;
    int j = 0;
    while (true) {
      if (i == streets.size() || j == pois.size()) {
        break;
      }

      SqRoad road = streets.get(i);
      SqPoi poi = pois.get(j);
      String roadName = road.getSimpleName().toLowerCase(Locale.US);
      String poiName = poi.getSimpleName().toLowerCase(Locale.US);
      int c = roadName.compareTo(poiName);
      if ((!inverse && c <= 0) || (inverse && c >= 0)) {
        results.add(road);
        i++;
      } else {
        results.add(poi);
        j++;
      }
    }

    if (i < streets.size()) {
      for (int k = i; k < streets.size(); k++) {
        results.add(streets.get(k));
      }
    }
    if (j < pois.size()) {
      for (int k = j; k < pois.size(); k++) {
        results.add(pois.get(k));
      }
    }

    return results;
  }

  private List<SqEntity> mergeByDistance(List<SqRoad> streets,
                                         List<SqPoi> pois, Point position)
  {
    List<SqEntity> results = new ArrayList<>();

    int i = 0;
    int j = 0;
    while (true) {
      if (i == streets.size() || j == pois.size()) {
        break;
      }

      SqRoad road = streets.get(i);
      SqPoi poi = pois.get(j);

      double distRoad = distance(road, position);
      double distPoi = distance(poi, position);

      if (distRoad - distPoi <= 0) {
        results.add(road);
        i++;
      } else {
        results.add(poi);
        j++;
      }
    }

    if (i < streets.size()) {
      for (int k = i; k < streets.size(); k++) {
        results.add(streets.get(k));
      }
    }
    if (j < pois.size()) {
      for (int k = j; k < pois.size(); k++) {
        results.add(pois.get(k));
      }
    }

    return results;
  }

  private long distance(SqEntity entity, Point position)
  {
    long dy = entity.getY() - position.y;
    long dx = entity.getX() - position.x;
    return dx * dx + dy * dy;
  }

  public void destroy()
  {
    synchronized (sync) {
      destroyed = true;
      sync.notify();
    }
  }

}
