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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.Locale;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.BaseStadtplan;
import de.topobyte.apps.viewer.label.LabelDrawerPreferenceAbstraction;
import de.topobyte.apps.viewer.label.LabelMode;
import de.topobyte.apps.viewer.poi.Categories;
import de.topobyte.apps.viewer.widget.EmbeddedExpandableListAdapter;

public class DrawerListAdapter extends EmbeddedExpandableListAdapter
{

  private BaseStadtplan stadtplan;

  public DrawerListAdapter(Context context, Categories info)
  {
    super(context, new LabelCategoriesListAdapter(context, info));
  }

  @Override
  public int getNumEntriesBefore()
  {
    return 1;
  }

  @Override
  public int getNumEntriesAfter()
  {
    return 0;
  }

  public void setStadtplan(BaseStadtplan stadtplan)
  {
    this.stadtplan = stadtplan;
    ((LabelCategoriesListAdapter) listAdapter).setStadtplan(stadtplan);
  }

  @Override
  public View getSpecialChildView(int groupPosition, int childPosition,
                                  boolean isLastChild, View convertView, ViewGroup parent)
  {

    LayoutInflater inflater = (LayoutInflater) this.context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    int n = groupPosition == 0 ? childPosition : getNumEntriesBefore()
        + childPosition;

    int layout;
    if (n == 0) {
      layout = R.layout.row_layout_drawer_list_extra_top;
    } else {
      layout = R.layout.row_layout_drawer_list_extra_text;
    }
    convertView = inflater.inflate(layout, parent, false);

    TextView textView = convertView.findViewById(R.id.text);

    if (n == 0) {
      String labels = context.getString(R.string.labels);
      textView.setText(labels.toUpperCase(Locale.getDefault()));

      LabelDrawerPreferenceAbstraction pa = new LabelDrawerPreferenceAbstraction(
          context);
      LabelMode labelMode = pa.determineLabelMode();
      boolean on = labelMode == LabelMode.BY_CONFIG;

      CompoundButton button = convertView
          .findViewById(R.id.button);
      button.setChecked(on);
      button.setOnCheckedChangeListener(new OnCheckedChangeListener()
      {

        @Override
        public void onCheckedChanged(CompoundButton button,
                                     boolean state)
        {
          setLabelDrawerMode(state);
        }
      });
    }

    return convertView;
  }

  @Override
  public boolean isSpecialChildSelectable(int position)
  {
    return false;
  }

  protected void setLabelDrawerMode(boolean on)
  {
    LabelMode mode = on ? LabelMode.BY_CONFIG : LabelMode.MINIMAL;
    LabelDrawerPreferenceAbstraction pa = new LabelDrawerPreferenceAbstraction(
        context);
    pa.storeLabelMode(mode);

    stadtplan.updateLayers();
  }
}
