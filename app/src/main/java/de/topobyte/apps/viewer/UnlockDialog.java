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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.freemium.FreemiumUtil;
import de.topobyte.apps.viewer.search.HelpSearchDialog;

public class UnlockDialog extends DialogFragment
{

  @Override
  @SuppressLint("InflateParams")
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();

    builder.setTitle(R.string.dialog_unlock_title);
    View view = inflater.inflate(R.layout.dialog_unlock, null);
    builder.setView(view);

    builder.setPositiveButton(android.R.string.ok,
        new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int id)
          {
            Intent unlockIntent = FreemiumUtil.createUnlockIntent();
            startActivity(unlockIntent);
          }
        });

    builder.setNegativeButton(android.R.string.cancel,
        new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int id)
          {
            // ignore
          }
        });

    Button button1 = view.findViewById(R.id.button1);
    button1.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        HelpSearchDialog helpDialog = new HelpSearchDialog();
        helpDialog
            .show(getActivity().getSupportFragmentManager(), null);
      }
    });

    Button button2 = view.findViewById(R.id.button2);
    button2.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        Intent unlockIntent = FreemiumUtil.createUnlockIntent();
        startActivity(unlockIntent);
      }
    });

    return builder.create();
  }
}