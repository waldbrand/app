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
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

import de.waldbrandapp.R;

public abstract class EmbeddedExpandableListAdapter extends
    BaseExpandableListAdapter
{

  protected Context context;
  protected BaseExpandableListAdapter listAdapter;

  private final Object groupObject0 = new Object();
  private final Object groupObject1 = new Object();
  private final List<Object> childObjects0 = new ArrayList<>();
  private final List<Object> childObjects1 = new ArrayList<>();

  public EmbeddedExpandableListAdapter(Context context,
                                       BaseExpandableListAdapter listAdapter)
  {
    this.context = context;
    this.listAdapter = listAdapter;
    for (int i = 0; i < getNumEntriesBefore(); i++) {
      childObjects0.add(new Object());
    }
    for (int i = 0; i < getNumEntriesAfter(); i++) {
      childObjects1.add(new Object());
    }
    listAdapter.registerDataSetObserver(new DataSetObserver()
    {

      @Override
      public void onChanged()
      {
        notifyDataSetChanged();
      }

      @Override
      public void onInvalidated()
      {
        notifyDataSetInvalidated();
      }
    });
  }

  public abstract int getNumEntriesBefore();

  public abstract int getNumEntriesAfter();

  private int getNumExtraEntries()
  {
    return getNumEntriesBefore() + getNumEntriesAfter();
  }

  public abstract View getSpecialChildView(final int groupPosition,
                                           final int childPosition, boolean isLastChild,
                                           View convertView,
                                           ViewGroup parent);

  public abstract boolean isSpecialChildSelectable(int position);

  @Override
  public Object getChild(int groupPosition, int childPosition)
  {
    if (groupPosition == 0) {
      return childObjects0.get(childPosition);
    } else if (groupPosition > listAdapter.getGroupCount()) {
      return childObjects1.get(childPosition);
    } else {
      return listAdapter.getChild(groupPosition - 1, childPosition);
    }
  }

  @Override
  public long getChildId(int groupPosition, int childPosition)
  {
    return childPosition;
  }

  @Override
  public int getChildrenCount(int groupPosition)
  {
    if (groupPosition == 0) {
      return getNumEntriesBefore();
    } else if (groupPosition > listAdapter.getGroupCount()) {
      return getNumEntriesAfter();
    } else {
      return listAdapter.getChildrenCount(groupPosition - 1);
    }
  }

  @Override
  public Object getGroup(int groupPosition)
  {
    if (groupPosition == 0) {
      return groupObject0;
    } else if (groupPosition > listAdapter.getGroupCount()) {
      return groupObject1;
    } else {
      return listAdapter.getGroup(groupPosition - 1);
    }
  }

  @Override
  public long getGroupId(int groupPosition)
  {
    return groupPosition;
  }

  @Override
  public int getGroupCount()
  {
    return 2 + listAdapter.getGroupCount();
  }

  @Override
  public View getGroupView(final int groupPosition, boolean isExpanded,
                           View convertView, ViewGroup parent)
  {
    boolean special = groupPosition == 0
        || groupPosition > listAdapter.getGroupCount();

    if (!special) {
      return listAdapter.getGroupView(groupPosition - 1, isExpanded,
          convertView, parent);
    }

    int number = 0;
    if (convertView == null || !convertView.getTag().equals(number)) {
      LayoutInflater inflater = (LayoutInflater) this.context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(
          R.layout.row_layout_drawer_list_group_empty, parent, false);
      convertView.setTag(number);
    }
    convertView.setVisibility(View.GONE);
    return convertView;
  }

  @Override
  public View getChildView(final int groupPosition, final int childPosition,
                           boolean isLastChild, View convertView, ViewGroup parent)
  {
    boolean special = groupPosition == 0
        || groupPosition > listAdapter.getGroupCount();

    if (!special) {
      return listAdapter.getChildView(groupPosition - 1, childPosition,
          isLastChild, convertView, parent);
    }

    return getSpecialChildView(groupPosition, childPosition, isLastChild,
        convertView, parent);
  }

  @Override
  public int getGroupType(int groupPosition)
  {
    if (groupPosition == 0 || groupPosition > listAdapter.getGroupCount()) {
      return 0;
    }
    return 1 + listAdapter.getGroupType(groupPosition);
  }

  @Override
  public int getGroupTypeCount()
  {
    return 1 + listAdapter.getGroupTypeCount();
  }

  @Override
  public int getChildType(int groupPosition, int childPosition)
  {
    if (groupPosition == 0) {
      return childPosition;
    } else if (groupPosition > listAdapter.getGroupCount()) {
      return getNumEntriesBefore() + childPosition;
    } else {
      return listAdapter.getChildType(groupPosition - 1, childPosition);
    }
  }

  @Override
  public int getChildTypeCount()
  {
    return getNumExtraEntries() + listAdapter.getChildTypeCount();
  }

  @Override
  public boolean hasStableIds()
  {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition)
  {
    if (groupPosition == 0) {
      return isSpecialChildSelectable(childPosition);
    } else if (groupPosition > listAdapter.getGroupCount()) {
      return isSpecialChildSelectable(getNumEntriesBefore()
          + childPosition);
    } else {
      return listAdapter.isChildSelectable(groupPosition - 1,
          childPosition);
    }
  }

}
