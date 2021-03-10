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

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import de.topobyte.apps.viewer.AppData;
import de.topobyte.apps.viewer.Database;
import de.topobyte.apps.viewer.search.ResultsBuffer;
import de.topobyte.apps.viewer.search.SearchQuery;
import de.topobyte.apps.viewer.search.SearchResultsReceiver;
import de.topobyte.apps.viewer.search.SearchWorker;
import de.topobyte.luqe.android.AndroidConnection;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.nomioc.luqe.model.SqEntity;

public class WorkerFragment extends Fragment implements SearchResultsReceiver
{

  private static final String LOG_TAG = "search";

  private SearchWorker searchWorker;

  private ResultsBuffer resultsBuffer;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.i(LOG_TAG, "WorkerFragment.onCreate()");

    setRetainInstance(true);

    String filename = Database.getDatabasePath(getActivity());

    resultsBuffer = new ResultsBuffer();

    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(filename, null);
    IConnection ldb = new AndroidConnection(db);
    AppData appData = AppData.getInstance(ldb);
    db.close();

    searchWorker = new SearchWorker(filename, this,
        appData.getSpatialIndexStreets(), appData.getSpatialIndexPois());
    new Thread(searchWorker).start();
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    Log.i(LOG_TAG, "WorkerFragment.onDestroy()");
    searchWorker.destroy();
  }

  public SearchWorker getSearchWorker()
  {
    return searchWorker;
  }

  public ResultsBuffer getResultsBuffer()
  {
    return resultsBuffer;
  }

  @Override
  public void reportNone(final SearchQuery query)
  {
    runOnUiThread(() -> resultsBuffer.reportNone(query));
  }

  @Override
  public void report(final SearchQuery query, final List<SqEntity> results)
  {
    runOnUiThread(() -> resultsBuffer.report(query, results));
  }

  private void runOnUiThread(Runnable runnable)
  {
    FragmentActivity activity = getActivity();
    if (activity == null) {
      return;
    }
    activity.runOnUiThread(runnable);
  }
}
