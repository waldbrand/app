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

package de.topobyte.apps.viewer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.topobyte.android.common.resources.CommonIcons;
import de.topobyte.android.intent.utils.AppMetaIntents;
import de.topobyte.android.intent.utils.IntentFactory;
import de.topobyte.android.intent.utils.TopobyteIntentFactory;
import de.waldbrandapp.R;
import de.topobyte.apps.viewer.AppConstants;

public class MoreAppsActivity extends PlainActivity
{

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    setContentView(R.layout.activity_more_apps);

    Button buttonNetzplan = findViewById(R.id.buttonNetzplan);
    Button buttonOtherAppsWeb = findViewById(R.id.buttonOtherAppsWeb);
    Button buttonOtherAppsApp = findViewById(R.id.buttonOtherAppsApp);
    Button buttonAtlas = findViewById(R.id.buttonAtlas);
    Button buttonDice = findViewById(R.id.buttonDice);

    CommonIcons commonIcons = new CommonIcons(this, 36);

    boolean showNetzplanInfo = AppConstants.PACKAGE_NETZPLAN != null;

    if (showNetzplanInfo) {
      commonIcons.setNetzplan(buttonNetzplan);

      buttonNetzplan.setOnClickListener(view -> startActivity(
          IntentFactory.createGooglePlayAppDetailsIntent(AppConstants.PACKAGE_NETZPLAN)));
    } else {
      View spacerNetzplan = findViewById(R.id.spacerNetzplan);
      TextView headNetzplan = findViewById(R.id.headNetzplan);
      TextView textNetzplan = findViewById(R.id.textNetzplan);

      spacerNetzplan.setVisibility(View.GONE);
      headNetzplan.setVisibility(View.GONE);
      textNetzplan.setVisibility(View.GONE);
      buttonNetzplan.setVisibility(View.GONE);
    }

    commonIcons.setDice(buttonDice);
    commonIcons.setMap(buttonOtherAppsWeb);

    buttonOtherAppsWeb.setOnClickListener(
        view -> startActivity(AppMetaIntents.createMapListIntent(MoreAppsActivity.this)));

    buttonOtherAppsApp.setOnClickListener(
        view -> startActivity(TopobyteIntentFactory.createAppManagerIntent(MoreAppsActivity.this)));

    buttonAtlas.setOnClickListener(
        view -> startActivity(TopobyteIntentFactory.createAtlasIntent(MoreAppsActivity.this)));

    buttonDice.setOnClickListener(
        view -> startActivity(TopobyteIntentFactory.createDiceIntent(MoreAppsActivity.this)));

  }

}
