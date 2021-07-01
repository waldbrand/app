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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.infomatiq.jsi.Rectangle;
import com.slimjars.dist.gnu.trove.iterator.TIntIterator;
import com.slimjars.dist.gnu.trove.list.TIntList;
import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.topobyte.adt.geo.BBox;
import de.topobyte.android.maps.utils.label.Label;
import de.topobyte.android.maps.utils.label.LabelBox;
import de.topobyte.android.maps.utils.label.LabelDrawer;
import de.topobyte.android.maps.utils.label.RectangleIntersectionTester;
import de.topobyte.android.maps.utils.map.BaseMapView;
import de.topobyte.android.misc.utils.AndroidTimeUtil;
import de.topobyte.apps.viewer.Database;
import de.topobyte.bvg.BvgAndroidPainter;
import de.topobyte.bvg.BvgIO;
import de.topobyte.bvg.BvgImage;
import de.topobyte.jeography.core.mapwindow.MapWindow;
import de.topobyte.jeography.core.mapwindow.SteplessMapWindow;
import de.topobyte.mapocado.android.mapfile.MapfileOpener;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.styles.labels.elements.IconLabel;
import de.topobyte.mapocado.styles.labels.elements.LabelType;
import de.topobyte.mercatorcoordinates.GeoConv;
import de.topobyte.sqlitespatial.spatialindex.access.SpatialIndex;
import de.waldbrandapp.Waldbrand;

public class LabelDrawerPoi extends LabelDrawer<Integer, LabelClass, BaseMapView>
{
  public static final String PREF_ENABLED = "labelDrawerEnabled";
  public static final boolean DEFAULT_ENABLED = true;

  public static final String PREF_MODE = "labelDrawerMode";
  public static final LabelMode DEFAULT_MODE = LabelMode.BY_CONFIG;

  private final static String LOG = "labels";
  private final static String LOG_TIMES = "labels-time";

  private QueryWorkerPoi queryWorkerPoi;

  private final String filename;
  private SQLiteDatabase db = null;

  private final Context context;

  private final SpatialIndex spatialIndex;
  private final MapfileOpener opener;
  private final MapfileOpener openerWaldbrand;

  private RenderConfig renderConfig;

  public synchronized void openDatabase()
  {
    if (this.db == null) {
      this.db = SQLiteDatabase.openOrCreateDatabase(filename, null);
    }
  }

  public synchronized void closeDatabase()
  {
    this.db.close();
    this.db = null;
  }

  public LabelDrawerPoi(Context context, View view, float density,
                        SpatialIndex spatialIndex, MapfileOpener opener,
                        MapfileOpener openerWaldbrand) throws IOException
  {
    super(context, view, density);
    this.context = context;

    this.spatialIndex = spatialIndex;
    this.opener = opener;
    this.openerWaldbrand = openerWaldbrand;

    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(context);

    boolean enabled = prefs.getBoolean(PREF_ENABLED, DEFAULT_ENABLED);
    setEnabled(enabled);

    filename = Database.getDatabasePath(context);

    // TODO: does this do anything?
    for (int key : labelClasses.keys()) {
      labelClassToId.put(labelClasses.get(key), key);
    }

    openDatabase();
  }

  public void setRenderConfig(RenderConfig renderConfig)
  {
    this.renderConfig = renderConfig;

    initWorkers();

    for (int key : labelClasses.keys()) {
      LabelClass labelClass = labelClasses.get(key);
      labelClass.setMagnification(magnification);
    }

    for (Bitmap bitmap : symbolsCache.values()) {
      bitmap.recycle();
    }
    symbolsCache.clear();
    clearBitmapsAndCandidates();
  }

  boolean init = false;

  private void initWorkers()
  {
    renderedLabels.clear();
    for (int i : renderConfig.getAllClassIds().toArray()) {
      renderedLabels.put(i, new ArrayList<>());

      RenderClass renderClass = renderConfig.get(i);
      labelClasses.put(i, renderClass.labelClass);
      labelClassToId.put(renderClass.labelClass, i);
    }

    if (!init) {
      queryWorkerPoi = new QueryWorkerPoi(this, db, renderConfig,
          spatialIndex, opener, openerWaldbrand);
      queryWorker = queryWorkerPoi;

      new Thread(queryWorker).start();

      renderWorker = new RenderWorkerPoi(context, this);
      new Thread(renderWorker).start();
    } else {
      queryWorkerPoi.setRenderConfig(renderConfig);
    }

    this.enabledInternally = true;
  }

  @Override
  protected void render(SteplessMapWindow mapWindow, BBox bbox, Canvas canvas,
                        RectangleIntersectionTester tester)
  {
    double zoom = mapWindow.getZoom();
    int izoom = (int) Math.floor(zoom);

    TIntList ids = renderConfig.getRelevantClassIds(izoom);

    TIntIterator iterator;

    AndroidTimeUtil.time("render icons");
    iterator = ids.iterator();
    while (iterator.hasNext()) {
      int id = iterator.next();
      renderIcons(id, mapWindow, bbox, canvas, tester);
    }
    AndroidTimeUtil.time("render icons", LOG_TIMES,
        "time for rendering icons: %d");

    AndroidTimeUtil.time("render dots");
    iterator = ids.iterator();
    while (iterator.hasNext()) {
      int id = iterator.next();
      renderDots(id, mapWindow, bbox, canvas, tester);
    }
    AndroidTimeUtil.time("render dots", LOG_TIMES,
        "time for rendering dots: %d");

    AndroidTimeUtil.time("render labels");
    TIntSet usedIds = new TIntHashSet();
    iterator = ids.iterator();
    while (iterator.hasNext()) {
      int id = iterator.next();
      renderLabels(id, mapWindow, bbox, canvas, tester, usedIds);
    }
    AndroidTimeUtil.time("render labels", LOG_TIMES,
        "time for rendering labels: %d");
  }

  private boolean contains(BBox box, double lon, double lat)
  {
    return lon >= box.getLon1() && lon <= box.getLon2()
        && lat >= box.getLat2() && lat <= box.getLat1();
  }

  private final Map<String, Bitmap> symbolsCache = new HashMap<>();

  private void renderIcons(int type, SteplessMapWindow mapWindow, BBox bbox,
                           Canvas canvas, RectangleIntersectionTester tester)
  {
    Set<Label> labels = candidates.get(type);

    if (labels == null) {
      return;
    }

    LabelClass labelClass = labelClasses.get(type);
    if (labelClass.type != LabelType.ICON) {
      return;
    }

    IconLabel icon = (IconLabel) labelClass.labelStyle;

    String imageName = icon.getImage();

    Bitmap bitmap = symbolsCache.get(imageName);

    if (bitmap == null) {
      BvgImage image = null;
      File file = renderConfig.getSymbol(imageName);

      try {
        image = BvgIO.read(file);
      } catch (IOException e) {
        Log.e("render-worker", "Unable to open icon: '" + file.getPath()
            + "'");
        return;
      }

      float height = labelClass.iconSize;
      float scale = (float) (height / image.getHeight());
      float width = (float) (image.getWidth() * scale);

      bitmap = Bitmap.createBitmap((int) Math.ceil(width),
          (int) Math.ceil(height), Bitmap.Config.ARGB_8888);
      Canvas c = new Canvas(bitmap);
      BvgAndroidPainter.draw(c, image, 0, 0, scale, scale, scale);

      symbolsCache.put(imageName, bitmap);
    }

    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    for (Label label : labels) {
      double mx = Mercator.getX(label.x, mapWindow.getZoom());
      double my = Mercator.getY(label.y, mapWindow.getZoom());

      float x = (float) mapWindow.mercatorToX(mx) - w / 2;
      float y = (float) mapWindow.mercatorToY(my) - h / 2;
      canvas.drawBitmap(bitmap, x, y, null);
    }

  }

  private void renderDots(int type, SteplessMapWindow mapWindow, BBox bbox,
                          Canvas canvas, RectangleIntersectionTester tester)
  {
    Set<Label> labels = candidates.get(type);

    if (labels == null) {
      return;
    }

    LabelClass labelClass = labelClasses.get(type);
    if (!labelClass.hasDot) {
      return;
    }

    for (Label label : labels) {
      double mx = Mercator.getX(label.x, mapWindow.getZoom());
      double my = Mercator.getY(label.y, mapWindow.getZoom());

      float x = (float) mapWindow.mercatorToX(mx);
      float y = (float) mapWindow.mercatorToY(my);

      canvas.drawCircle(x, y, labelClass.dotSize, labelClass.paintDotFill);
    }
  }

  private void renderLabels(int type, SteplessMapWindow mapWindow, BBox bbox,
                            Canvas canvas, RectangleIntersectionTester tester, TIntSet usedIds)
  {
    Set<Label> labels = candidates.get(type);
    Map<String, Bitmap> bm = bitmaps.get(type);

    List<LabelBox> basket = renderedLabels.get(type);
    basket.clear();

    if (labels == null) {
      return;
    }

    if (bm == null) {
      bm = new HashMap<>();
      bitmaps.put(type, bm);
    }

    BBox storageBox = new BBox(
        GeoConv.mercatorFromLongitude(bbox.getLon1()),
        GeoConv.mercatorFromLatitude(bbox.getLat1()),
        GeoConv.mercatorFromLongitude(bbox.getLon2()),
        GeoConv.mercatorFromLatitude(bbox.getLat2()));

    AndroidTimeUtil.time("cache purge");
    Set<String> used = new HashSet<>();
    Set<String> unused = new HashSet<>();
    Iterator<Label> iterator = labels.iterator();
    while (iterator.hasNext()) {
      Label label = iterator.next();

      if (contains(storageBox, label.x, label.y)) {
        used.add(label.text);
      } else {
        Log.i(LOG, "Removing label from candidates: '" + label.text
            + "'");
        iterator.remove();
        unused.add(label.text);
      }
    }
    for (String text : unused) {
      if (used.contains(text)) {
        continue;
      }
      bm.remove(text);
    }
    AndroidTimeUtil.time("cache purge", LOG_TIMES,
        "time for cache housekeeping during rendering: %d");

    LabelClass labelClass = labelClasses.get(type);
    LabelBoxConfig lbc = labelClass.labelBoxConfig;

    double zoom = mapWindow.getZoom();

    int id = type;

    AndroidTimeUtil.time("render-intersect");
    Log.i(LOG, "LabelDrawer: labels are null: " + (labels == null));
    Log.i(LOG, "LabelDrawer: I got " + labels.size() + " labels");
    Rectangle r = new Rectangle();
    for (Label label : labels) {
      if (label.getText() == null) {
        continue;
      }
      // The dimension of the text box
      if (label.width == -1) {
        label.width = labelClass.getBoxWidth(label.text);
      }

      double mx = Mercator.getX(label.x, zoom);
      double my = Mercator.getY(label.y, zoom);

      float x = (float) mapWindow.mercatorToX(mx);
      float y = (float) mapWindow.mercatorToY(my);

      int bx = Math.round(x - label.width / 2);
      int by = Math.round(y + labelClass.dy);

      int itemId = label.getId();
      // housenumber labels have ids == -1
      if (itemId >= 0 && usedIds.contains(itemId)) {
        continue;
      }

      // try primary position
      r.set(bx, by, bx + label.width, by + lbc.height);
      if (tester.isFree(r)) {
        use(id, label, labelClass, canvas, tester, bm, r, basket);
        usedIds.add(itemId);
        continue;
      }
    }
    AndroidTimeUtil.time("render-intersect", LOG_TIMES,
        "time for rendering and intersections: %d");
  }

  public void reloadVisibility(Context context)
  {
    renderConfig.reloadVisibility(context);
    forceNewQuery();
  }

  @Override
  public void destroy()
  {
    super.destroy();
    db.close();
  }

  public Poi getIcon(MapWindow mapWindow, float ex, float ey)
  {
    int dist = 40;
    int dist2 = dist * dist;

    double zoom = mapWindow.getZoom();

    for (String type : Waldbrand.getLabelTypes()) {
      RenderClass renderClass = renderConfig.getRenderClass(type);
      Set<Label> labels = candidates.get(renderClass.classId);
      if (labels == null) {
        continue;
      }
      for (Label label : labels) {
        double mx = Mercator.getX(label.x, zoom);
        double my = Mercator.getY(label.y, zoom);

        float x = (float) mapWindow.mercatorToX(mx);
        float y = (float) mapWindow.mercatorToY(my);
        if (distance(ex, ey, x, y) < dist2) {
          return new Poi(type, label);
        }
      }
    }
    return null;
  }

  private float distance(float x1, float y1, float x2, float y2)
  {
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
  }

}
