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

package de.topobyte.apps.viewer;

import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

import de.topobyte.android.intent.utils.AppMetaIntents;
import de.topobyte.android.intent.utils.IntentFactory;
import de.topobyte.android.intent.utils.TopobyteIntentFactory;
import de.topobyte.android.misc.utils.PackageUtil;
import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.activities.AboutActivity;
import de.topobyte.apps.viewer.activities.MoreAppsActivity;
import de.topobyte.apps.viewer.activities.PrivacyActivity;
import de.topobyte.apps.viewer.config.AppClassUtil;
import de.topobyte.apps.viewer.preferences.SettingsActivity;

public class CommonMenu
{

  public static void setupVisibility(Menu menu)
  {
    if (!AppClassUtil.showGooglePlayLinksToApps()) {
      menuState(menu, R.id.menu_more_maps, false);
    }

    boolean showNetzplan = AppConstants.PACKAGE_NETZPLAN != null;
    menuState(menu, R.id.menu_public_transport, showNetzplan);
  }

  public static void menuState(Menu menu, int itemId, boolean state)
  {
    MenuItem item = menu.findItem(itemId);
    if (item != null) {
      item.setVisible(state);
    }
  }

  public static boolean handleMenuItemSelected(FragmentActivity context, MenuItem item)
  {
    Intent intent;
    switch (item.getItemId()) {

      case R.id.menu_info_about:
        context.startActivity(new Intent(context, AboutActivity.class));
        return true;

      case R.id.menu_info_privacy:
        context.startActivity(new Intent(context, PrivacyActivity.class));
        return true;

      case R.id.menu_preferences:
        context.startActivity(new Intent(context, SettingsActivity.class));
        return true;

      case R.id.menu_tips_and_tricks:
        TipsAndTricks.showTipsAndTricks(context);
        return true;

      case R.id.menu_more_maps:
        intent = new Intent(context, MoreAppsActivity.class);
        context.startActivity(intent);
        return true;

      case R.id.menu_faq:
        intent = AppMetaIntents.createFAQIntent(context);
        context.startActivity(intent);
        return true;

      case R.id.menu_weather:
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        intent = TopobyteIntentFactory.createTopobyteWeatherIntent(
            AppConstants.ISO3, AppConstants.CITY_NAME, lang, false);
        context.startActivity(intent);
        return true;

      case R.id.menu_public_transport:
        if (AppConstants.PACKAGE_NETZPLAN != null) {
          if (PackageUtil.isPackageInstalled(context, AppConstants.PACKAGE_NETZPLAN)) {
            intent =
                context.getPackageManager().getLaunchIntentForPackage(AppConstants.PACKAGE_NETZPLAN);
          } else {
            intent = IntentFactory.createGooglePlayAppDetailsIntent(AppConstants.PACKAGE_NETZPLAN);
          }
          context.startActivity(intent);
        }
        return true;

      default:
        return false;
    }
  }

}
