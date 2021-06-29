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

import android.database.sqlite.SQLiteDatabase;

import androidx.fragment.app.Fragment;

import de.topobyte.apps.viewer.Database;
import de.topobyte.apps.viewer.search.DatabaseAccess;
import de.topobyte.luqe.android.AndroidConnection;
import de.topobyte.luqe.iface.IConnection;

public class BaseGeocodingFragment extends Fragment implements DatabaseAccess
{

  private String filename = null;
  protected IConnection ldb = null;
  protected SQLiteDatabase db = null;

  @Override
  public void onResume()
  {
    super.onResume();
    if (filename == null) {
      filename = Database.getDatabasePath(getActivity());
    }
  }

  public synchronized void openDatabase()
  {
    if (db == null) {
      db = SQLiteDatabase.openOrCreateDatabase(this.filename, null);
      ldb = new AndroidConnection(db);
    }
  }

  public synchronized void closeDatabase()
  {
    db.close();
    db = null;
  }

  @Override
  public synchronized boolean resourceAvailable()
  {
    return db != null;
  }

  @Override
  public synchronized IConnection getDatabase()
  {
    return ldb;
  }

}
