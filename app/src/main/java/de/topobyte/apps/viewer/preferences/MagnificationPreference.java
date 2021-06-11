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

public class MagnificationPreference extends SeekBarPreference
{

  public MagnificationPreference(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    messageText = getContext().getString(
        R.string.preferences_magnification_desc);

    min = preferences.getInt(Constants.PREF_MIN_MAGNIFICATION, 80);
    max = preferences.getInt(Constants.PREF_MAX_MAGNIFICATION, 150);
    currentValue = preferences.getInt(this.getKey(),
        Constants.DEFAULT_MAGNIFICATION) - this.min;
  }

  @Override
  protected String getCurrentValueText(int progress)
  {
    return String.format(getContext()
            .getString(R.string.preferences_magnification_value), this.min + progress);
  }

}