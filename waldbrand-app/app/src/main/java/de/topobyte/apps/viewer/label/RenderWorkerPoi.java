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
import android.graphics.Bitmap;
import android.graphics.Canvas;

import de.topobyte.android.maps.utils.label.LabelDrawer;
import de.topobyte.android.maps.utils.label.RenderWorker;

public class RenderWorkerPoi extends RenderWorker<LabelClass>
{

  private final Context context;

  public RenderWorkerPoi(Context context,
                         LabelDrawer<?, LabelClass, ?> labelDrawer)
  {
    super(labelDrawer);
    this.context = context;
  }

  @Override
  protected Bitmap createTextImage(LabelClass labelClass, String name)
  {
    int width = labelClass.getBoxWidth(name);

    LabelBoxConfig lbc = labelClass.labelBoxConfig;
    Bitmap bitmap = Bitmap.createBitmap(width, lbc.height,
        Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bitmap);

    c.drawText(name, lbc.border, lbc.height - lbc.lowExtra - lbc.border,
        labelClass.paintTextStroke);
    c.drawText(name, lbc.border, lbc.height - lbc.lowExtra - lbc.border,
        labelClass.paintTextFill);

    return bitmap;
  }
}
