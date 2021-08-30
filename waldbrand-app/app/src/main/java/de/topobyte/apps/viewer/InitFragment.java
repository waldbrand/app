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

package de.topobyte.apps.viewer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import de.topobyte.android.appversions.VersionUpdateChecker;
import de.topobyte.android.loader3.TaskFragment;
import de.topobyte.android.maps.utils.MagnificationConfig;
import de.topobyte.apps.viewer.map.MapPreferenceAbstraction;
import de.waldbrandapp.BuildConfig;

public class InitFragment extends TaskFragment
{

  private static final String LOG_TAG_DATA = "data";

  private static final String PREF_KEY_DATABASE_HASH = "database-hash";

  private VersionUpdateChecker versionUpdateChecker;

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);

    versionUpdateChecker = new VersionUpdateChecker(activity);
  }

  @Override
  public boolean performInitialization()
  {
//    SystemClock.sleep(2000);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
    String hash = preferences.getString(PREF_KEY_DATABASE_HASH, null);
    boolean forceCopyDatabase = false;
    if (!BuildConfig.DATABASE_FILE_MD5.equals(hash)) {
      forceCopyDatabase = true;
    }

    if (versionUpdateChecker.isVersionUpdate() || forceCopyDatabase) {

      initializeMagnificationSettings();

      if (versionUpdateChecker.getStoredVersion() < 7) {
        setScaleBarEnabledByDefault();
      }

      if (versionUpdateChecker.getStoredVersion() < 91 || forceCopyDatabase) {
        MapPreferenceAbstraction mapPrefs = new MapPreferenceAbstraction(getActivity(), null);
        mapPrefs.clearPosition();
      }

      if (versionUpdateChecker.getStoredVersion() <= 120) {
        initializePersonalizedAdsSettings();
        initializeDebugSettings();
      }

      FileUtil.wipeFiles(getActivity());

      boolean enoughSpace = false;
      enoughSpace = FileUtil
          .hasEnoughSpaceAtLocation(getActivity().getFilesDir(),
              ResourceConstantsHelper
                  .getEstimatedNumberOfRequiredBytes());

      if (!enoughSpace) {
        return false;
      }

      boolean success = ensureFiles();

      if (success) {
        versionUpdateChecker.storeCurrentVersion();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_KEY_DATABASE_HASH, BuildConfig.DATABASE_FILE_MD5);
        editor.commit();
      }

      return success;
    }
    return true;
  }

  private void initializeMagnificationSettings()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());

    MagnificationConfig magnificationConfig = MagnificationConfig
        .getMagnificationConfig(getActivity());

    SharedPreferences.Editor editor = preferences.edit();

    editor.putInt(Constants.PREF_MAGNIFICATION,
        Constants.DEFAULT_MAGNIFICATION);
    editor.putInt(Constants.PREF_MIN_MAGNIFICATION, magnificationConfig.min);
    editor.putInt(Constants.PREF_MAX_MAGNIFICATION, magnificationConfig.max);

    editor.commit();
  }

  private void setScaleBarEnabledByDefault()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());

    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(Constants.PREF_SHOW_SCALE_BAR, Constants.DEFAULT_HAS_SCALE_BAR);
    editor.commit();
  }

  private boolean ensureFiles()
  {
    boolean filesOk = false;

    try {
      FileUtil.ensureAssets(getActivity(), ResourceConstants.ASS_DATABASE_FILE,
          ResourceConstants.DATABASE_FILE,
          BuildConfig.DATABASE_FILE_SIZE, true);
      filesOk = true;
    } catch (IOException e) {
      Log.e(LOG_TAG_DATA, "unable to ensure assets: " + e.getMessage());
      Log.d(LOG_TAG_DATA, "wiping files");
      FileUtil.wipeFiles(getActivity());
    }

    return filesOk;
  }

  private void initializePersonalizedAdsSettings()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());

    SharedPreferences.Editor editor = preferences.edit();

    editor.putBoolean(Constants.PREF_PERSONALIZED_ADS, Constants.DEFAULT_PERSONALIZED_ADS);

    editor.commit();
  }

  private void initializeDebugSettings()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());

    SharedPreferences.Editor editor = preferences.edit();

    editor.putBoolean(Constants.PREF_SHOW_GRID, Constants.DEFAULT_SHOW_GRID);
    editor.putBoolean(Constants.PREF_SHOW_ZOOM_LEVEL, Constants.DEFAULT_SHOW_ZOOM_LEVEL);
    editor.putBoolean(Constants.PREF_SHOW_COORDINATES, Constants.DEFAULT_SHOW_COORDINATES);

    editor.commit();
  }

}
