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

package de.topobyte.apps.viewer.search.categories;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import com.slimjars.dist.gnu.trove.set.TIntSet;

import de.topobyte.apps.viewer.poi.Categories;
import de.topobyte.apps.viewer.widget.NormalCheckBox;
import de.waldbrandapp.R;

public class CategoryList extends ExpandableListView
{

  public CategoryList(Context context)
  {
    super(context);
    init(context);
  }

  public CategoryList(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }

  public CategoryList(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init(context);
  }

  private Categories info;

  private SearchCategoriesListAdapter adapter;

  private void init(Context context)
  {
    info = Categories.getSearchInstance();

    adapter = new SearchCategoriesListAdapter(context, info);

    setAdapter(adapter);

    final TIntSet specialIndices = info.getSpecialIndices();
    for (int groupIndex : specialIndices.toArray()) {
      expandGroup(groupIndex);
    }

    setOnGroupClickListener((parent, view, groupPosition, id) -> {
      if (specialIndices.contains(groupPosition)) {
        return true;
      } else {
        return false;
      }
    });

    setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

      NormalCheckBox checkbox = v.findViewById(R.id.chkListItem);
      checkbox.toggle();

      return true;
    });
  }

  public SearchCategoriesListAdapter getCategoriesAdapter()
  {
    return adapter;
  }
}
