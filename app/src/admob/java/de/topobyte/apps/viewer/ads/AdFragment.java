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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.Constants;

public class AdFragment extends Fragment
{
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    return inflater.inflate(R.layout.fragment_ad, container, false);
    // return inflater.inflate(R.layout.fragment_test, container, false);
  }

  @Override
  public void onActivityCreated(Bundle bundle)
  {
    super.onActivityCreated(bundle);
    final AdView mAdView = getView().findViewById(R.id.adView);

    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getContext());

    boolean personalizedAds = preferences.getBoolean(
        Constants.PREF_PERSONALIZED_ADS,
        Constants.DEFAULT_PERSONALIZED_ADS);

    Bundle extras = new Bundle();
    if (!personalizedAds) {
      extras.putString("npa", "1");
    }

    Builder builder = new AdRequest.Builder();
    builder = builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
    AdRequest adRequest = builder.build();

    mAdView.setVisibility(View.GONE);
    mAdView.setAdListener(new AdListener()
    {

      @Override
      public void onAdLoaded()
      {
        mAdView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAdFailedToLoad(int errorCode)
      {
        Log.i("ads", "error code: " + errorCode);
      }
    });

    mAdView.loadAd(adRequest);
  }
}
