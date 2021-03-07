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
import android.widget.RelativeLayout;

import de.topobyte.bvg.android.BvgButton;

public class OverlayShower extends RelativeLayout
{

  private float density;
  private ShowButton button;

  private final int marginTop = 10;
  private final int marginRight = 0;

  private final int bw = 25;
  private final int bh = 50;
  private final float cr = 5;

  private void init()
  {
    density = getResources().getDisplayMetrics().density;

    float dbw = bw * density;
    float dbh = bh * density;
    float dcr = cr * density;

    button = new ShowButton(getContext(), dbw, dbh, dcr,
        "buttons/sidemenu.bvg", dbw, dbh);

    int paddingTop = Math.round(marginTop * density);
    int paddingRight = Math.round(marginRight * density);
    setPadding(0, paddingTop, paddingRight, 0);

    LayoutParams params = new RelativeLayout.LayoutParams(
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    addView(button, params);
  }

  public OverlayShower(Context context)
  {
    super(context);
    init();
  }

  public OverlayShower(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public OverlayShower(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }

  public BvgButton getButton()
  {
    return button;
  }

}
