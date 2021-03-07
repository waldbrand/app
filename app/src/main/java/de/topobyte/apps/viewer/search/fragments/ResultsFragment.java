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

package de.topobyte.apps.viewer.search.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.poi.PoiTypeInfo;
import de.topobyte.apps.viewer.search.DatabaseAccess;
import de.topobyte.apps.viewer.search.ResultOrder;
import de.topobyte.apps.viewer.search.SearchQuery;
import de.topobyte.apps.viewer.search.widget.PoiContextMenuListener;
import de.topobyte.apps.viewer.search.widget.PoiListAdapterMix;
import de.topobyte.mercatorcoordinates.GeoConv;
import de.topobyte.nomioc.luqe.model.SqEntity;

public class ResultsFragment extends Fragment
{

  private static final String LOG_TAG = "search";

  private ListView resultsList;
  private PoiListAdapterMix listAdapter;

  private List<SqEntity> results;
  private boolean showDistance;
  private Point queryPoint;

  private DatabaseAccess da;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.i(LOG_TAG, "ResultsFragment.onCreate() "
        + (savedInstanceState != null));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    super.onCreateView(inflater, container, savedInstanceState);
    Log.i(LOG_TAG, "ResultsFragment.onCreateView()");
    View view = inflater.inflate(R.layout.search_results, container, false);

    resultsList = view.findViewById(R.id.resultsList);

    Log.i(LOG_TAG, "Have results? " + (results != null));
    if (results != null) {
      setupListAdapter();
    }

    return view;
  }

  private boolean initializedListStuff = false;

  private void initializeListStuff()
  {
    if (initializedListStuff) {
      return;
    }

    initializedListStuff = true;

    PoiTypeInfo typeInfo = PoiTypeInfo.getInstance(da.getDatabase());

    resultsList.setOnCreateContextMenuListener(new PoiContextMenuListener(
        typeInfo));

    resultsList.setOnItemClickListener(new OnItemClickListener()
    {

      @Override
      public void onItemClick(AdapterView<?> parent, View view,
                              int position, long id)
      {
        Object item = parent.getItemAtPosition(position);
        if (!(item instanceof SqEntity)) {
          return;
        }
        SqEntity entity = (SqEntity) item;
        finish(entity.getX(), entity.getY());
      }
    });
  }

  @Override
  public void onPause()
  {
    super.onPause();
    Log.i(LOG_TAG, "ResultsFragment.onPause()");
  }

  @Override
  public void onResume()
  {
    super.onResume();
    Log.i(LOG_TAG, "ResultsFragment.onResume()");
  }

  protected void finish(int mx, int my)
  {
    double lon = GeoConv.mercatorToLongitude(mx);
    double lat = GeoConv.mercatorToLatitude(my);
    getActivity().setResult(Activity.RESULT_OK,
        new Intent().putExtra("lon", lon).putExtra("lat", lat));
    getActivity().finish();
  }

  public void setDatabase(DatabaseAccess da)
  {
    this.da = da;
  }

  public void showResults(SearchQuery query, List<SqEntity> results)
  {
    this.results = results;
    showDistance = query.getResultOrder() == ResultOrder.BY_DISTANCE;
    queryPoint = showDistance ? query.getPosition() : null;
    setupListAdapter();
  }

  private void setupListAdapter()
  {
    if (getActivity() == null) {
      Log.i(LOG_TAG, "activity is null, unable to setup list adapter");
      return;
    }
    if (resultsList == null) {
      return;
    }

    initializeListStuff();

    listAdapter = new PoiListAdapterMix(getActivity(), da, results,
        queryPoint);
    resultsList.setAdapter(listAdapter);
  }
}
