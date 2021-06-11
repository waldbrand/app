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

package de.topobyte.apps.viewer.preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import androidx.annotation.Nullable;

import de.topobyte.apps.viewer.Constants;
import de.topobyte.apps.viewer.theme.ThemeConfig;
import de.waldbrandapp.R;

public class SettingsFragment extends PreferenceFragment
{

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
    updateThemePreference();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);

    setupAppInfoButton();
    setupLocationSettingsButton();
  }

  private void updateThemePreference()
  {
    ThemeConfig themeConfig = new ThemeConfig();

    ListPreference renderStyleList = (ListPreference) findPreference(Constants.PREF_RENDER_THEME);

    renderStyleList.setEntries(themeConfig.getThemeNames());
    renderStyleList.setEntryValues(themeConfig.getThemeKeys());
    renderStyleList.setDefaultValue(themeConfig.getDefaultThemeKey());
  }

  private void setupAppInfoButton()
  {
    Preference button = findPreference(Constants.PREF_APP_INFO);
    button.setOnPreferenceClickListener(preference -> {
      Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
          .setData(Uri.fromParts("package", getActivity().getPackageName(), null));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      return true;
    });
  }

  private void setupLocationSettingsButton()
  {
    Preference button = findPreference(Constants.PREF_LOCATION_SOURCES);
    button.setOnPreferenceClickListener(preference -> {
      Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      return true;
    });
  }

}
