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

package de.topobyte.apps.viewer.search.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import java.util.Locale;

public class PoiShareMenuItemOnClickListener implements OnMenuItemClickListener
{

  private final Context context;
  private double lon;
  private double lat;
  private String name;

  public PoiShareMenuItemOnClickListener(Context context, double lon, double lat, String name)
  {
    this.context = context;
    this.lon = lon;
    this.lat = lat;
    this.name = name;
  }

  @Override
  public boolean onMenuItemClick(MenuItem item)
  {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    String uri = String.format(Locale.US, "geo:%f,%f?q=%f,%f(%s)",
        lat, lon, lat, lon, name);
    intent.setData(Uri.parse(uri));
    context.startActivity(intent);
    return true;
  }

}
