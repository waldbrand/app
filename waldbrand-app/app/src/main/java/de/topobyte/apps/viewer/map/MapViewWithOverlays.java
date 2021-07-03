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
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.IOException;

import de.topobyte.apps.viewer.AppData;
import de.topobyte.apps.viewer.Database;
import de.topobyte.apps.viewer.label.LabelDrawerPoi;
import de.topobyte.apps.viewer.label.Poi;
import de.topobyte.apps.viewer.label.RenderConfig;
import de.topobyte.luqe.android.AndroidConnection;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.mapocado.android.mapfile.MapfileOpener;
import de.topobyte.mapocado.android.style.MapRenderConfig;
import de.waldbrandapp.PoiClickListener;
import de.waldbrandapp.Waldbrand;

import static android.widget.Toast.LENGTH_SHORT;

public class MapViewWithOverlays extends MapView
{

  private AppData appData;

  private LabelDrawerPoi labelDrawer;
  private MapocadoScaleDrawer scaleDrawer;

  private final float gap = 5;

  private GestureDetector gd;
  private PoiClickListener poiClickListener = null;

  public MapViewWithOverlays(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }

  public MapViewWithOverlays(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public MapViewWithOverlays(Context context)
  {
    super(context);
    init();
  }

  protected void init()
  {
    String databasePath = Database.getDatabasePath(getContext());
    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databasePath,
        null);
    IConnection ldb = new AndroidConnection(db);
    appData = AppData.getInstance(ldb);
    db.close();

    super.init();

    initGestureDetector();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    super.onTouchEvent(event);
    gd.onTouchEvent(event);
    return true;
  }

  public void setMapFile(MapfileOpener opener, MapfileOpener openerWaldbrand) throws IOException,
      ClassNotFoundException
  {
    super.setMapFile(opener);

    setup(opener, openerWaldbrand);
  }

  private void setup(MapfileOpener opener, MapfileOpener openerWaldbrand)
  {
    float density = global.getDensity();

    if (labelDrawer != null) {
      removeOnDrawListener(labelDrawer);
    }
    if (scaleDrawer != null) {
      removeOnDrawListener(scaleDrawer);
    }

    try {
      labelDrawer = new LabelDrawerPoi(getContext(), this, density,
          appData.getSpatialIndexPois(), opener, openerWaldbrand);
      labelDrawer.setDrawDebugFrame(false);
      labelDrawer.setDrawDebugBoxes(false);
      addOnDrawListener(labelDrawer);
    } catch (IOException e) {
      Log.e("label", "error while creating label drawer", e);
    }

    scaleDrawer = new MapocadoScaleDrawer((int) Math.ceil(120 * density),
        margin * density, (margin + overlayTextSize + gap) * density,
        overlayFgStroke * density, overlayBgStroke * density,
        8 * density, overlayTextSize * density);
    addOnDrawListener(scaleDrawer);
  }

  public MapocadoScaleDrawer getScaleDrawer()
  {
    return scaleDrawer;
  }

  @Override
  public void setRenderConfig(MapRenderConfig mapRenderConfig)
  {
    super.setRenderConfig(mapRenderConfig);
    scaleDrawer.setRenderConfig(mapRenderConfig);

    RenderConfig renderConfig = new RenderConfig(mapRenderConfig,
        getContext());
    labelDrawer.setRenderConfig(renderConfig);
  }

  private final float magnification = 1.0f;

  @Override
  public void setMagnification(float magnification)
  {
    if (magnification == this.magnification) {
      return;
    }
    labelDrawer.setMagnification(magnification);
    super.setMagnification(magnification);
  }

  public LabelDrawerPoi getLabelDrawer()
  {
    return labelDrawer;
  }

  @Override
  public void destroy()
  {
    super.destroy();
    labelDrawer.destroy();
  }

  private void initGestureDetector()
  {
    gd = new GestureDetector(getContext(),
        new GestureDetector.SimpleOnGestureListener()
        {

          @Override
          public boolean onSingleTapConfirmed(MotionEvent e)
          {
            tap(e);
            return true;
          }

        });
  }

  public void setPoiClickListener(PoiClickListener listener)
  {
    poiClickListener = listener;
  }

  private void tap(MotionEvent e)
  {
    float x = e.getX();
    float y = e.getY();
    Poi poi = labelDrawer.getIcon(mapWindow, x, y);
    if (poi == null) {
      return;
    }
    String name = Waldbrand.getName(poi.getType());
    Toast.makeText(getContext(),
        String.format("%s [%.1f %.1f / %d %d]", name, x, y, poi.getLabel().x, poi.getLabel().y),
        LENGTH_SHORT).show();
    if (poiClickListener != null) {
      poiClickListener.onPoiClicked(poi);
    }
  }

}
