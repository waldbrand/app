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

package de.topobyte.apps.viewer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.topobyte.android.common.resources.CommonIcons;
import de.topobyte.android.intent.utils.IntentFactory;
import de.topobyte.apps.viewer.FeedbackUtil;
import de.waldbrandapp.BuildConfig;
import de.waldbrandapp.R;

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

    TextView website = findViewById(R.id.textViewWebsite);
    TextView repo = findViewById(R.id.textViewRepo);
    TextView license = findViewById(R.id.textViewLicense);

    license.setText(
        String.format("%s: %s", getString(R.string.about_license), getString(R.string.gpl_3_0)));

    Linkify.addLinks(website, Pattern.compile("www.waldbrand-app.de"), "", allFilter,
        new StaticUrlTransformer("https://www.waldbrand-app.de"));
    Linkify.addLinks(repo, Pattern.compile("waldbrand/app"), "", allFilter,
        new StaticUrlTransformer("https://github.com/waldbrand/app"));
    Linkify.addLinks(license, Pattern.compile(getString(R.string.gpl_3_0)), "", allFilter,
        new StaticUrlTransformer("https://www.gnu.org/licenses/gpl-3.0.en.html"));

    TextView copyrightOSM = findViewById(R.id.textViewMapCopyrightOSM);
    TextView licenseOSM = findViewById(R.id.textViewMapLicenseOSM);

    Linkify.addLinks(copyrightOSM, Pattern.compile("OpenStreetMap"), "", allFilter,
        new StaticUrlTransformer("http://openstreetmap.org/about"));
    Linkify.addLinks(licenseOSM, Pattern.compile("Open Database License"), "", allFilter,
        new StaticUrlTransformer("http://opendatacommons.org/licenses/odbl"));

    TextView licenseLFB = findViewById(R.id.textViewEmergencyAccessPointsLicense);
    TextView linkLFB = findViewById(R.id.textViewEmergencyAccessPointsLink);

    Linkify.addLinks(licenseLFB, Pattern.compile("[^:]+"), "", (s, start, end) -> start != 0,
        new StaticUrlTransformer("https://www.govdata.de/dl-de/by-2-0"));
    Linkify.addLinks(linkLFB, Pattern.compile(".*"), "", allFilter,
        new StaticUrlTransformer(
            "https://geoportal.brandenburg.de/detailansichtdienst/render?view=gdibb&url=https%3A%2F%2Fregistry.gdi-de.org%2Fid%2Fde.bb.metadata%2F8D7BE274-EB8A-4E63-8111-A0E5A0CBFC12"));
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
