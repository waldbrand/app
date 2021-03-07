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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import de.topobyte.android.common.resources.hasviews.ViewHasViews;
import de.topobyte.apps.maps.atestcity.R;

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

    Privacy.setupVisibility(getContext(), new ViewHasViews(view));
    Privacy.setupLinks(new ViewHasViews(view));

    builder.setTitle(R.string.consent_title);
    builder.setPositiveButton(R.string.consent_accept,
        new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int id)
          {
            ((OnAnswerReceived) getActivity()).onConsentDialogAccepted();
          }
        });

    builder.setNegativeButton(R.string.consent_decline,
        new DialogInterface.OnClickListener()
        {

          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            ((OnAnswerReceived) getActivity()).onConsentDialogDenied();
          }
        });

    return builder.create();
  }

}
