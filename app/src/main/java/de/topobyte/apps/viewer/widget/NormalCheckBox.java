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

package de.topobyte.apps.viewer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import de.waldbrandapp.R;

public class NormalCheckBox extends ImageButton
{

  private boolean checked = false;

  private OnCheckedChangeListener onCheckedChangeListener;

  public NormalCheckBox(Context context)
  {
    super(context);
    init();
  }

  public NormalCheckBox(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public NormalCheckBox(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }

  private void init()
  {
    syncImage();
  }

  public boolean isChecked()
  {
    return checked;
  }

  public void setChecked(boolean checked)
  {
    if (this.checked == checked) {
      return;
    }
    this.checked = checked;
    syncImage();
  }

  public interface OnCheckedChangeListener
  {
    void onCheckedChanged(NormalCheckBox button, boolean checked);
  }

  public void setOnCheckedChangeListener(OnCheckedChangeListener listener)
  {
    onCheckedChangeListener = listener;
  }

  public void toggle()
  {
    checked = !checked;
    syncImage();
    if (onCheckedChangeListener != null) {
      onCheckedChangeListener.onCheckedChanged(this, checked);
    }
  }

  private void syncImage()
  {
    int attr = checked ? R.attr.icon_check_yes : R.attr.icon_check_no;
    TypedArray a = getContext().getTheme().obtainStyledAttributes(
        R.style.MainTheme, new int[]{attr});
    int drawableId = a.getResourceId(0, 0);
    setImageResource(drawableId);
  }

  @Override
  public boolean performClick()
  {
    toggle();
    return super.performClick();
  }

}
