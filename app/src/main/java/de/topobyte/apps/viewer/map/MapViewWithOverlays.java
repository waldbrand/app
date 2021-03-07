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

package de.topobyte.apps.viewer.map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;

import de.topobyte.apps.viewer.AppData;
import de.topobyte.apps.viewer.Database;
import de.topobyte.apps.viewer.label.LabelDrawerPoi;
import de.topobyte.apps.viewer.label.RenderConfig;
import de.topobyte.luqe.android.AndroidConnection;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.mapocado.android.mapfile.MapFileOpener;
import de.topobyte.mapocado.android.style.MapRenderConfig;

public class MapViewWithOverlays extends MapView
{

  private AppData appData;

  private LabelDrawerPoi labelDrawer;
  private SulfurScaleDrawer scaleDrawer;

  private final float gap = 5;

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
  }

  @Override
  public void setMapFile(MapFileOpener opener) throws IOException,
      ClassNotFoundException
  {
    super.setMapFile(opener);

    setup(opener);
  }

  private void setup(MapFileOpener opener)
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
          appData.getSpatialIndexPois(), opener);
      labelDrawer.setDrawDebugFrame(false);
      labelDrawer.setDrawDebugBoxes(false);
      addOnDrawListener(labelDrawer);
    } catch (IOException e) {
      Log.e("label", "error while creating label drawer", e);
    }

    scaleDrawer = new SulfurScaleDrawer((int) Math.ceil(120 * density),
        margin * density, (margin + overlayTextSize + gap) * density,
        overlayFgStroke * density, overlayBgStroke * density,
        8 * density, overlayTextSize * density);
    addOnDrawListener(scaleDrawer);
  }

  public SulfurScaleDrawer getScaleDrawer()
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

}
