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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Timer;
import java.util.TimerTask;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.ads.AdSetup;
import de.topobyte.apps.viewer.freemium.FreemiumInfo;
import de.topobyte.apps.viewer.freemium.FreemiumUtil;

public class Stadtplan extends BaseStadtplan
{

  private Timer timer = null;

  // The unlock buttons current label index
  private int state = 0;

  private FreemiumInfo freemiumInfo = null;

  private View unlockButton = null;

  private AdSetup adSetup = new AdSetup();

  private boolean loaded = false;
  private boolean mobileAdsInitialized = false;

  @Override
  public void loaderFinished()
  {
    super.loaderFinished();

    if (initializationSucceeded) {
      init();
      updateFreemium();
    }
  }

  private void init()
  {
    loaded = true;

    unlockButton = findViewById(R.id.unlock);
    unlockButton.setOnClickListener(new View.OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        UnlockDialog unlockDialog = new UnlockDialog();
        unlockDialog.show(getSupportFragmentManager(), null);
      }
    });

    initializeMobileAds();
  }

  private void initializeMobileAds()
  {
    if (mobileAdsInitialized) {
      return;
    }

    if (!FreemiumUtil.isUnlocked(this)) {
      mobileAdsInitialized = true;
      MobileAds.initialize(this, new OnInitializationCompleteListener()
      {
        @Override
        public void onInitializationComplete(InitializationStatus initializationStatus)
        {
        }
      });
    }
  }

  @Override
  public void onResume()
  {
    super.onResume();

    if (!loaded && initializationDone && initializationSucceeded) {
      init();
    }

    if (!loaded) {
      return;
    }

    if (initializationDone && initializationSucceeded) {
      updateFreemium();
    }
  }

  private void updateFreemium()
  {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

    freemiumInfo = FreemiumUtil.updateFreemiumInfo(this, preferences);

    if (freemiumInfo.isUnlocked()) {
      unlockButton.setVisibility(View.GONE);
    } else {
      unlockButton.setVisibility(View.VISIBLE);
    }

    if (freemiumInfo.hasChanged()) {
      adSetup.setupAd(this);
      if (!freemiumInfo.isUnlocked()) {
        initializeMobileAds();
      }
    }

    final int delay = 5000;

    if (!freemiumInfo.isUnlocked()) {
      Log.i("freemiumstuff", "Starting change text timer");
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask()
      {
        @Override
        public void run()
        {
          runOnUiThread(new Runnable()
          {
            @Override
            public void run()
            {
              switchUnlockText();
            }
          });
        }
      }, delay, delay);
    }
  }

  @Override
  public void onPause()
  {
    super.onPause();

    if (timer != null) {
      timer.cancel();
    }
  }

  @Override
  public void mapFragmentViewCreated()
  {
    super.mapFragmentViewCreated();

    adSetup.setupAd(this);
  }

  private void switchUnlockText()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(this);
    FreemiumInfo freemiumInfo = FreemiumUtil.updateFreemiumInfo(this, preferences);
    Button unlockButton = findViewById(R.id.unlock);

    Log.i("handlerstuff", "hey jo, man: " + unlockButton);

    if (!freemiumInfo.isUnlocked()) {
      state = (state + 1) % 3;
      if (state == 0) {
        unlockButton.setText(R.string.unlock);
      } else if (state == 1) {
        unlockButton.setText(R.string.unlock2);
      } else if (state == 2) {
        unlockButton.setText(R.string.unlock3);
      }
    }
  }

}
