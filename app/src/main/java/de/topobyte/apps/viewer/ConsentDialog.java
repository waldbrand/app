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

package de.topobyte.apps.viewer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.waldbrandapp.R;

public class ConsentDialog extends DialogFragment
{

  public interface OnAnswerReceived
  {
    void onConsentDialogAccepted();

    void onConsentDialogDenied();
  }

  @Override
  @SuppressLint("InflateParams")
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    LayoutInflater inflater = getActivity().getLayoutInflater();

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    final View view = inflater.inflate(R.layout.dialog_consent, null);
    builder.setView(view);

    builder.setTitle(R.string.consent_title);
    builder.setPositiveButton(R.string.consent_accept,
        (dialog, id) -> ((OnAnswerReceived) getActivity()).onConsentDialogAccepted());

    builder.setNegativeButton(R.string.consent_decline,
        (dialog, which) -> ((OnAnswerReceived) getActivity()).onConsentDialogDenied());

    return builder.create();
  }

}
