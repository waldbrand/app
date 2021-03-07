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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import de.topobyte.apps.maps.atestcity.R;

public class TipsAndTricksDialog extends DialogFragment
{

  private static final String BUNDLE_INDEX = "index";

  private int index = 0;

  private final int[] texts = new int[]{R.string.dialog_startup_text1,
      R.string.dialog_startup_text2, R.string.dialog_startup_text3,
      R.string.dialog_startup_text4, R.string.dialog_startup_text5,
      R.string.dialog_startup_text6};

  private final int BUTTON_NEXT = DialogInterface.BUTTON_NEUTRAL;
  private final int BUTTON_PREV = DialogInterface.BUTTON_NEGATIVE;

  @Override
  @SuppressLint("InflateParams")
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    if (savedInstanceState != null) {
      index = savedInstanceState.getInt(BUNDLE_INDEX);
    }

    LayoutInflater inflater = getActivity().getLayoutInflater();

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    final View view = inflater.inflate(R.layout.dialog_startup, null);
    builder.setView(view);

    CheckBox check = view.findViewById(R.id.check);
    check.setChecked(areTipsEnabledOnStartup());

    builder.setTitle(R.string.dialog_startup_title);
    builder.setPositiveButton(android.R.string.ok,
        new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int id)
          {
            CheckBox check = view
                .findViewById(R.id.check);
            setTipsEnabledOnStartup(check.isChecked());
          }
        });

    builder.setNeutralButton(R.string.next,
        new DialogInterface.OnClickListener()
        {

          @Override
          public void onClick(DialogInterface dialog, int which)
          {

          }
        });
    builder.setNegativeButton(R.string.previous,
        new DialogInterface.OnClickListener()
        {

          @Override
          public void onClick(DialogInterface dialog, int which)
          {

          }
        });

    return builder.create();
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    outState.putInt(BUNDLE_INDEX, index);
  }

  @Override
  public void onStart()
  {
    super.onStart();

    AlertDialog dialog = (AlertDialog) getDialog();
    Button buttonNext = dialog.getButton(BUTTON_NEXT);
    Button buttonPrev = dialog.getButton(BUTTON_PREV);

    buttonNext.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        incrementIndex();
        updateText();
        updateUI();
      }
    });

    buttonPrev.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        decrementIndex();
        updateText();
        updateUI();
      }
    });

    updateText();
    updateUI();
  }

  protected void incrementIndex()
  {
    if (index < texts.length - 1) {
      index++;
    }
  }

  protected void decrementIndex()
  {
    if (index > 0) {
      index--;
    }
  }

  protected void updateText()
  {
    TextView textView = getDialog().findViewById(R.id.text);
    textView.setText(texts[index]);
  }

  protected void updateUI()
  {
    AlertDialog dialog = (AlertDialog) getDialog();
    Button buttonNext = dialog.getButton(BUTTON_NEXT);
    Button buttonPrev = dialog.getButton(BUTTON_PREV);

    buttonPrev.setEnabled(index != 0);
    buttonNext.setEnabled(index != texts.length - 1);
  }

  protected boolean areTipsEnabledOnStartup()
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());

    return preferences.getBoolean(Constants.PREF_TIPS_AT_STARTUP,
        Constants.DEFAULT_TIPS_AT_STARTUP);
  }

  protected void setTipsEnabledOnStartup(boolean enabled)
  {
    SharedPreferences preferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());

    Editor editor = preferences.edit();
    editor.putBoolean(Constants.PREF_TIPS_AT_STARTUP, enabled);
    editor.commit();
  }
}