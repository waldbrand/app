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

package de.topobyte.apps.viewer.freemium;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import de.topobyte.android.intent.utils.IntentFactory;
import de.topobyte.android.misc.utils.PackageUtil;
import de.topobyte.apps.viewer.BuildOptions;

public class FreemiumUtil
{

  private static final String UNLOCK_PACKAGE_NAME = "de.topobyte.apps.freemium.unlock.citymaps";

  private static final String PREF_KEY_UNLOCKED = "freemium-unlocked";

  public static boolean isUnlocked(Context context)
  {
    return PackageUtil.isPackageInstalled(context, UNLOCK_PACKAGE_NAME);
  }

  public static FreemiumInfo updateFreemiumInfo(Context context,
                                                SharedPreferences preferences)
  {
    boolean unlocked = false;
    boolean storedValue = false;
    boolean changed = false;

    unlocked = isUnlocked(context);
    if (preferences.contains(PREF_KEY_UNLOCKED)) {
      storedValue = preferences.getBoolean(PREF_KEY_UNLOCKED, false);
    }
    // if the unlocked state does not match the stored state
    if (storedValue != unlocked) {
      changed = true;
      Editor editor = preferences.edit();
      editor.putBoolean(PREF_KEY_UNLOCKED, unlocked);
      editor.commit();
    }

    return new FreemiumInfo(unlocked, changed);
  }

  public static Intent createUnlockIntent()
  {
    Intent unlockIntent = IntentFactory
        .createGooglePlayAppDetailsIntent(UNLOCK_PACKAGE_NAME);
    return unlockIntent;
  }

  public static boolean showPremiumFeatures(Context context)
  {
    if (!BuildOptions.IS_FREEMIUM) {
      return true;
    }
    return isUnlocked(context);
  }

  public static boolean showAdRelatedContent(Context context)
  {
    if (!BuildOptions.HAS_ADS && !BuildOptions.IS_FREEMIUM) {
      return false;
    } else {
      return !FreemiumUtil.isUnlocked(context);
    }
  }

}
