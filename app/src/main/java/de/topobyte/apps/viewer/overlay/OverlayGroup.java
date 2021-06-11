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

package de.topobyte.apps.viewer.overlay;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import de.waldbrandapp.R;

public class OverlayGroup extends RelativeLayout
{

  private OverlayShower os;
  private OverlayGps og;
  private OverlayView ov;
  private ScrollView sv;

  public OverlayGroup(Context context)
  {
    super(context);
    inflateLayout(context);
    init();
  }

  public OverlayGroup(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    inflateLayout(context);
    init();
  }

  public OverlayGroup(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    inflateLayout(context);
    init();
  }

  private void inflateLayout(Context context)
  {
    LayoutInflater layoutInflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    layoutInflater.inflate(R.layout.overlay_group, this);

    os = findViewById(R.id.overlay_shower);
    og = findViewById(R.id.overlay_gps);
    sv = findViewById(R.id.overlay_view_scroll);
    ov = findViewById(R.id.overlay_view);
  }

  private void init()
  {
    sv.setVisibility(View.INVISIBLE);

    os.getButton().setOnClickListener(v -> {
      Log.i("overlay", "show clicked");
      showView();
    });

    ov.getCloseButton().setOnClickListener(v -> {
      Log.i("overlay", "hide clicked");
      hideView();
    });

    ov.getThemesButton().setOnClickListener(v -> listener.selectTheme());

    ov.getLayersButton().setOnClickListener(v -> listener.selectLayers());
  }

  @Override
  protected Parcelable onSaveInstanceState()
  {
    Parcelable state = super.onSaveInstanceState();
    SavedState savedState = new SavedState(state);

    savedState.controlsVisible = controlsVisible;

    return savedState;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state)
  {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    SavedState savedState = (SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());

    controlsVisible = savedState.controlsVisible;

    if (controlsVisible) {
      os.setVisibility(View.INVISIBLE);
      sv.setVisibility(View.VISIBLE);
    } else {
      os.setVisibility(View.VISIBLE);
      sv.setVisibility(View.INVISIBLE);
    }
  }

  private final int length = 500;
  private OverlayListener listener;
  private boolean animating = false;
  private boolean controlsVisible = false;

  protected synchronized boolean isAnimating()
  {
    return animating;
  }

  protected synchronized void setAnimating(boolean value)
  {
    animating = value;
  }

  protected void showView()
  {
    if (isAnimating() || controlsVisible) {
      return;
    }
    Log.i("overlay", "execute show");
    setAnimating(true);
    AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
    fadeOut.setAnimationListener(new DefaultAnimationListener()
    {

      @Override
      public void onAnimationEnd(Animation animation)
      {
        os.setVisibility(View.INVISIBLE);
      }
    });
    fadeOut.setDuration(length);
    os.startAnimation(fadeOut);

    AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
    fadeIn.setAnimationListener(new DefaultAnimationListener()
    {

      @Override
      public void onAnimationEnd(Animation animation)
      {
        sv.setVisibility(View.VISIBLE);
        setAnimating(false);
        controlsVisible = true;
      }
    });
    fadeIn.setDuration(length);
    sv.startAnimation(fadeIn);
  }

  protected void hideView()
  {
    if (isAnimating() || !controlsVisible) {
      return;
    }
    Log.i("overlay", "execute hide");
    setAnimating(true);
    AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
    fadeOut.setAnimationListener(new DefaultAnimationListener()
    {

      @Override
      public void onAnimationEnd(Animation animation)
      {
        sv.setVisibility(View.INVISIBLE);
      }
    });
    fadeOut.setDuration(length);
    sv.startAnimation(fadeOut);

    AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
    fadeIn.setAnimationListener(new DefaultAnimationListener()
    {

      @Override
      public void onAnimationEnd(Animation animation)
      {
        os.setVisibility(View.VISIBLE);
        setAnimating(false);
        controlsVisible = false;
      }
    });
    fadeIn.setDuration(length);
    os.startAnimation(fadeIn);
  }

  public void setOverlayListener(OverlayListener listener)
  {
    this.listener = listener;
  }

  public OverlayGps getOverlayGps()
  {
    return og;
  }

  static class SavedState extends BaseSavedState
  {
    boolean controlsVisible;

    SavedState(Parcelable superState)
    {
      super(superState);
    }

    private SavedState(Parcel in)
    {
      super(in);
      boolean[] values = new boolean[1];
      in.readBooleanArray(values);
      this.controlsVisible = values[0];
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
      super.writeToParcel(out, flags);
      out.writeBooleanArray(new boolean[]{controlsVisible});
    }

    public static final Parcelable.Creator<SavedState> CREATOR =
        new Parcelable.Creator<SavedState>()
        {
          @Override
          public SavedState createFromParcel(Parcel in)
          {
            return new SavedState(in);
          }

          @Override
          public SavedState[] newArray(int size)
          {
            return new SavedState[size];
          }
        };
  }
}
