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

package de.topobyte.apps.viewer.label;

import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Typeface;

import de.topobyte.android.maps.utils.MagnificationSupport;
import de.topobyte.mapocado.android.rendering.StyleConversion;
import de.topobyte.mapocado.styles.labels.elements.DotLabel;
import de.topobyte.mapocado.styles.labels.elements.IconLabel;
import de.topobyte.mapocado.styles.labels.elements.Label;
import de.topobyte.mapocado.styles.labels.elements.LabelType;
import de.topobyte.mapocado.styles.labels.elements.PlainLabel;

public class LabelClass implements MagnificationSupport
{

  private float density;
  private float magnification;

  public LabelType type;

  public Paint paintTextFill;
  public Paint paintTextStroke;
  public Paint paintDotFill;
  public boolean hasDot;
  public int dotSize;
  public boolean hasIcon;
  public int iconSize;
  public LabelBoxConfig labelBoxConfig;
  public Label labelStyle;
  public int dy;

  public LabelClass(LabelType type, Label labelStyle, float magnification,
                    float density)
  {
    this.type = type;
    this.labelStyle = labelStyle;
    this.density = density;
    this.hasDot = labelStyle instanceof DotLabel;

    PlainLabel plain = (PlainLabel) labelStyle;

    Typeface family = StyleConversion.getFontFamily(plain.getFamily());
    int style = StyleConversion.getFontStyle(plain.getStyle());
    Typeface font = Typeface.create(family, style);

    int fg = StyleConversion.getColor(plain.getFg());
    int bg = StyleConversion.getColor(plain.getBg());

    paintTextFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintTextFill.setStyle(Style.FILL);
    paintTextFill.setTypeface(font);
    paintTextStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintTextStroke.setStyle(Style.STROKE);
    paintTextStroke.setStrokeCap(Cap.ROUND);
    paintTextStroke.setStrokeJoin(Join.ROUND);
    paintTextStroke.setTypeface(font);
    paintDotFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintDotFill.setStyle(Style.FILL);

    paintTextFill.setColor(fg);
    paintTextStroke.setColor(bg);

    if (labelStyle instanceof DotLabel) {
      DotLabel dot = (DotLabel) labelStyle;
      int dotColor = StyleConversion.getColor(dot.getDotFg());
      paintDotFill.setColor(dotColor);
    }

    update();
  }

  @Override
  public void setMagnification(float magnification)
  {
    this.magnification = magnification;
    update();
  }

  private void update()
  {
    PlainLabel plain = (PlainLabel) labelStyle;

    int textSize = (int) Math.ceil(plain.getFontSize() * magnification);
    int strokeWidth = (int) Math.ceil(plain.getStrokeWidth()
        * magnification);
    int border = (int) Math
        .ceil(plain.getStrokeWidth() * magnification / 2);

    paintTextFill.setTextSize(textSize);
    paintTextStroke.setTextSize(textSize);
    paintTextStroke.setStrokeWidth(strokeWidth);

    LabelBoxConfig lbc = new LabelBoxConfig(textSize, border);
    labelBoxConfig = lbc;

    if (labelStyle instanceof DotLabel) {
      DotLabel dot = (DotLabel) labelStyle;
      dotSize = (int) Math.ceil(dot.getRadius() * magnification);
      dy = -lbc.height + lbc.lowExtra - dotSize;
    } else if (labelStyle instanceof IconLabel) {
      IconLabel icon = (IconLabel) labelStyle;
      iconSize = Math.round(icon.getIconHeight() * density);
      dy = -lbc.height + lbc.lowExtra - iconSize / 2;
    } else {
      dy = -lbc.height / 2;
    }
  }

  public int getBoxWidth(String name)
  {
    float textLength = paintTextFill.measureText(name);
    return (int) Math.ceil(textLength + 2 * labelBoxConfig.border);
  }

}
