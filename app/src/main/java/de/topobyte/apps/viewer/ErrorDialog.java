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

package de.topobyte.apps.viewer;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Locale;

import de.waldbrandapp.R;

public class ErrorDialog extends DialogFragment
{

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    String noSpace = getString(R.string.nospaceleft);
    String spaceNeeded1 = getString(R.string.spaceneeded1);
    String spaceNeeded2 = getString(R.string.spaceneeded2);
    String close = getString(R.string.close);

    String space = getHumanReadableSpace(ResourceConstantsHelper
        .getEstimatedNumberOfRequiredBytes());

    String message = String.format("%s %s %s %s", noSpace, spaceNeeded1,
        space, spaceNeeded2);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(message)
        .setCancelable(false)
        .setPositiveButton(close,
            (dialog, id) -> getActivity().finish());
    setCancelable(false);
    return builder.create();
  }

  private String getHumanReadableSpace(long bytes)
  {
    if (bytes > 1024 * 1024) {
      double x = bytes / 1024. / 1024.;
      String mb = getString(R.string.file_size_mb);
      return String.format(Locale.getDefault(), "%.2f %s", x, mb);
    }
    if (bytes > 1024) {
      double x = bytes / 1024. / 1024.;
      String kb = getString(R.string.file_size_kb);
      return String.format(Locale.getDefault(), "%.2f %s", x, kb);
    }
    return String.format(Locale.getDefault(), "%d %s", bytes,
        R.string.file_size_bytes);
  }

}
