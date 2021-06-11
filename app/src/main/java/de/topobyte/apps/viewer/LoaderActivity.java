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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import de.topobyte.android.loader2.LoaderDialog;
import de.topobyte.android.loader3.TaskFragment;
import de.waldbrandapp.R;

public abstract class LoaderActivity extends AppCompatActivity
    implements TaskFragment.TaskCallbacks, ConsentDialog.OnAnswerReceived
{

  private static final String TAG_LOAD_DIALOG = "load_dialog";
  private static final String TAG_TASK_FRAGMENT = "task_fragment";

  private static final String BUNDLE_DONE = "done";
  private static final String BUNDLE_SUCCESS = "success";

  private TaskFragment taskFragment;

  private LoaderDialog loaderDialog = null;

  protected boolean initializationDone = false;
  protected boolean initializationSucceeded = false;

  private boolean resumed = false;

  public abstract TaskFragment createTaskFragment();

  public abstract void loaderFinished();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    Log.i("loader", "onCreate");
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(BUNDLE_DONE)) {
        initializationDone = savedInstanceState.getBoolean(BUNDLE_DONE);
      }
      if (savedInstanceState.containsKey(BUNDLE_SUCCESS)) {
        initializationSucceeded = savedInstanceState.getBoolean(BUNDLE_SUCCESS);
      }
    }

    Log.i("loader", "initializationDone? " + initializationDone);
    Log.i("loader", "initializationSucceeded? " + initializationSucceeded);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

    boolean consentGiven = preferences.getBoolean(Constants.PREF_CONSENT_GIVEN, false);
    boolean showDialog = !consentGiven;

    if (showDialog) {
      if (savedInstanceState == null) {
        showConsentDialog();
      }
    } else {
      startLoading();
    }
  }

  private void showConsentDialog()
  {
    FragmentManager fm = getSupportFragmentManager();
    ConsentDialog dialog = new ConsentDialog();
    dialog.setCancelable(false);
    dialog.show(fm, null);
  }


  public void onConsentDialogAccepted()
  {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(Constants.PREF_CONSENT_GIVEN, true);
    editor.commit();

    startLoading();
  }

  public void onConsentDialogDenied()
  {
    finish();
  }

  private void startLoading()
  {
    FragmentManager fm = getSupportFragmentManager();

    taskFragment = (TaskFragment) fm
        .findFragmentByTag(TAG_TASK_FRAGMENT);
    if (taskFragment != null) {
      Log.i("loader", "task fragment is not null");
      initializationDone = taskFragment.hasFinished();
      initializationSucceeded = taskFragment.getResult();
    }

    if (!initializationDone) {
      loaderDialog = (LoaderDialog) fm.findFragmentByTag(TAG_LOAD_DIALOG);

      if (loaderDialog == null) {
        Log.i("loader", "dialog is null");
        showLoadDialog();
      }

      // If the Fragment is non-null, then it is currently being
      // retained across a configuration change.
      if (taskFragment == null) {
        Log.i("loader", "task fragment is null");
        taskFragment = createTaskFragment();
        fm.beginTransaction().add(taskFragment, TAG_TASK_FRAGMENT)
            .commit();
      }
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    resumed = true;

    if (initializationDone) {
      tryToDismissLoaderDialog();
    }
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    resumed = false;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    resumed = false;
    Log.i("loader", "onSaveInstanceState");

    outState.putBoolean(BUNDLE_DONE, initializationDone);
    outState.putBoolean(BUNDLE_SUCCESS, initializationSucceeded);
  }

  private void showLoadDialog()
  {
    loaderDialog = LoaderDialog.newInstance(R.string.loading_data);
    loaderDialog.setCancelable(false);
    loaderDialog.show(getSupportFragmentManager(), TAG_LOAD_DIALOG);
  }

  @Override
  public void onTaskFragmentPostExecute(boolean result)
  {
    Log.i("loader", "onPostExecute");
    initializationDone = true;
    initializationSucceeded = result;
    Log.i("loader", "beforeOnSaveInstance? " + resumed);
    if (resumed) {
      Log.i("loader", "dialog != null? " + (loaderDialog != null));
      tryToDismissLoaderDialog();
      loaderFinished();
    }
  }

  private void tryToDismissLoaderDialog()
  {
    if (loaderDialog != null) {
      loaderDialog.dismiss();
    }
  }

}