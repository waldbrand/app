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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import de.topobyte.android.fullscreen.FullscreenUtil;

public class FullscreenAppCompatActivity extends AppCompatActivity
{

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    manageFullScreen();
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    manageFullScreen();
  }

  private void manageFullScreen()
  {
    // check if the full screen mode should be activated
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(this);
    FullscreenUtil.setupFullScreen(this, preferences, getWindow());
  }

}
