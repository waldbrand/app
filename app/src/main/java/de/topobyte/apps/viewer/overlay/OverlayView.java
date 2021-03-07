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
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.android.misc.utils.Toaster;
import de.topobyte.bvg.android.BvgButton;

public class OverlayView extends RelativeLayout
{

  private Toaster toaster;

  private float density;

  private List<BvgButton> buttons;

  private final int marginTop = 10;
  private final int marginBottom = 10;
  private final int marginRight = 10;

  private final int bw = 50;
  private final int bh = 50;
  private final float cr = 5;
  private final float padding = 5;

  private final float is = 35;

  private float dbw;
  private float dbh;
  private float dcr;

  private BvgButton btnClose;
  private BvgButton btnLayers;
  private BvgButton btnThemes;

  public OverlayView(Context context)
  {
    super(context);
    init();
  }

  public OverlayView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public OverlayView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }

  private void init()
  {
    toaster = new Toaster(getContext());

    density = getResources().getDisplayMetrics().density;

    dbw = bw * density;
    dbh = bh * density;
    dcr = cr * density;
    float dis = is * density;

    buttons = new ArrayList<>();

    btnClose = new BvgButton(getContext(), dbw, dbh, dcr,
        "buttons/right.bvg", dis, dis);
    btnLayers = new BvgButton(getContext(), dbw, dbh, dcr,
        "buttons/layers.bvg", dis, dis);
    btnThemes = new BvgButton(getContext(), dbw, dbh, dcr,
        "buttons/themes.bvg", dis, dis);

    buttons.add(btnClose);
    buttons.add(btnLayers);
    buttons.add(btnThemes);

    // for (int i = 0; i < 9; i++) {
    // BvgButton b = new BvgButton(getContext(), dbw, dbh, dcr,
    // "buttons/layers.bvg", dis, dis);
    // buttons.add(b);
    // }

    btnLayers.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        toaster.toastShort("select layers");
      }
    });
    btnThemes.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        toaster.toastShort("select theme");
      }
    });

    int paddingTop = Math.round(marginTop * density);
    int paddingRight = Math.round(marginRight * density);
    int paddingBottom = Math.round(marginBottom * density);
    setPadding(0, paddingTop, paddingRight, paddingBottom);
    int margin = Math.round(padding * density);

    for (int i = 0; i < buttons.size(); i++) {
      BvgButton button = buttons.get(i);
      LayoutParams params = new RelativeLayout.LayoutParams(
          android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
          android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
      button.setId(i + 1);
      if (i == 0) {
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
      } else {
        params.addRule(RelativeLayout.BELOW, i);
        params.setMargins(0, margin, 0, 0);
      }
      addView(button, params);
    }
  }

  public BvgButton getCloseButton()
  {
    return btnClose;
  }

  public BvgButton getThemesButton()
  {
    return btnThemes;
  }

  public BvgButton getLayersButton()
  {
    return btnLayers;
  }
}
