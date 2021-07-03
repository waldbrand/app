package de.waldbrandapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.topobyte.apps.viewer.label.Poi;

import static de.topobyte.geomath.WGS84.merc2lat;
import static de.topobyte.geomath.WGS84.merc2lon;
import static de.topobyte.mapocado.mapformat.Geo.MERCATOR_SIZE;
import static java.lang.String.format;

public class PoiDetailsFragment extends BottomSheetDialogFragment
{

  private static final String ARG_TYPE = "type";
  private static final String ARG_X = "x";
  private static final String ARG_Y = "y";

  private TextView textViewType;
  private TextView textViewPosition;

  public static PoiDetailsFragment newInstance(Poi poi)
  {
    Bundle args = new Bundle();
    args.putString(ARG_TYPE, poi.getType());
    args.putInt(ARG_X, poi.getLabel().x);
    args.putInt(ARG_Y, poi.getLabel().y);

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

    Bundle args = getArguments();
    String type = args.getString(ARG_TYPE);
    int x = args.getInt(ARG_X);
    int y = args.getInt(ARG_Y);

    textViewType.setText(Waldbrand.getName(type));
    double lon = merc2lon(x, MERCATOR_SIZE);
    double lat = merc2lat(y, MERCATOR_SIZE);
    textViewPosition.setText(format("Position (lon/lat): %f/%f", lon, lat));

    return view;
  }

}
