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

package de.topobyte.apps.viewer.theme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThemeList
{

  private final List<Theme> themes = new ArrayList<>();

  public void add(Theme theme)
  {
    themes.add(theme);
  }

  public String[] getNamesArray()
  {
    String[] names = new String[themes.size()];
    for (int i = 0; i < themes.size(); i++) {
      Theme theme = themes.get(i);
      names[i] = theme.getName();
    }
    return names;
  }

  public String[] getKeysArray()
  {
    String[] keys = new String[themes.size()];
    for (int i = 0; i < themes.size(); i++) {
      Theme theme = themes.get(i);
      keys[i] = theme.getKey();
    }
    return keys;
  }

  public Set<String> getKeySet()
  {
    Set<String> keys = new HashSet<>();
    for (int i = 0; i < themes.size(); i++) {
      Theme theme = themes.get(i);
      keys.add(theme.getKey());
    }
    return keys;
  }
}
