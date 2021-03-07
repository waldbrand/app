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

package de.topobyte.apps.viewer.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import de.topobyte.apps.viewer.map.MapFragment;

public class MapLocationListener implements LocationListener
{

  private static final int TEN_SECONDS = 1000 * 10;

  private Location currentBestLocation = null;

  private final MapFragment mapFragment;

  public MapLocationListener(MapFragment mapFragment, Location backupLocation)
  {
    this.mapFragment = mapFragment;
    this.currentBestLocation = backupLocation;
  }

  @Override
  public void onLocationChanged(Location location)
  {
    Log.i("maploc", "location update");
    if (isBetterLocation(location)) {
      currentBestLocation = location;
      mapFragment.updateLocation(location);
    }
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras)
  {
    // ignore
  }

  @Override
  public void onProviderEnabled(String provider)
  {
    // ignore
  }

  @Override
  public void onProviderDisabled(String provider)
  {
    // ignore
  }

  protected boolean isBetterLocation(Location location)
  {
    if (currentBestLocation == null) {
      // A new location is always better than no location
      return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TEN_SECONDS;
    boolean isSignificantlyOlder = timeDelta < -TEN_SECONDS;
    boolean isNewer = timeDelta > 0;

    Log.i("maploc", "new location from provider: " + location.getProvider());
    Log.i("maploc", "timeDelta: " + timeDelta + ", sig.newer: "
        + isSignificantlyNewer + ", sig.older: " + isSignificantlyOlder
        + ", newer: " + isNewer);
    Log.i("maploc", "accuracy: " + location.getAccuracy());

    // If it's been more than ten seconds since the current location, use
    // the new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
      return true;
      // If the new location is more than ten seconds older, it must be
      // worse
    } else if (isSignificantlyOlder) {
      return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
        .getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(),
        currentBestLocation.getProvider());

    Log.i("maploc", "accuracyDelta: " + accuracyDelta
        + ", isLessAccurate: " + isLessAccurate + ", isMoreAccurate: "
        + isMoreAccurate + ", sig.less: " + isSignificantlyLessAccurate
        + ", same: " + isFromSameProvider);

    // Determine location quality using a combination of timeliness and
    // accuracy
    if (isMoreAccurate) {
      return true;
    } else if (isNewer && !isLessAccurate) {
      return true;
    } else if (isNewer && !isSignificantlyLessAccurate
        && isFromSameProvider) {
      return true;
    }
    return false;
  }

  private boolean isSameProvider(String provider1, String provider2)
  {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }
}
