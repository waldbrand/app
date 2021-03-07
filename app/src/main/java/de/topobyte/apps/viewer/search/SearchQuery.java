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

import android.graphics.Point;

import de.topobyte.nomioc.luqe.dao.MatchMode;

public class SearchQuery
{

  private String query;
  private MatchMode matchMode;
  private ResultOrder resultOrder;
  private Point position;
  private TypeSelection typeSelection;

  public SearchQuery(String query, MatchMode matchMode,
                     ResultOrder resultOrder, Point position, TypeSelection typeSelection)
  {
    this.query = query;
    this.matchMode = matchMode;
    this.resultOrder = resultOrder;
    this.position = position;
    this.typeSelection = typeSelection;
  }

  public String getQuery()
  {
    return query;
  }

  public MatchMode getMatchMode()
  {
    return matchMode;
  }

  public ResultOrder getResultOrder()
  {
    return resultOrder;
  }

  public Point getPosition()
  {
    return position;
  }

  public TypeSelection getTypeSelection()
  {
    return typeSelection;
  }

  @Override
  public boolean equals(Object other)
  {
    if (!(other instanceof SearchQuery)) {
      return false;
    }
    SearchQuery otherQuery = (SearchQuery) other;
    return matchMode == otherQuery.matchMode
        && query.equals(otherQuery.query)
        && resultOrder == otherQuery.resultOrder
        && position.equals(otherQuery.position)
        && typeSelection.equals(otherQuery.typeSelection);
  }

  @Override
  public String toString()
  {
    return "Query: '" + query + "', matchMode: " + matchMode
        + ", resultOrder: " + resultOrder + ", position: " + position;
  }
}
