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

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.widget.TextWatcherAdapter.TextWatcherListener;

/**
 * To change clear icon, set
 * <p/>
 * <pre>
 * android:drawableRight="@drawable/custom_icon"
 * </pre>
 */
public class Clearable implements OnTouchListener, TextWatcherListener
{

  public static void setClearable(EditText edit)
  {
    new Clearable(edit);
  }

  private final EditText edit;
  private Drawable icon;

  private Clearable(EditText edit)
  {
    this.edit = edit;
    edit.setOnTouchListener(this);
    edit.addTextChangedListener(new TextWatcherAdapter(edit, this));

    icon = edit.getCompoundDrawables()[2];
    if (icon == null) {
      TypedArray a = edit
          .getContext()
          .getTheme()
          .obtainStyledAttributes(R.style.MainTheme,
              new int[]{R.attr.clearcross});
      int attributeResourceId = a.getResourceId(0, 0);
      icon = edit.getResources().getDrawable(attributeResourceId);
    }
    icon.setBounds(0, 0, icon.getIntrinsicWidth(),
        icon.getIntrinsicHeight());

    boolean visible = !isEmpty(edit.getText());
    setClearIconVisible(visible);
  }

  @Override
  public boolean onTouch(View v, MotionEvent event)
  {
    if (edit.getCompoundDrawables()[2] != null) {
      boolean tappedIcon = event.getX() > (edit.getWidth()
          - edit.getPaddingRight() - icon.getIntrinsicWidth());
      // do not consume events, i.e. return false, to get event through to
      // the edit text so that the soft keyboard appears anyway
      if (event.getAction() == MotionEvent.ACTION_UP) {
        if (tappedIcon) {
          edit.setText("");
          return false;
        }
      }
    }
    return false;
  }

  @Override
  public void onTextChanged(EditText view, String text)
  {
    setClearIconVisible(!isEmpty(text));
  }

  private static boolean isEmpty(CharSequence str)
  {
    return str == null || str.length() == 0;
  }

  protected void setClearIconVisible(boolean visible)
  {
    Drawable x = visible ? icon : null;
    edit.setCompoundDrawables(edit.getCompoundDrawables()[0],
        edit.getCompoundDrawables()[1], x,
        edit.getCompoundDrawables()[3]);
  }
}
