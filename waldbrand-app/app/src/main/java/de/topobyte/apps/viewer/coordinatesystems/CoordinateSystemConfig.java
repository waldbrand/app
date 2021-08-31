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

import java.util.Map;
import java.util.Set;

public class CoordinateSystemConfig
{

  public static final CoordinateSystem DEFAULT = CoordinateSystem.UTM;

  private static final CoordinateSystemList list = new CoordinateSystemList();

  static {
    list.add(CoordinateSystem.UTM);
    list.add(CoordinateSystem.WGS84);
  }

  private static final Map<String, CoordinateSystem> mapping = list.getMapping();

  private final String[] names = list.getNamesArray();
  private final String[] keys = list.getKeysArray();
  private final Set<String> keySet = list.getKeySet();

  public String[] getNames()
  {
    return names;
  }

  public String[] getKeys()
  {
    return keys;
  }

  public Set<String> getKeySet()
  {
    return keySet;
  }

  public static Map<String, CoordinateSystem> getMapping()
  {
    return mapping;
  }

  public String getDefaultKey()
  {
    return DEFAULT.getKey();
  }

}
