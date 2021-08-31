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

package de.topobyte.apps.viewer.coordinatesystems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoordinateSystemList
{

  private final List<CoordinateSystem> css = new ArrayList<>();

  public void add(CoordinateSystem cs)
  {
    css.add(cs);
  }

  public String[] getNamesArray()
  {
    String[] names = new String[css.size()];
    for (int i = 0; i < css.size(); i++) {
      CoordinateSystem cs = css.get(i);
      names[i] = cs.getName();
    }
    return names;
  }

  public String[] getKeysArray()
  {
    String[] keys = new String[css.size()];
    for (int i = 0; i < css.size(); i++) {
      CoordinateSystem cs = css.get(i);
      keys[i] = cs.getKey();
    }
    return keys;
  }

  public Set<String> getKeySet()
  {
    Set<String> keys = new HashSet<>();
    for (int i = 0; i < css.size(); i++) {
      CoordinateSystem cs = css.get(i);
      keys.add(cs.getKey());
    }
    return keys;
  }

  public Map<String, CoordinateSystem> getMapping()
  {
    Map<String, CoordinateSystem> mapping = new HashMap<>();
    for (int i = 0; i < css.size(); i++) {
      CoordinateSystem cs = css.get(i);
      mapping.put(cs.getKey(), cs);
    }
    return mapping;
  }

}
