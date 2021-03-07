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

package de.topobyte.apps.viewer.location;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;

import de.topobyte.android.maps.utils.OnDrawListener;
import de.topobyte.android.maps.utils.map.BaseMapView;
import de.topobyte.jeography.core.mapwindow.MapWindow;
import de.topobyte.mapocado.android.style.MapRenderConfig;
import de.topobyte.util.maps.MercatorUtil;

public class LocationOverlay implements OnDrawListener<BaseMapView>
{

  private final MapWindow mapWindow;
  private final float density;

  private boolean valid = false;
  private boolean enabled = true;

  private float minRadius = 5;
  private float maxRadius = 100;

  private Location location;
  private float radius = 10;
  private float x = 100;
  private float y = 100;

  private final Paint stroke, fill;

  public LocationOverlay(MapWindow mapWindow, float density)
  {
    this.mapWindow = mapWindow;
    this.density = density;
    Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    stroke.setStyle(Paint.Style.STROKE);
    stroke.setColor(0xFFFF0000);
    stroke.setStrokeWidth(density <= 1 ? 1 : density);

    Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    fill.setStyle(Paint.Style.FILL);
    fill.setColor(0x60FF0000);

    this.stroke = stroke;
    this.fill = fill;

    // only if density is >1, increase the minimum radius so that the
    // location circle won't be too tiny on high resolution screens and
    // also allow an increased maxRadius
    if (density > 1) {
      minRadius = minRadius * density;
      maxRadius = maxRadius * density;
    }
  }

  public void setLocation(Location location)
  {
    this.location = location;

    if (location == null) {
      valid = false;
      return;
    }
    valid = true;

    updateValues();
  }

  private void updateValues()
  {
    // find out the radius in pixels

    float metersPerPixel = (float) MercatorUtil.calculateGroundResolution(
        mapWindow.getCenterLat(), mapWindow.getWorldsizePixels());
    float accuracy = location.getAccuracy();
    float meters = accuracy;
    float pixels = meters / metersPerPixel;
    radius = pixels * density;

    if (radius < minRadius) {
      radius = minRadius;
    }
    if (radius > maxRadius) {
      radius = maxRadius;
    }

    // find position
    double lon = location.getLongitude();
    double lat = location.getLatitude();
    this.x = (float) mapWindow.longitudeToX(lon);
    this.y = (float) mapWindow.latitudeToY(lat);
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public boolean isValid()
  {
    return valid;
  }

  public Location getLocation()
  {
    return location;
  }

  @Override
  public void onDraw(BaseMapView mapView, Canvas canvas)
  {
    if (!valid) {
      return;
    }
    if (!enabled) {
      return;
    }

    updateValues();

    canvas.drawCircle(x, y, radius, fill);
    canvas.drawCircle(x, y, radius, stroke);
  }

  public void setColors(MapRenderConfig renderConfig)
  {
    fill.setColor(renderConfig.getOverlayGpsInner());
    stroke.setColor(renderConfig.getOverlayGpsOuter());
  }

}
