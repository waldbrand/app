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

package de.topobyte.apps.viewer.overlay;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;

import de.waldbrandapp.R;
import de.topobyte.apps.viewer.Constants;
import de.topobyte.apps.viewer.map.Global;
import de.topobyte.apps.viewer.theme.ThemeConfig;

public class ThemeSelectorDialog extends DialogFragment
{

  private ThemeConfig themeConfig;
  private String[] keys;
  private String[] names;

  private String currentKey;
  private int currentIndex = 0;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    themeConfig = new ThemeConfig();

    keys = themeConfig.getThemeKeys();
    names = themeConfig.getThemeNames();

    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
    currentKey = preferences.getString(Constants.PREF_RENDER_THEME,
        themeConfig.getDefaultThemeKey());

    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      if (currentKey.equals(key)) {
        currentIndex = i;
        break;
      }
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(getActivity().getResources().getString(
        R.string.dialog_select_theme_title));

    builder.setSingleChoiceItems(names, currentIndex,
        (dialog, which) -> selected(which));

    return builder.create();
  }

  protected void selected(int which)
  {
    dismiss();
    if (which != currentIndex) {
      String newKey = keys[which];
      setTheme(newKey);
    }
  }

  private void setTheme(String newKey)
  {
    Global global = Global.getInstance(getActivity());
    try {
      global.setRenderTheme(getActivity(), newKey);
    } catch (IOException e) {
      Log.e("theme", "unable to set rendertheme key: '" + newKey + "'", e);
      return;
    }
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
    Editor editor = preferences.edit();
    editor.putString(Constants.PREF_RENDER_THEME, newKey);
    editor.commit();
  }

}
