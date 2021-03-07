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

import de.topobyte.bvg.android.BvgToggleButton;

public class OverlayGps extends RelativeLayout
{
  private float density;

  private final int marginTop = 10;
  private final int marginLeft = 10;

  private final int bw = 50;
  private final int bh = 50;
  private final float cr = 5;

  private final float is = 35;

  private float dbw;
  private float dbh;
  private float dcr;

  private BvgToggleButton btnSnap;

  private void init()
  {
    density = getResources().getDisplayMetrics().density;

    dbw = bw * density;
    dbh = bh * density;
    dcr = cr * density;
    float dis = is * density;

    btnSnap = new BvgToggleButton(getContext(), dbw, dbh, dcr,
        "buttons/gps.bvg", dis, dis, false);

    int paddingTop = Math.round(marginTop * density);
    int paddingLeft = Math.round(marginLeft * density);
    setPadding(paddingLeft, paddingTop, 0, 0);

    LayoutParams params = new RelativeLayout.LayoutParams(
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    addView(btnSnap, params);
  }

  public OverlayGps(Context context)
  {
    super(context);
    init();
  }

  public OverlayGps(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public OverlayGps(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }

  public BvgToggleButton getSnapGpsButton()
  {
    return btnSnap;
  }
}
