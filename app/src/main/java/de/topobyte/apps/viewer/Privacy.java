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

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import de.topobyte.android.common.resources.hasviews.HasViews;
import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.freemium.FreemiumUtil;

public class Privacy
{

  public static void setupLinks(HasViews views)
  {
    TextView text = views.findViewById(R.id.google);
    text.setMovementMethod(LinkMovementMethod.getInstance());
  }

  public static void setupVisibility(Context context, HasViews views)
  {
    if (!BuildOptions.HAS_ADS && !BuildOptions.IS_FREEMIUM) {
      views.findViewById(R.id.spacer_before_google).setVisibility(View.GONE);
      views.findViewById(R.id.google).setVisibility(View.GONE);
    }
    if (BuildOptions.HAS_ADS || BuildOptions.IS_FREEMIUM) {
      if (FreemiumUtil.isUnlocked(context)) {
        views.findViewById(R.id.spacer_before_unlock_installed).setVisibility(View.VISIBLE);
        views.findViewById(R.id.unlock_installed).setVisibility(View.VISIBLE);
      }
    }
  }

}
