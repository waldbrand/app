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

package de.topobyte.apps.viewer.map;

import android.content.Context;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import org.locationtech.jts.geom.Coordinate;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import de.topobyte.android.mapview.ImageManagerSourceRam;
import de.topobyte.android.mapview.ReferenceCountedBitmap;
import de.topobyte.apps.viewer.FileUtil;
import de.topobyte.jeography.core.Tile;
import de.topobyte.mapocado.android.mapfile.MapfileOpener;
import de.topobyte.mapocado.android.rendering.MapocadoImageSource;
import de.topobyte.mapocado.android.style.MapRenderConfig;
import de.topobyte.mapocado.android.style.StyleLinker;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.styles.directory.StyleDirectory;

public class Global
{

  private static Global instance = null;

  public static Global getInstance(Context context)
  {
    if (instance == null) {
      instance = new Global(context);
    }
    return instance;
  }

  private static final String LOG_TAG = "global";

  private final int displayHeight;
  private final int displayWidth;
  private float density;

  private Mapfile mapfile;
  private MapfileOpener opener;

  private Coordinate startupPosition;

  private final MapocadoImageSource imageSource;
  private final ImageManagerSourceRam<Tile, ReferenceCountedBitmap> imageManager;

  private String renderThemeKey;
  private MapRenderConfig mapRenderConfig;

  private Location location;

  private Global(Context context)
  {
    WindowManager windowManager = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics displaymetrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getMetrics(displaymetrics);
    displayHeight = displaymetrics.heightPixels;
    displayWidth = displaymetrics.widthPixels;
    density = displaymetrics.density;
    Log.i("display-metrics", "height: " + displayHeight);
    Log.i("display-metrics", "width: " + displayWidth);
    Log.i("display-metrics", "density: " + density);
    if (density < 1) {
      density = 1;
    }

    int cacheSize = 10;

    imageSource = new MapocadoImageSource();
    imageManager = new ImageManagerSourceRam<>(
        1, cacheSize, imageSource);
  }

  public ImageManagerSourceRam<Tile, ReferenceCountedBitmap> getImageManager()
  {
    return imageManager;
  }

  public void setMapFile(MapfileOpener opener) throws IOException,
      ClassNotFoundException
  {
    if (this.opener != null && this.opener.equals(opener)) {
      return;
    }
    this.opener = opener;
    mapfile = opener.open();
    imageSource.setMapFile(opener);
  }

  public Mapfile getMapFile()
  {
    return mapfile;
  }

  public float getDensity()
  {
    return density;
  }

  private float tileScaleFactor = 0;
  private float userScaleFactor = 0;

  public void setMagnification(float tileScaleFactor, float userScaleFactor)
  {
    if (this.tileScaleFactor == tileScaleFactor && this.userScaleFactor == userScaleFactor) {
      return;
    }
    this.tileScaleFactor = tileScaleFactor;
    this.userScaleFactor = userScaleFactor;
    imageSource.setTileScaleFactor(tileScaleFactor);
    imageSource.setMagnification(userScaleFactor);
    ensureCleanState();
  }

  public void setRenderConfig(MapRenderConfig mapRenderConfig)
  {
    this.mapRenderConfig = mapRenderConfig;
    imageSource.setRenderConfig(mapRenderConfig);
    ensureCleanState();

    triggerRenderThemeListeners(mapRenderConfig);
  }

  protected void updateCacheSize(int cacheSize)
  {
    imageManager.setCacheSize(cacheSize);
  }

  private void ensureCleanState()
  {
    imageManager.cancelJobs();
    imageManager.setIgnorePendingProductions();
    imageManager.clearCache();
  }

  private File getCurrentStyleDir(Context context)
  {
    File dir = FileUtil.newFile(context.getFilesDir(), "styles", "default");
    return dir;
  }

  private void ensureCurrentStyle(Context context) throws IOException
  {
    File dir = getCurrentStyleDir(context);
    dir.mkdirs();

    String asset = getRenderThemePath(renderThemeKey);
    InputStream input = context.getAssets().open(asset);

    FileUtil.unzipTo(dir, input);
  }

  private String getRenderThemePath(String renderThemeKey)
  {
    return renderThemeKey + ".zip";
  }

  public boolean setRenderTheme(Context context, String renderThemeKey)
      throws IOException
  {
    if (this.renderThemeKey != null
        && this.renderThemeKey.equals(renderThemeKey)) {
      return true;
    }
    this.renderThemeKey = renderThemeKey;

    // decompress style
    ensureCurrentStyle(context);

    // load
    Log.i(LOG_TAG, "loading style");
    StyleDirectory style = new StyleDirectory(getCurrentStyleDir(context));

    // initialize
    try {
      style.init();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      Log.e(LOG_TAG,
          "unable to read the configuration (ParserConfigurationException)");
      return false;
    } catch (SAXException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "unable to read the configuration (SAXException)");
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "unable to read the configuration (IOException)");
      return false;
    }

    if (!style.isInitialized()) {
      Log.e(LOG_TAG, "unable to initialize map style");
      return false;
    }

    MapRenderConfig mapRenderConfig = new MapRenderConfig(style);

    StyleLinker.createSlimClasses(style.getObjectClasses(), mapfile
        .getMetadata().getPoolForKeepKeys());

    setRenderConfig(mapRenderConfig);

    return true;
  }

  public MapRenderConfig getRenderConfig()
  {
    return mapRenderConfig;
  }

  public MapfileOpener getMapFileOpener()
  {
    return opener;
  }

  private final List<RenderThemeListener> renderThemeListeners = new ArrayList<>();

  public void addRenderThemeListener(RenderThemeListener l)
  {
    renderThemeListeners.add(l);
  }

  public void removeRenderThemeListener(RenderThemeListener l)
  {
    renderThemeListeners.remove(l);
  }

  private void triggerRenderThemeListeners(MapRenderConfig mapRenderConfig)
  {
    for (RenderThemeListener l : renderThemeListeners) {
      l.renderThemeChanged(mapRenderConfig);
    }
  }

  public Location getLocation()
  {
    return location;
  }

  public void setLocation(Location location)
  {
    this.location = location;
  }

  public Coordinate getStartupPosition()
  {
    return startupPosition;
  }

  public void setStartPosition(Coordinate startupPosition)
  {
    this.startupPosition = startupPosition;
  }

}
