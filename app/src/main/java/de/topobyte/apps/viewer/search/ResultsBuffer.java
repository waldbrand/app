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

package de.topobyte.apps.viewer.search;

import android.util.Log;

import java.util.List;

import de.topobyte.nomioc.luqe.model.SqEntity;

public class ResultsBuffer implements SearchResultsReceiver
{

  private static final String LOG_TAG = "search";

  private SearchQuery query;
  private ResultState state = ResultState.NOT_INITIALIZED;
  private List<SqEntity> results;
  private SearchResultsReceiver delegate;

  public void setResultReceiver(SearchResultsReceiver delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public void reportNone(SearchQuery query)
  {
    Log.i(LOG_TAG, "ResultsBuffer.reportNone()");
    this.query = query;
    results = null;
    state = ResultState.NONE;

    if (delegate != null) {
      delegate.reportNone(query);
    }
  }

  @Override
  public void report(SearchQuery query, List<SqEntity> results)
  {
    Log.i(LOG_TAG, "ResultsBuffer.report()");
    this.query = query;
    this.results = results;
    state = ResultState.SOME;

    Log.i(LOG_TAG, "Delegate available? " + (delegate != null));
    if (delegate != null) {
      delegate.report(query, results);
    }
  }

  public SearchQuery getQuery()
  {
    return query;
  }

  public ResultState getState()
  {
    return state;
  }

  public List<SqEntity> getResults()
  {
    return results;
  }

}
