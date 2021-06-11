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

import de.topobyte.apps.viewer.Stadtplan;
import de.topobyte.apps.viewer.poi.Categories;
import de.topobyte.apps.viewer.poi.CategoriesListAdapter;
import de.topobyte.apps.viewer.poi.Group;
import de.topobyte.apps.viewer.poi.category.Category;
import de.topobyte.apps.viewer.widget.TriStateCheckBox.ButtonState;

public class LabelCategoriesListAdapter extends CategoriesListAdapter
{

  private Stadtplan stadtplan;

  public LabelCategoriesListAdapter(Context context, Categories info)
  {
    super(context, info, Categories.PREF_PREFIX_LABELS);
  }

  public void setStadtplan(Stadtplan stadtplan)
  {
    this.stadtplan = stadtplan;
  }

  protected void updateLayers()
  {
    stadtplan.updateLayers();
  }

  @Override
  protected void groupClicked(int groupPosition, Group group,
                              ButtonState state)
  {
    super.groupClicked(groupPosition, group, state);

    updateLayers();
  }

  @Override
  protected void itemCheckedChanged(int groupPosition, int childPosition,
                                    Category category, boolean checked)
  {
    super.itemCheckedChanged(groupPosition, childPosition, category,
        checked);

    updateLayers();
  }
}
