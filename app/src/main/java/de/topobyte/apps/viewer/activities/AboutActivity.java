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

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.topobyte.android.common.resources.CommonIcons;
import de.topobyte.android.intent.utils.IntentFactory;
import de.topobyte.android.intent.utils.ThankOption;
import de.topobyte.android.intent.utils.TopobyteIntentFactory;
import de.topobyte.apps.maps.atestcity.BuildConfig;
import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.FeedbackUtil;

public class AboutActivity extends PlainActivity
{

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    setContentView(R.layout.activity_about);

    TextView appVersion = findViewById(R.id.textViewAppVersion);
    String appVersionTemplate = getString(R.string.app_version);
    String versionName = BuildConfig.VERSION_NAME;
    appVersion.setText(String.format(appVersionTemplate, versionName));

    setupFeedback();
    setupDonate();

    TextView mapCopyright = findViewById(R.id.textViewMapCopyright);
    TextView mapLicense = findViewById(R.id.textViewMapLicense);

    Linkify.addLinks(mapCopyright, Pattern.compile("OpenStreetMap"), "", allFilter,
        new StaticUrlTransformer("http://openstreetmap.org/about"));
    Linkify.addLinks(mapLicense, Pattern.compile("Open Database License"), "", allFilter,
        new StaticUrlTransformer("http://opendatacommons.org/licenses/odbl"));
  }

  private void setupFeedback()
  {
    Button buttonRate = findViewById(R.id.buttonRate);
    Button buttonMail = findViewById(R.id.buttonMail);
    Button buttonShare = findViewById(R.id.buttonShare);

    CommonIcons commonIcons = new CommonIcons(this, 36);

    commonIcons.setRate(buttonRate);
    commonIcons.setEmail(buttonMail);
    commonIcons.setShare(buttonShare);

    buttonRate.setOnClickListener(view -> {
      Intent intent = IntentFactory.createRateAppIntent(AboutActivity.this);
      startActivity(intent);
    });

    buttonMail.setOnClickListener(view -> FeedbackUtil.sendFeedbackMail(AboutActivity.this));

    buttonShare.setOnClickListener(view -> FeedbackUtil.share(AboutActivity.this));
  }

  private void setupDonate()
  {
    Button buttonDonate1 = findViewById(R.id.buttonDonate1);
    Button buttonDonate2 = findViewById(R.id.buttonDonate2);
    Button buttonDonate5 = findViewById(R.id.buttonDonate5);
    Button buttonDonate10 = findViewById(R.id.buttonDonate10);

    CommonIcons commonIcons = new CommonIcons(this, 36);

    commonIcons.setCafe(buttonDonate1);
    commonIcons.setBeer(buttonDonate2);
    commonIcons.setCinema(buttonDonate5);
    commonIcons.setRestaurant(buttonDonate10);

    buttonDonate1.setOnClickListener(view -> {
      Intent intent = TopobyteIntentFactory.createThanksAppDetailIntent(ThankOption.THANK_1);
      startActivity(intent);
    });

    buttonDonate2.setOnClickListener(view -> {
      Intent intent = TopobyteIntentFactory.createThanksAppDetailIntent(ThankOption.THANK_2);
      startActivity(intent);
    });

    buttonDonate5.setOnClickListener(view -> {
      Intent intent = TopobyteIntentFactory.createThanksAppDetailIntent(ThankOption.THANK_5);
      startActivity(intent);
    });

    buttonDonate10.setOnClickListener(view -> {
      Intent intent = TopobyteIntentFactory.createThanksAppDetailIntent(ThankOption.THANK_10);
      startActivity(intent);
    });
  }

  static final Linkify.MatchFilter allFilter = (s, start, end) -> true;

  static class StaticUrlTransformer implements Linkify.TransformFilter
  {

    private final String link;

    public StaticUrlTransformer(String link)
    {
      this.link = link;
    }

    @Override
    public String transformUrl(Matcher match, String url)
    {
      return link;
    }
  }

}