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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.io.IOException;

import de.topobyte.adt.geo.BBox;
import de.topobyte.android.maps.utils.TextOverlayDrawer;
import de.topobyte.android.maps.utils.map.BaseMapView;
import de.topobyte.android.mapview.ImageManagerSourceRam;
import de.topobyte.android.mapview.ReferenceCountedBitmap;
import de.topobyte.apps.viewer.AppConstants;
import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.core.mapwindow.SteplessMapWindow;
import de.topobyte.jeography.core.viewbounds.BboxViewBounds;
import de.topobyte.mapocado.android.mapfile.MapfileOpener;
import de.topobyte.mapocado.android.style.MapRenderConfig;

public class MapView extends BaseMapView
{

  /*
   * Constructors
   */

  public MapView(Context context)
  {
    super(context);
    init();
  }

  public MapView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public MapView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }

  /*
   * Fields
   */

  protected Global global;

  private MapRenderConfig mapRenderConfig;

  private TextOverlayDrawer textOverlayDrawer;

  protected float overlayTextSize = 10;
  protected float overlayFgStroke = 1;
  protected float overlayBgStroke = 2;
  protected float margin = 5;

  private Paint paintReticle = new Paint(Paint.ANTI_ALIAS_FLAG);

  // initialization, called on construction

  protected void init()
  {
    /* start position */
    double lon = 6.6198, lat = 51.2297;

    int width = getWidth();
    int height = getHeight();

    /* setup */
    SteplessMapWindow mapWindow = new SteplessMapWindow(width, height, 16, lon, lat);
    mapWindow.setMinZoom(8);
    mapWindow.setMaxZoom(20);

    BBox bbox = new BBox(AppConstants.BBOX);
    mapWindow.setViewBounds(new BboxViewBounds(bbox));

    init(mapWindow);

    global = Global.getInstance(getContext());

    ImageManagerSourceRam<Tile, ReferenceCountedBitmap> imageManager = global.getImageManager();

    init(imageManager);

    textOverlayDrawer = new TextOverlayDrawer(overlayTextSize,
        overlayBgStroke, global.getDensity());

    paintReticle.setColor(0x99000000);
    paintReticle.setStrokeCap(Paint.Cap.ROUND);
    paintReticle.setStrokeJoin(Paint.Join.ROUND);
    paintReticle.setStyle(Paint.Style.STROKE);
    paintReticle.setStrokeWidth(2.0f);

    mapWindow.addZoomListener(() -> global.updateCacheSize(calculateCacheSize()));
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh)
  {
    super.onSizeChanged(w, h, oldw, oldh);
    global.updateCacheSize(calculateCacheSize());
  }

  /*
   * Drawing
   */

  protected boolean drawZoomLevel = false;
  protected boolean drawPosition = false;
  protected boolean drawReticle = false;

  public void setDrawZoomLevel(boolean drawZoomLevel)
  {
    this.drawZoomLevel = drawZoomLevel;
  }

  public void setDrawPosition(boolean drawPosition)
  {
    this.drawPosition = drawPosition;
  }

  public void setDrawReticle(boolean drawReticle)
  {
    this.drawReticle = drawReticle;
  }

  public void setDrawGrid(boolean drawGrid)
  {
    super.setDrawGrid(drawGrid);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);

    // draw reticle
    if (drawReticle) {
      drawReticle(canvas);
    }

    // draw overlay text at the top

    int line = 0;
    if (drawPosition) {
      double lon = mapWindow.getCenterLon();
      double lat = mapWindow.getCenterLat();
      String text = String.format("Kartenmitte (lon/lat): %f %f", lon, lat);
      textOverlayDrawer.drawTopLeft(canvas, text, margin, line++);
    }

    if (drawZoomLevel) {
      String text = String.format("Zoom: %f", mapWindow.getZoom());
      textOverlayDrawer.drawTopLeft(canvas, text, margin, line++);
    }

    // draw overlay text at the bottom

    String copyright = "Map data by OpenStreetMap, Landesbetrieb Forst Brandenburg";
    textOverlayDrawer.drawBottomLeft(canvas, copyright, margin, getHeight());
  }

  private void drawReticle(Canvas canvas)
  {
    int height = getHeight();
    int width = getWidth();
    int h2 = height / 2;
    int w2 = width / 2;
    float radius = 25 * global.getDensity();
    canvas.drawCircle(w2, h2, radius, paintReticle);
    canvas.drawLine(w2 - radius, h2, w2 + radius, h2, paintReticle);
    canvas.drawLine(w2, h2 - radius, w2, h2 + radius, paintReticle);
  }

  /*
   * Configuration
   */

  public MapRenderConfig getMapRenderConfig()
  {
    return mapRenderConfig;
  }

  public void setRenderConfig(MapRenderConfig mapRenderConfig)
  {
    this.mapRenderConfig = mapRenderConfig;
    setBackgroundColor(mapRenderConfig.getBackgroundColor());
    textOverlayDrawer.setBackgroundColor(mapRenderConfig.getOverlayOuter());
    textOverlayDrawer.setForegroundColor(mapRenderConfig.getOverlayInner());
    postInvalidate();
  }

  public void setMapFile(MapfileOpener opener) throws IOException,
      ClassNotFoundException
  {
    postInvalidate();
  }

  @Override
  public void longClick(float x, float y)
  {
    // do nothing at the moment
  }

}