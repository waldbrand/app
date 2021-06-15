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

package de.topobyte.apps.viewer.label;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.slimjars.dist.gnu.trove.list.TIntList;
import com.slimjars.dist.gnu.trove.list.array.TIntArrayList;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.adt.geo.BBox;
import de.topobyte.android.maps.utils.label.Label;
import de.topobyte.android.maps.utils.label.QueryWorker;
import de.topobyte.android.maps.utils.map.BaseMapView;
import de.topobyte.android.misc.utils.AndroidTimeUtil;
import de.topobyte.luqe.android.AndroidConnection;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.mapocado.android.mapfile.MapfileOpener;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.TextNode;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.model.SqLabel;
import de.topobyte.sqlitespatial.spatialindex.access.SpatialIndex;

/**
 * This worker executes queries on the database and returns the results to the
 * LabelDrawer.
 */
public class QueryWorkerPoi extends QueryWorker<BaseMapView>
{
  private final static String LOG = "labels";

  private final SQLiteDatabase db;

  private final LabelDrawerPoi labelDrawerPoi;

  private RenderConfig renderConfig;

  private final IConnection ldb;

  private final SpatialIndex spatialIndex;

  private Mapfile mapfile;
  private Mapfile mapfileHydrants;

  public QueryWorkerPoi(LabelDrawerPoi labelDrawer, SQLiteDatabase db,
                        RenderConfig renderConfig, SpatialIndex spatialIndex,
                        MapfileOpener opener, MapfileOpener openerHydrants)
  {
    super(labelDrawer);
    labelDrawerPoi = labelDrawer;

    this.db = db;
    this.renderConfig = renderConfig;
    this.spatialIndex = spatialIndex;

    ldb = new AndroidConnection(db);

    try {
      mapfile = opener.open();
    } catch (Exception e) {
      Log.e(LOG, "Unable to open mapfile", e);
    }
    try {
      mapfileHydrants = openerHydrants.open();
    } catch (Exception e) {
      Log.e(LOG, "Unable to open hydrants mapfile", e);
    }
  }

  public void setRenderConfig(RenderConfig renderConfig)
  {
    this.renderConfig = renderConfig;
  }

  private Label createLabel(SqLabel label, int placeType)
  {
    return new Label(label.getX(), label.getY(), label.getName(),
        placeType, label.getId());
  }

  @Override
  protected TIntObjectHashMap<List<Label>> runQuery(BBox bbox, int zoom)
  {
    Log.i(LOG, "box: " + bbox);

    // TODO: be aware of the labels the LabelDrawer knows anyway (by means
    // of a stored currentlyKnownArea) and only report new items to him.
    TIntObjectHashMap<List<Label>> labelMapClass = new TIntObjectHashMap<>();
    TIntObjectHashMap<List<Label>> labelMapType = new TIntObjectHashMap<>();

    AndroidTimeUtil.time("query labels");

    TIntList ids = renderConfig.getRelevantTypeIds(zoom);

    List<SqLabel> labels;
    try {
      if (zoom > 12) {
        labels = Dao.getLabels(ldb, spatialIndex, bbox, ids);
      } else {
        labels = Dao.getLabels(ldb, bbox, ids);
      }
    } catch (QueryException e) {
      Log.e(LOG, "Error while fetching labels", e);
      return labelMapClass;
    }

    Log.i(LOG, "results #: " + labels.size());

    for (SqLabel label : labels) {
      int typeId = label.getType();
      int classId = renderConfig.getClassIdForTypeId(typeId);
      List<Label> l = labelMapType.get(typeId);
      if (l == null) {
        l = new ArrayList<>();
        labelMapClass.put(classId, l);
        labelMapType.put(typeId, l);
      }

      l.add(createLabel(label, classId));
      Log.i(LOG, "got: " + label.getName() + " " + label.getX() + " "
          + label.getY());
    }
    AndroidTimeUtil.time("query labels", LOG, "Time to query labels: %d");

    AndroidTimeUtil.time("query housenumbers");

    if (renderConfig.areHousenumbersRelevant(zoom)) {
      DiskTree<TextNode> treeHousenumbers = mapfile.getTreeHousenumbers();

      BoundingBox rectRequest = new BoundingBox(bbox.getLon1(),
          bbox.getLon2(), bbox.getLat1(), bbox.getLat2(), true);
      try {
        List<TextNode> housenumbers = treeHousenumbers
            .intersectionQuery(rectRequest);
        Log.i("housenumbers",
            "Number of housenumbers: " + housenumbers.size());
        int classId = renderConfig.getHousenumberClassId();

        List<Label> list = new ArrayList<>();
        labelMapClass.put(classId, list);
        for (TextNode number : housenumbers) {
          Coordinate point = number.getPoint();
          list.add(new Label(point.getX(), point.getY(), number
              .getText(), classId, -1));
        }
      } catch (IOException e) {
        Log.e(LOG, "Error while querying mapfile for housenumbers", e);
      }
    }

    AndroidTimeUtil.time("query housenumbers", LOG,
        "Time to query housenumbers: %d");

    BoundingBox rectRequest = new BoundingBox(bbox.getLon1(),
        bbox.getLon2(), bbox.getLat1(), bbox.getLat2(), true);


    IntervalTree<Integer, DiskTree<Node>> nodeTrees = mapfileHydrants.getTreeNodes();
    for (DiskTree<Node> t : nodeTrees.getObjects(zoom)) {
      try {
        List<Node> nodes = t.intersectionQuery(rectRequest);
        System.out.println("nodes: " + nodes.size());
        for (Node node : nodes) {
          TIntArrayList classes = node.getClasses();
          for (int ci : classes.toArray()) {
            // TODO: do not resolve via strings, use int lookup
            String type = mapfileHydrants.getMetadata().getPoolForRefs().getString(ci);

            RenderClass renderClass = renderConfig.getRenderClass(type);
            if (renderClass == null) {
              continue;
            }
            int classId = renderClass.classId;
            List<Label> list = labelMapClass.get(classId);
            if (list == null) {
              list = new ArrayList<>();
              labelMapClass.put(classId, list);
            }

            Coordinate point = node.getPoint();
            list.add(new Label(point.getX(), point.getY(), "", classId, -1));
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    IntervalTree<Integer, DiskTree<Way>> wayTrees = mapfileHydrants.getTreeWays();
    for (DiskTree<Way> t : wayTrees.getObjects(zoom)) {
      try {
        List<Way> ways = t.intersectionQuery(rectRequest);
        System.out.println("ways: " + ways.size());
        for (Way way : ways) {

        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return labelMapClass;
  }

  @Override
  public void destroy()
  {
    super.destroy();
    if (mapfile != null) {
      try {
        mapfile.close();
      } catch (IOException e) {
        Log.e(LOG, "Unable to close mapfile", e);
      }
    }
  }

}
