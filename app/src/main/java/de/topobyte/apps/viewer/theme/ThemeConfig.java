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

import java.util.Set;

public class ThemeConfig
{
  private static final ThemeList themes = new ThemeList();

  static {
    themes.add(new Theme("Default", "style-default", false));
    themes.add(new Theme("At Night", "style-inverted-luminance", false));
    themes.add(new Theme("Dark Blue", "style-inverted-rgb", false));
    themes.add(new Theme("Pink", "style-pink", false));
    themes.add(new Theme("Black & White light", "style-bw-light", false));
    themes.add(new Theme("Black & White dark", "style-bw-dark", false));
  }

  private final String[] themeNames = themes.getNamesArray();
  private final String[] themeKeys = themes.getKeysArray();
  private final Set<String> themeKeySet = themes.getKeySet();

  public String[] getThemeNames()
  {
    return themeNames;
  }

  public String[] getThemeKeys()
  {
    return themeKeys;
  }

  public Set<String> getThemeKeySet()
  {
    return themeKeySet;
  }

  public String getDefaultThemeKey()
  {
    return themeKeys[0];
  }
}
