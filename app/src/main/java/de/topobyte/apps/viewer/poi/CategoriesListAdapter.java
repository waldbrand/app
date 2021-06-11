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

package de.topobyte.apps.viewer.poi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.slimjars.dist.gnu.trove.set.TIntSet;

import java.util.List;

import de.waldbrandapp.R;
import de.topobyte.apps.viewer.poi.category.Category;
import de.topobyte.apps.viewer.widget.NormalCheckBox;
import de.topobyte.apps.viewer.widget.TriStateCheckBox;
import de.topobyte.apps.viewer.widget.TriStateCheckBox.ButtonState;

public abstract class CategoriesListAdapter extends BaseExpandableListAdapter
{

  private final Context context;

  private final List<Group> groups;
  private final TIntSet specialIndices;

  protected SharedPreferences prefs;

  private final String prefix;

  public CategoriesListAdapter(Context context, Categories info, String prefix)
  {
    this.context = context;
    this.prefix = prefix;
    this.groups = info.getGroups();
    this.specialIndices = info.getSpecialIndices();
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Override
  public Object getChild(int groupPosition, int childPosition)
  {
    return groups.get(groupPosition).getChildren().get(childPosition);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition)
  {
    return childPosition;
  }

  @Override
  public View getChildView(final int groupPosition, final int childPosition,
                           boolean isLastChild, View convertView, ViewGroup parent)
  {
    final Category category = (Category) getChild(groupPosition,
        childPosition);

    boolean special = specialIndices.contains(groupPosition);
    int number = special ? 2 : 1;

    if (convertView == null || convertView.getTag() == null
        || !convertView.getTag().equals(number)) {
      Log.i("children", "create new? yes");

      LayoutInflater inflater = (LayoutInflater) this.context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      if (specialIndices.contains(groupPosition)) {
        convertView = inflater.inflate(
            R.layout.row_layout_drawer_list_item_toplevel, parent,
            false);
      } else {
        convertView = inflater.inflate(
            R.layout.row_layout_drawer_list_item, parent, false);
      }
      convertView.setTag(number);
    } else {
      Log.i("children", "create new? no");
    }

    TextView txtListChild = convertView
        .findViewById(R.id.lblListItem);
    txtListChild.setText(category.getNameId());

    final NormalCheckBox checkbox = convertView
        .findViewById(R.id.chkListItem);
    checkbox.setFocusable(false);

    checkbox.setOnCheckedChangeListener(null);

    boolean checked = prefs.getBoolean(getPreferenceKey(category), true);
    Log.i("categories", "preference '" + getPreferenceKey(category) + "': "
        + checked);
    checkbox.setChecked(checked);

    checkbox.setOnCheckedChangeListener(
        (button, newChecked) -> itemCheckedChanged(groupPosition, childPosition, category,
            checkbox.isChecked()));

    checkbox.setOnClickListener(button -> {
      // empty listener to allow for click sound
    });

    return convertView;
  }

  @Override
  public int getChildrenCount(int groupPosition)
  {
    return groups.get(groupPosition).getChildren().size();
  }

  @Override
  public Object getGroup(int groupPosition)
  {
    return groups.get(groupPosition);
  }

  @Override
  public int getGroupCount()
  {
    return groups.size();
  }

  @Override
  public long getGroupId(int groupPosition)
  {
    return groupPosition;
  }

  @Override
  public View getGroupView(final int groupPosition, boolean isExpanded,
                           View convertView, ViewGroup parent)
  {
    final Group group = (Group) getGroup(groupPosition);
    Log.i("groups", "create new? " + (convertView == null));

    boolean special = specialIndices.contains(groupPosition);
    int number = special ? 2 : 1;

    if (convertView == null || !convertView.getTag().equals(number)) {
      LayoutInflater inflater = (LayoutInflater) this.context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      if (special) {
        convertView = inflater.inflate(
            R.layout.row_layout_drawer_list_group_empty, parent,
            false);
      } else {
        convertView = inflater.inflate(
            R.layout.row_layout_drawer_list_group, parent, false);
      }
      convertView.setTag(number);
    }

    if (special) {
      convertView.setVisibility(View.GONE);
    } else {
      convertView.setVisibility(View.VISIBLE);

      int headerTitle = group.getTitleId();
      TextView lblListHeader = convertView
          .findViewById(R.id.lblListHeader);
      lblListHeader.setTypeface(null, Typeface.BOLD);
      lblListHeader.setText(headerTitle);

      final TriStateCheckBox checkbox = convertView
          .findViewById(R.id.chkListItem);
      checkbox.setFocusable(false);

      checkbox.setOnStateChangeListener(null);

      ButtonState state = groupState(group);
      checkbox.setState(state);

      checkbox.setOnStateChangeListener(
          (button, newState) -> groupClicked(groupPosition, group, newState));

      checkbox.setOnClickListener(button -> {
        // empty listener to allow for click sound
      });
    }

    return convertView;
  }

  @Override
  public int getGroupType(int groupPosition)
  {
    if (specialIndices.contains(groupPosition)) {
      return 1;
    }
    return 0;
  }

  @Override
  public int getGroupTypeCount()
  {
    return 2;
  }

  @Override
  public int getChildType(int groupPosition, int childPosition)
  {
    if (specialIndices.contains(groupPosition)) {
      return 1;
    }
    return 0;
  }

  @Override
  public int getChildTypeCount()
  {
    return 2;
  }

  @Override
  public boolean hasStableIds()
  {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition)
  {
    return true;
  }

  private ButtonState groupState(Group group)
  {
    boolean yesSeen = false;
    boolean noSeen = false;
    List<Category> categories = group.getChildren();
    for (Category category : categories) {
      boolean checked = prefs
          .getBoolean(getPreferenceKey(category), true);
      if (checked) {
        yesSeen = true;
      } else {
        noSeen = true;
      }
      if (yesSeen && noSeen) {
        return ButtonState.SOME;
      }
    }
    if (yesSeen) {
      return ButtonState.ALL;
    }
    return ButtonState.NONE;
  }

  protected void groupClicked(int groupPosition, Group group,
                              ButtonState state)
  {
    Log.i("groups", "group check: " + groupPosition);
    Editor editor = prefs.edit();
    for (Category category : group.getChildren()) {
      if (state == ButtonState.NONE) {
        editor.putBoolean(getPreferenceKey(category), false);
      } else {
        editor.putBoolean(getPreferenceKey(category), true);
      }
    }
    editor.commit();
    notifyDataSetChanged();
  }

  protected void itemCheckedChanged(int groupPosition, int childPosition,
                                    Category category, boolean checked)
  {
    Log.i("children", "check: " + groupPosition + ", " + childPosition);
    Editor editor = prefs.edit();
    editor.putBoolean(getPreferenceKey(category), checked);
    editor.commit();

    notifyDataSetChanged();
  }

  private String getPreferenceKey(Category category)
  {
    return prefix + category.getPreferenceKey();
  }

}
