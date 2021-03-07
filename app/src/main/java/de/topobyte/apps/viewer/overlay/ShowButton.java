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

package de.topobyte.apps.viewer.overlay;

import android.content.Context;
import android.util.AttributeSet;

import de.topobyte.bvg.android.BvgButton;

public class ShowButton extends BvgButton
{

  public ShowButton(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public ShowButton(Context context, float bw, float bh, float cr)
  {
    super(context, bw, bh, cr);
  }

  public ShowButton(Context context, float bw, float bh, float cr,
                    String iconPath, float iconWidth, float iconHeight)
  {
    super(context, bw, bh, cr, iconPath, iconWidth, iconHeight);
  }

  @Override
  protected void initPath(int width, int height)
  {
    float x1 = strokeWidth / 2;
    float y1 = strokeWidth / 2;
    float x2 = x1 + width;
    float y2 = y1 + height - strokeWidth;

    path.rewind();
    path.moveTo(x1 + cr, y1);
    path.lineTo(x2, y1);
    path.lineTo(x2, y2);
    path.lineTo(x1 + cr, y2);
    path.quadTo(x1, y2, x1, y2 - cr);
    path.lineTo(x1, y1 + cr);
    path.quadTo(x1, y1, x1 + cr, y1);
    path.close();
  }

}
