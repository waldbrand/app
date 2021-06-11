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

import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.slimjars.dist.gnu.trove.set.TIntSet;

import de.topobyte.apps.viewer.poi.PoiHelper;
import de.topobyte.apps.viewer.poi.PoiTypeInfo;
import de.topobyte.nomioc.luqe.model.SqEntity;
import de.topobyte.nomioc.luqe.model.SqPoi;
import de.waldbrandapp.R;

public class PoiContextMenuListener implements OnCreateContextMenuListener
{

  private final PoiTypeInfo typeInfo;

  public PoiContextMenuListener(PoiTypeInfo typeInfo)
  {
    this.typeInfo = typeInfo;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
                                  ContextMenuInfo menuInfo)
  {
    ListView list = (ListView) v;
    AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    PoiList poiAdapter = (PoiList) list.getAdapter();
    SqEntity entity = poiAdapter.getItem(info.position);
    if (!(entity instanceof SqPoi)) {
      return;
    }
    SqPoi poi = (SqPoi) entity;
    Resources resources = v.getResources();

    TIntSet types = poi.getTypes();
    boolean noPhone = PoiHelper.isPeakOrVolcano(typeInfo, types);

    // TODO: implement this
//    MenuItem itemShare = menu.add("share");
//    double lon = GeoConv.mercatorToLongitude(poi.getX());
//    double lat = GeoConv.mercatorToLatitude(poi.getY());
//    itemShare.setOnMenuItemClickListener(new PoiShareMenuItemOnClickListener(
//        v.getContext(), lon, lat, poi.getNameSafe()));

    if (!noPhone && poi.getPhone() != null) {
      String dial = resources.getString(R.string.context_dial);
      MenuItem item = menu.add(dial + ": " + poi.getPhone());
      item.setOnMenuItemClickListener(new PoiDialMenuItemOnClickListener(
          v.getContext(), poi.getPhone()));
    }
    if (poi.getWebsite() != null) {
      MenuItem item = menu.add(resources
          .getString(R.string.context_website));
      item.setOnMenuItemClickListener(new PoiWebMenuItemOnClickListener(v
          .getContext(), poi.getWebsite()));
    }
  }
}
