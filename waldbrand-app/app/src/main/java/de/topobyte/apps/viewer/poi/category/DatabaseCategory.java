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

package de.topobyte.apps.viewer.poi.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseCategory extends Category
{

  private final List<String> ids = new ArrayList<>();

  public DatabaseCategory(int nameId, String preferenceKey,
                          String... identifiers)
  {
    super(nameId, preferenceKey);
    Collections.addAll(ids, identifiers);
  }

  public List<String> getIdentifiers()
  {
    return ids;
  }

}
