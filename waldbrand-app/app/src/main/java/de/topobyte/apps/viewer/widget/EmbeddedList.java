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
import android.util.AttributeSet;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class EmbeddedList extends ExpandableListView
{

  public EmbeddedList(Context context)
  {
    super(context);
  }

  public EmbeddedList(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public EmbeddedList(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
  }

  @Override
  public void setAdapter(ExpandableListAdapter adapter)
  {
    super.setAdapter(adapter);

    expandGroup(0);
    expandGroup(adapter.getGroupCount() - 1);
  }

}
