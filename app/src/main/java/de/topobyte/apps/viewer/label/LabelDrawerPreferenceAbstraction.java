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

package de.topobyte.apps.viewer.label;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import de.topobyte.melon.enums.EnumLookup;
import de.topobyte.melon.enums.EnumLookups;
import de.topobyte.melon.enums.naming.SimpleEnumNamer;

public class LabelDrawerPreferenceAbstraction
{

  private final SharedPreferences prefs;

  public LabelDrawerPreferenceAbstraction(Context context)
  {
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public LabelDrawerPreferenceAbstraction(SharedPreferences prefs)
  {
    this.prefs = prefs;
  }

  public static EnumLookup<LabelMode> modeLookup =
      EnumLookups.build(LabelMode.class, new SimpleEnumNamer<>());

  public void storeLabelMode(LabelMode mode)
  {
    Editor editor = prefs.edit();
    editor.putString(LabelDrawerPoi.PREF_MODE, mode.name());
    editor.commit();
  }

  public LabelMode determineLabelMode()
  {
    String modeName = prefs.getString(LabelDrawerPoi.PREF_MODE, null);
    LabelMode mode = modeLookup.get(modeName);
    if (mode == null) {
      mode = LabelDrawerPoi.DEFAULT_MODE;
    }
    return mode;
  }

}
