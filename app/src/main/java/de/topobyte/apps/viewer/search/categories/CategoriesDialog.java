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

package de.topobyte.apps.viewer.search.categories;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.poi.Categories;
import de.topobyte.apps.viewer.search.Common;
import de.topobyte.apps.viewer.search.fragments.SearchFragment;

public class CategoriesDialog extends DialogFragment
{

  private final int BUTTON_ALL = DialogInterface.BUTTON_NEUTRAL;
  private final int BUTTON_NONE = DialogInterface.BUTTON_NEGATIVE;

  private CategoryList list;

  @Override
  @SuppressLint("InflateParams")
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    LayoutInflater inflater = getActivity().getLayoutInflater();

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    final View view = inflater.inflate(R.layout.dialog_categories, null);
    builder.setView(view);

    list = view.findViewById(R.id.list);

    builder.setTitle(R.string.dialog_select_categories_title);

    builder.setPositiveButton(android.R.string.ok,
        new DialogInterface.OnClickListener()
        {

          @Override
          public void onClick(DialogInterface dialog, int which)
          {

          }
        });

    builder.setNeutralButton(R.string.all,
        new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int id)
          {

          }
        });

    builder.setNegativeButton(R.string.none,
        new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int id)
          {

          }
        });

    return builder.create();
  }

  @Override
  public void onStart()
  {
    super.onStart();

    AlertDialog dialog = (AlertDialog) getDialog();
    Button buttonAll = dialog.getButton(BUTTON_ALL);
    Button buttonNone = dialog.getButton(BUTTON_NONE);

    buttonAll.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        selectAll();
      }
    });

    buttonNone.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        selectNone();
      }
    });

  }

  protected void selectAll()
  {
    Categories categories = Categories.getSearchInstance();
    categories.pickAll(getActivity());
    list.getCategoriesAdapter().notifyDataSetInvalidated();
  }

  protected void selectNone()
  {
    Categories categories = Categories.getSearchInstance();
    categories.pickNone(getActivity());
    list.getCategoriesAdapter().notifyDataSetInvalidated();
  }

  @Override
  public void onDismiss(DialogInterface dialog)
  {
    super.onDismiss(dialog);
    if (getActivity() == null) {
      return;
    }
    SearchFragment searchFragment = (SearchFragment) getFragmentManager()
        .findFragmentByTag(Common.FRAGMENT_SEARCH);
    if (searchFragment == null) {
      return;
    }
    searchFragment.reloadSelectedCategories();
  }
}
