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

package de.topobyte.apps.viewer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import de.waldbrandapp.R;

public class TriStateCheckBox extends ImageButton
{

  public enum ButtonState
  {
    NONE, SOME, ALL
  }

  private ButtonState state = ButtonState.NONE;

  private OnStateChangeListener onStateChangeListener;

  public TriStateCheckBox(Context context)
  {
    super(context);
    init();
  }

  public TriStateCheckBox(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public TriStateCheckBox(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }

  private void init()
  {
    syncImage();
  }

  public ButtonState getState()
  {
    return state;
  }

  public void setState(ButtonState state)
  {
    if (this.state == state) {
      return;
    }
    this.state = state;
    syncImage();
  }

  public interface OnStateChangeListener
  {
    void onStateChanged(TriStateCheckBox button, ButtonState state);
  }

  public void setOnStateChangeListener(OnStateChangeListener listener)
  {
    onStateChangeListener = listener;
  }

  public void toggle()
  {
    if (state == ButtonState.ALL) {
      state = ButtonState.NONE;
    } else {
      state = ButtonState.ALL;
    }
    syncImage();
    if (onStateChangeListener != null) {
      onStateChangeListener.onStateChanged(this, state);
    }
  }

  private void syncImage()
  {
    int attr;
    switch (state) {
      default:
      case NONE:
        attr = R.attr.icon_check_no;
        break;
      case ALL:
        attr = R.attr.icon_check_yes;
        break;
      case SOME:
        attr = R.attr.icon_check_half;
        break;
    }
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
