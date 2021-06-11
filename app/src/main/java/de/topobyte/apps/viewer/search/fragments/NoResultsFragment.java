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

package de.topobyte.apps.viewer.search.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import de.waldbrandapp.R;

public class NoResultsFragment extends Fragment
{

  private TextView resultsText1;
  private TextView resultsText2;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.search_no_results, container,
        false);

    resultsText1 = view.findViewById(R.id.resultsText1);
    resultsText2 = view.findViewById(R.id.resultsText2);

    resultsText1.setText(R.string.search_no_results);

    resultsText2.setVisibility(View.VISIBLE);
    resultsText2.setText(R.string.search_no_results_more);

    return view;
  }

  @Override
  public void onPause()
  {
    super.onPause();
  }

  @Override
  public void onResume()
  {
    super.onResume();
  }

}
