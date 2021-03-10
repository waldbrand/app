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

package de.topobyte.apps.viewer.drawer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.slimjars.dist.gnu.trove.set.TIntSet;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.BaseStadtplan;
import de.topobyte.apps.viewer.poi.Categories;
import de.topobyte.apps.viewer.widget.EmbeddedList;
import de.topobyte.apps.viewer.widget.NormalCheckBox;

public class DrawerList extends EmbeddedList
{

  public DrawerList(Context context)
  {
    super(context);
    init(context);
  }

  public DrawerList(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }

  public DrawerList(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init(context);
  }

  private Categories info;

  private BaseStadtplan stadtplan;

  private DrawerListAdapter adapter;

  private void init(Context context)
  {
    info = Categories.getLabelInstance();

    adapter = new DrawerListAdapter(context, info);
    setAdapter(adapter);

    final TIntSet specialIndices = info.getSpecialIndices();
    for (int groupIndex : specialIndices.toArray()) {
      expandGroup(groupIndex + 1);
    }

    setOnGroupClickListener((parent, view, groupPosition, id) -> {
      if (specialIndices.contains(groupPosition - 1)) {
        return true;
      } else {
        return false;
      }
    });

    setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
      if (groupPosition > 0 && groupPosition < adapter.getGroupCount() - 1) {
        NormalCheckBox checkbox = v.findViewById(R.id.chkListItem);
        checkbox.toggle();
      }
      return true;
    });
  }

  public void setStadtplan(BaseStadtplan stadtplan)
  {
    this.stadtplan = stadtplan;
    adapter.setStadtplan(stadtplan);
  }

  protected void updateLayers()
  {
    stadtplan.updateLayers();
  }

  public void selectAll()
  {
    Log.i("drawer", "selecting all");
    info.pickAll(getContext());
    adapter.notifyDataSetInvalidated();
    stadtplan.updateLayers();
  }

  public void selectNone()
  {
    Log.i("drawer", "selecting none");
    info.pickNone(getContext());
    adapter.notifyDataSetInvalidated();
    stadtplan.updateLayers();
  }

}
