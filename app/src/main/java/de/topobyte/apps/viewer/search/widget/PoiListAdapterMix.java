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
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.slimjars.dist.gnu.trove.set.TIntSet;

import org.locationtech.jts.geom.Coordinate;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.topobyte.apps.viewer.poi.ListDrawables;
import de.topobyte.apps.viewer.poi.PoiHelper;
import de.topobyte.apps.viewer.poi.PoiTypeInfo;
import de.topobyte.apps.viewer.search.DatabaseAccess;
import de.topobyte.geomath.WGS84;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.mercatorcoordinates.GeoConv;
import de.topobyte.nomioc.luqe.model.SqBorough;
import de.topobyte.nomioc.luqe.model.SqEntity;
import de.topobyte.nomioc.luqe.model.SqPoi;
import de.topobyte.nomioc.luqe.model.SqPostcode;
import de.waldbrandapp.R;

public class PoiListAdapterMix extends BaseAdapter implements PoiList
{

  final static String LOG_TAG = "poimix";

  private final Context context;
  private final DatabaseAccess da;
  private final LayoutInflater inflater;

  private final List<SqEntity> pois;
  private final Point point;
  private Coordinate c;

  private final PoiTypeInfo typeInfo;

  private final NumberFormat usFormat;
  private final NumberFormat userFormatHeight;
  private final NumberFormat userFormatDistance;

  public PoiListAdapterMix(Context context, DatabaseAccess da,
                           List<SqEntity> pois, Point point)
  {
    this.context = context;
    this.da = da;
    this.pois = pois;
    this.point = point;
    inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (point != null) {
      double lon = GeoConv.mercatorToLongitude(point.x);
      double lat = GeoConv.mercatorToLatitude(point.y);
      c = new Coordinate(lon, lat);
    }

    typeInfo = PoiTypeInfo.getInstance(da.getDatabase());

    Locale userLocale = Locale.getDefault();
    usFormat = NumberFormat.getNumberInstance(Locale.US);
    userFormatHeight = NumberFormat.getNumberInstance(userLocale);
    userFormatDistance = NumberFormat.getNumberInstance(userLocale);
    userFormatHeight.setMaximumFractionDigits(2);
    userFormatDistance.setMaximumFractionDigits(2);
  }

  @Override
  public int getCount()
  {
    return this.pois.size();
  }

  @Override
  public SqEntity getItem(int i)
  {
    return this.pois.get(i);
  }

  @Override
  public long getItemId(int i)
  {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View view = convertView;
    if (view == null) {
      view = inflater.inflate(R.layout.row_layout_poimix, parent, false);
    }
    if (!da.resourceAvailable()) {
      Log.w(LOG_TAG, "database not available");
      return view;
    }
    SqEntity entity = this.pois.get(position);

    TextView text = view.findViewById(R.id.label);
    ImageView image = view.findViewById(R.id.icon);
    TextView boroughsPost = view
        .findViewById(R.id.labelBoroughsPost);
    TextView meta = view.findViewById(R.id.labelMeta);

    text.setText(entity.getNameSafe());

    IConnection db = da.getDatabase();

    boolean showDistance = point != null;
    double distance = 0;
    if (showDistance) {
      double lon = GeoConv.mercatorToLongitude(entity.getX());
      double lat = GeoConv.mercatorToLatitude(entity.getY());
      distance = WGS84.haversineDistance(c.x, c.y, lon, lat);
    }

    try {
      Set<SqBorough> boroughs = entity.getBoroughs(db);
      Set<SqPostcode> postCodes = entity.getPostcodes(db);
      if (boroughs.isEmpty() && postCodes.isEmpty() && !showDistance) {
        boroughsPost.setVisibility(View.GONE);
      } else {
        boroughsPost.setVisibility(View.VISIBLE);
        StringBuilder buffer = new StringBuilder();
        if (showDistance) {
          buffer.append(getFormattedDistance(distance));
        }
        if (!boroughs.isEmpty()) {
          if (buffer.length() != 0) {
            buffer.append(", ");
          }
          buffer.append(SqEntity.getBorouhgsAsString(boroughs));
        }
        if (!postCodes.isEmpty()) {
          if (buffer.length() != 0) {
            buffer.append(", ");
          }
          buffer.append(SqEntity.getPostcodesAsString(postCodes));
        }
        boroughsPost.setText(buffer.toString());
      }
    } catch (QueryException e) {
      Log.e("database", "Unable to retrieve verbose string", e);
    }

    if (entity instanceof SqPoi) {
      SqPoi poi = (SqPoi) entity;
      TIntSet types = poi.getTypes();
      Log.i(LOG_TAG, "categories: " + types);
      if (types != null) {
        int cat = types.iterator().next();
        int drawableId = ListDrawables.getDrawable(cat);
        Drawable drawable = view.getResources().getDrawable(drawableId);
        image.setImageDrawable(drawable);
      } else {
        image.setImageDrawable(view.getResources().getDrawable(
            R.drawable.cat_misc));
      }

      String metaText = getMeta(poi);
      if (metaText != null) {
        meta.setText(metaText);
        meta.setVisibility(View.VISIBLE);
      } else {
        meta.setVisibility(View.GONE);
      }
    } else {
      Log.i(LOG_TAG, "category: street");
      image.setImageDrawable(view.getResources().getDrawable(
          R.drawable.cat_street));

      meta.setVisibility(View.GONE);
    }

    return view;
  }

  private String getFormattedDistance(double meters)
  {
    if (meters < 1000) {
      return (int) meters + "m";
    }
    double kilometers = meters / 1000;
    return userFormatDistance.format(kilometers) + "km";
  }

  private String getFormattedHeight(String phone) throws ParseException
  {
    Number number = usFormat.parse(phone);
    return userFormatHeight.format(number) + "m";
  }

  private String getMeta(SqPoi poi)
  {
    String phone = poi.getPhone();
    String website = poi.getWebsite();

    List<String> parts = new ArrayList<>();

    boolean peak = PoiHelper.isPeakOrVolcano(typeInfo, poi.getTypes());
    if (peak) {
      String height = getHeight(phone);
      addNonNull(parts, height);
    } else {
      addNonNull(parts, phone);
    }

    addNonNull(parts, website);

    return getMeta(parts);
  }

  private void addNonNull(List<String> parts, String part)
  {
    if (part == null) {
      return;
    }
    parts.add(part);
  }

  private String getHeight(String phone)
  {
    if (phone == null) {
      return null;
    }

    try {
      String formatted = getFormattedHeight(phone);
      String elevation = context.getString(R.string.elevation);
      return elevation + ": " + formatted;
    } catch (ParseException e) {
      Log.e(LOG_TAG, "Error while parsing number: " + phone, e);
      return null;
    }
  }

  private String getMeta(List<String> parts)
  {
    if (parts.size() == 0) {
      return null;
    }
    StringBuilder buffer = new StringBuilder();
    Iterator<String> iterator = parts.iterator();
    while (iterator.hasNext()) {
      buffer.append(iterator.next());
      if (iterator.hasNext()) {
        buffer.append(", ");
      }
    }
    return buffer.toString();
  }

}
