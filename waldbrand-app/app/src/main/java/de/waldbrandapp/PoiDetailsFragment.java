package de.waldbrandapp;

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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static de.topobyte.geomath.WGS84.merc2lat;
import static de.topobyte.geomath.WGS84.merc2lon;
import static de.topobyte.mapocado.mapformat.Geo.MERCATOR_SIZE;
import static java.lang.String.format;

public class PoiDetailsFragment extends BottomSheetDialogFragment
{

  private static final String ARG_TYPE = "type";
  private static final String ARG_X = "x";
  private static final String ARG_Y = "y";
  private static final String ARG_TAGS = "tags";

  private TextView textViewType;
  private TextView textViewPosition;
  private TextView textViewDiameter;

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

    Bundle args = getArguments();
    int type = args.getInt(ARG_TYPE);
    int x = args.getInt(ARG_X);
    int y = args.getInt(ARG_Y);
    TIntObjectMap<String> tags = (TIntObjectMap<String>) args.getSerializable(ARG_TAGS);

    textViewType.setText(Waldbrand.getName(type));
    double lon = merc2lon(x, MERCATOR_SIZE);
    double lat = merc2lat(y, MERCATOR_SIZE);
    textViewPosition.setText(format("Position (lon/lat): %f/%f", lon, lat));

    int idDiameter = Waldbrand.getStringId("fire_hydrant:diameter");
    String diameter = tags.get(idDiameter);

    if (diameter == null) {
      textViewDiameter.setVisibility(GONE);
    } else {
      textViewDiameter.setVisibility(VISIBLE);
      textViewDiameter.setText(String.format("Innendurchmesser: %smm", diameter));
    }

    Button buttonShare = view.findViewById(R.id.buttonShare);
    Button buttonEdit = view.findViewById(R.id.buttonEdit);
    buttonShare.setOnClickListener(
        e -> makeText(requireContext(), "Noch nicht implementiert", LENGTH_SHORT).show());
    buttonEdit.setOnClickListener(
        e -> makeText(requireContext(), "Noch nicht implementiert", LENGTH_SHORT).show());

    return view;
  }

}
