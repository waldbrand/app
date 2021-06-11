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

package de.topobyte.apps.viewer.search;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.waldbrandapp.R;
import de.topobyte.apps.viewer.CommonMenu;
import de.topobyte.apps.viewer.search.fragments.SearchFragment;
import de.topobyte.apps.viewer.search.fragments.WorkerFragment;

public class SearchActivity extends FullscreenAppCompatActivity
{

  public static final String EXTRA_X = "map-x";
  public static final String EXTRA_Y = "map-y";

  private static final String LOG_TAG = "search";

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.i(LOG_TAG, "SearchActivity.onCreate()");

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    setContentView(R.layout.activity_search);

    FragmentManager fm = getSupportFragmentManager();

    WorkerFragment workerFragment = (WorkerFragment) fm
        .findFragmentByTag("WorkerFragment");
    if (workerFragment != null) {
      Log.i(LOG_TAG, "SearchActivity: already have worker fragment");
    } else {
      Log.i(LOG_TAG, "SearchActivity: creating worker fragment");
      workerFragment = new WorkerFragment();
      fm.beginTransaction().add(workerFragment, "WorkerFragment")
          .commit();
    }

    if (savedInstanceState == null) {
      Log.i(LOG_TAG, "savedInstanceState == null");
      Bundle extras = getIntent().getExtras();
      Bundle bundle = new Bundle();
      bundle.putAll(extras);

      FragmentTransaction transaction = fm.beginTransaction();
      SearchFragment searchFragment = new SearchFragment();
      searchFragment.setWorkerFragment(workerFragment);
      searchFragment.setArguments(bundle);
      transaction.replace(R.id.layout, searchFragment,
          Common.FRAGMENT_SEARCH);
      transaction.commit();
    } else {
      Log.i(LOG_TAG, "savedInstanceState != null");
      SearchFragment searchFragment = (SearchFragment) fm
          .findFragmentById(R.id.layout);
      searchFragment.setWorkerFragment(workerFragment);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.options_menu_search, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    CommonMenu.setupVisibility(menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    boolean handled = CommonMenu.handleMenuItemSelected(this, item);
    handled |= handleMenuItemSelected(item);

    return handled;
  }

  private boolean handleMenuItemSelected(MenuItem item)
  {
    switch (item.getItemId()) {

      case android.R.id.home:
      case R.id.menu_map:
        finish();
        return true;

      default:
        return false;
    }
  }

}
