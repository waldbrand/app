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

package de.waldbrandapp;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static java.lang.String.format;
import static de.topobyte.geomath.WGS84.merc2lat;
import static de.topobyte.geomath.WGS84.merc2lon;
import static de.topobyte.mapocado.mapformat.Geo.MERCATOR_SIZE;
import static de.waldbrandapp.Waldbrand.FIRE_WATER_POND;
import static de.waldbrandapp.Waldbrand.PILLAR;
import static de.waldbrandapp.Waldbrand.RETTUNGSPUNKT;
import static de.waldbrandapp.Waldbrand.UNDERGROUND;
import static de.waldbrandapp.Waldbrand.WATER_TANK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.slimjars.dist.gnu.trove.map.TIntObjectMap;

import java.util.Locale;

public class PoiDetailsFragment extends BottomSheetDialogFragment
{

  public static String TAG = PoiDetailsFragment.class.getName();

  private static final String ARG_TYPE = "type";
  private static final String ARG_X = "x";
  private static final String ARG_Y = "y";
  private static final String ARG_TAGS = "tags";

  private TextView textViewType;
  private TextView textViewPosition;
  private TextView textViewDiameter;
  private TextView textViewFlowrate;
  private TextView textViewVolume;
  private TextView textViewId;

  public static PoiDetailsFragment newInstance(PoiLabel poi)
  {
    Bundle args = new Bundle();
    args.putInt(ARG_TYPE, poi.getWaldbrandType());
    args.putInt(ARG_X, poi.x);
    args.putInt(ARG_Y, poi.y);
    args.putSerializable(ARG_TAGS, poi.getTags());

    PoiDetailsFragment fragment = new PoiDetailsFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragement_poi_details, container, false);
    textViewType = view.findViewById(R.id.type);
    textViewPosition = view.findViewById(R.id.position);
    textViewDiameter = view.findViewById(R.id.diameter);
    textViewFlowrate = view.findViewById(R.id.flowrate);
    textViewVolume = view.findViewById(R.id.volume);
    textViewId = view.findViewById(R.id.id);

    Bundle args = getArguments();
    int type = args.getInt(ARG_TYPE);
    int x = args.getInt(ARG_X);
    int y = args.getInt(ARG_Y);
    TIntObjectMap<String> tags = (TIntObjectMap<String>) args.getSerializable(ARG_TAGS);

    textViewType.setText(Waldbrand.getName(type));
    double lon = merc2lon(x, MERCATOR_SIZE);
    double lat = merc2lat(y, MERCATOR_SIZE);
    textViewPosition.setText(format("Position (lon/lat): %f/%f", lon, lat));

    textViewDiameter.setVisibility(GONE);
    textViewFlowrate.setVisibility(GONE);
    if (type == UNDERGROUND) {
      displayDiameter(tags, 10);
    }

    if (type == PILLAR) {
      displayDiameter(tags, 15);
    }

    textViewVolume.setVisibility(GONE);
    if (type == FIRE_WATER_POND || type == WATER_TANK) {
      int idVolume = Waldbrand.getStringId("water_tank:volume");
      String volume = tags.get(idVolume);
      if (volume != null) {
        textViewVolume.setVisibility(VISIBLE);
        textViewVolume.setText(String.format("Volumen: %s", volume));
      }
    }

    textViewId.setVisibility(GONE);
    if (type == RETTUNGSPUNKT) {
      int idId = Waldbrand.getStringId("rettungspunkt-id");
      String id = tags.get(idId);
      if (id != null) {
        textViewId.setVisibility(VISIBLE);
        textViewId.setText(String.format("Nummer: %s", id));
      }
    }

    Button buttonShare = view.findViewById(R.id.buttonShare);
    Button buttonEdit = view.findViewById(R.id.buttonEdit);
    buttonShare.setOnClickListener(
        e -> makeText(requireContext(), "Noch nicht implementiert", LENGTH_SHORT).show());
    buttonEdit.setOnClickListener(e -> openEditor(lon, lat));

    return view;
  }

  private void displayDiameter(TIntObjectMap<String> tags, int factor)
  {
    int idDiameter = Waldbrand.getStringId("fire_hydrant:diameter");
    String diameter = tags.get(idDiameter);
    if (diameter != null) {
      textViewDiameter.setVisibility(VISIBLE);
      textViewDiameter.setText(String.format("Innendurchmesser: %smm", diameter));
      try {
        int d = Integer.parseInt(diameter);
        int flowrate = (int) Math.round(d * factor);
        textViewFlowrate.setVisibility(VISIBLE);
        textViewFlowrate.setText(String.format("Durchflussleistung: %dl/min", flowrate));
      } catch (NumberFormatException e) {
        // ignore for now
      }
    }
  }

  private void openEditor(double lon, double lat)
  {
    int zoom = 17;
    String pattern =
        "https://waldbrand-app.de/mapcomplete/waldbrand.html?z=%d&lat=%f&lon=%f#start";
    String link = format(Locale.US, pattern, zoom, lat, lon);
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
    startActivity(intent);
  }

}
