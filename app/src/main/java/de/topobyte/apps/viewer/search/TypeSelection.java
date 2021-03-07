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

package de.topobyte.apps.viewer.search;

import com.slimjars.dist.gnu.trove.set.TIntSet;

public class TypeSelection
{

  private boolean includeStreets = true;

  private PoiTypeSelection typeSelection = PoiTypeSelection.ALL;

  private TIntSet typeIds = null;

  public TypeSelection(boolean includeStreets,
                       PoiTypeSelection typeSelection, TIntSet typeIds)
  {
    this.includeStreets = includeStreets;
    this.typeSelection = typeSelection;
    this.typeIds = typeIds;
  }

  public boolean isIncludeStreets()
  {
    return includeStreets;
  }

  public PoiTypeSelection getTypeSelection()
  {
    return typeSelection;
  }

  public TIntSet getTypeIds()
  {
    return typeIds;
  }

  @Override
  public boolean equals(Object other)
  {
    if (!(other instanceof TypeSelection)) {
      return false;
    }
    TypeSelection otherS = (TypeSelection) other;
    return includeStreets == otherS.includeStreets
        && typeSelection == otherS.typeSelection
        && typeIds.equals(otherS.typeIds);
  }
}
