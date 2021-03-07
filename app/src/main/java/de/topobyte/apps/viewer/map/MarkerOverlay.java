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

import android.graphics.Canvas;
import android.util.Log;

import org.locationtech.jts.geom.Coordinate;

import java.io.File;
import java.io.IOException;

import de.topobyte.android.maps.utils.OnDrawListener;
import de.topobyte.android.maps.utils.map.BaseMapView;
import de.topobyte.bvg.BvgAndroidPainter;
import de.topobyte.bvg.BvgIO;
import de.topobyte.bvg.BvgImage;
import de.topobyte.jeography.core.mapwindow.MapWindow;
import de.topobyte.mapocado.android.style.MapRenderConfig;

public class MarkerOverlay implements OnDrawListener<BaseMapView>
{

  private final float density;

  private boolean enabled = true;
  private Coordinate c;

  private BvgImage image = null;
  private float scale = 0;
  private float width = 0;
  private float height = 0;

  public MarkerOverlay(float density)
  {
    this.density = density;
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setMarkerPosition(Coordinate c)
  {
    this.c = c;
  }

  @Override
  public void onDraw(BaseMapView mapView, Canvas canvas)
  {
    if (!enabled || image == null || c == null) {
      return;
    }

    MapWindow mapWindow = mapView.getMapWindow();
    double x = mapWindow.longitudeToX(c.x);
    double y = mapWindow.latitudeToY(c.y);

    float px = (float) (x - width / 2f);
    float py = (float) (y - height / 2f);
    BvgAndroidPainter.draw(canvas, image, px, py, scale, scale, scale);
  }

  public void setMarker(MapRenderConfig renderConfig)
  {
    File file = renderConfig.getSymbol("marker.bvg");
    try {
      image = BvgIO.read(file);
      height = 80 * density;
      scale = (float) (height / image.getHeight());
      width = (float) (image.getWidth() * scale);
    } catch (IOException e) {
      Log.w("marker", "unable to load marker image", e);
    }
  }
}
