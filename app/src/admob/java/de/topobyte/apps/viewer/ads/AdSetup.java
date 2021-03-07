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

package de.topobyte.apps.viewer.ads;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.freemium.FreemiumUtil;

public class AdSetup
{

  private static final String adTag = "ad";

  private AdFragment adFragment = null;

  public void setupAd(FragmentActivity activity)
  {
    FragmentManager fm = activity.getSupportFragmentManager();

    adFragment = (AdFragment) fm.findFragmentByTag(adTag);

    if (adFragment == null) {
      if (!FreemiumUtil.isUnlocked(activity)) {
        Log.i("ads", "Adding ad fragment");
        adFragment = new AdFragment();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.ad_frame, adFragment, adTag);
        transaction.commit();
      }
    } else {
      if (FreemiumUtil.isUnlocked(activity)) {
        Log.i("ads", "Removing ad fragment");
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.remove(adFragment);
        transaction.commit();
      }
    }
  }

}
