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

package de.topobyte.apps.viewer.search.widget;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public abstract class EnumMenu<T extends Enum<?>>
{

  private int[] ids;
  private T[] values;
  private PopupMenu p;

  public EnumMenu(Context context, View v, int menuResource, T value,
                  int[] ids, T[] values)
  {
    this.ids = ids;
    this.values = values;
    int selected = -1;
    for (int i = 0; i < ids.length; i++) {
      if (value == values[i]) {
        selected = ids[i];
      }
    }

    p = new PopupMenu(context, v);
    p.getMenuInflater().inflate(menuResource, p.getMenu());
    Menu menu = p.getMenu();

    menu.findItem(selected).setChecked(true);

    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
    {

      @Override
      public boolean onMenuItemClick(MenuItem item)
      {
        int id = item.getItemId();
        for (int i = 0; i < EnumMenu.this.ids.length; i++) {
          if (id == EnumMenu.this.ids[i]) {
            clicked(EnumMenu.this.values[i]);
            return true;
          }
        }
        return false;
      }

    });
  }

  public void show()
  {
    p.show();
  }

  protected abstract void clicked(T value);

}
