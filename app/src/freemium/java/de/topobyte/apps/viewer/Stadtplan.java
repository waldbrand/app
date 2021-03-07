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
import android.view.View;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.freemium.FreemiumInfo;
import de.topobyte.apps.viewer.freemium.FreemiumUtil;

public class Stadtplan extends BaseStadtplan
{

  private FreemiumInfo freemiumInfo = null;

  private View unlockButton = null;

  private boolean loaded = false;

  @Override
  public void loaderFinished()
  {
    super.loaderFinished();

    if (initializationSucceeded) {
      init();
      updateFreemium();
    }
  }

  @Override
  public void onResume()
  {
    super.onResume();

    if (!loaded && initializationDone && initializationSucceeded) {
      init();
    }

    if (initializationDone && initializationSucceeded) {
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
  }

}
