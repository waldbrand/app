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

package de.topobyte.apps.viewer.preferences;

import android.content.Context;
import android.util.AttributeSet;

import de.topobyte.android.seekbarpreference.SeekBarPreference;
import de.waldbrandapp.R;
import de.topobyte.apps.viewer.Constants;

public class MoveSpeedPreference extends SeekBarPreference
{

  public MoveSpeedPreference(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    messageText = getContext().getString(R.string.preferences_move_speed_desc);

    min = 0;
    max = Constants.MAX_MOVE_SPEED;
    currentValue = preferences.getInt(getKey(),
        Constants.DEFAULT_MOVE_SPEED);
  }

  @Override
  protected String getCurrentValueText(int progress)
  {
    return String.format(getContext().getString(R.string.preferences_move_speed_value), progress);
  }

}