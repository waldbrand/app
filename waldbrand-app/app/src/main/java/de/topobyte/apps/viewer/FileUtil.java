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

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil
{

  final static String LOG_TAG = "data";

  public static void ensureAssets(Context context, String assetFileName,
                                  String deviceFileName, int fileSize, boolean allowBigger)
      throws IOException
  {
    File local = new File(context.getFilesDir(), deviceFileName);
    Log.d(LOG_TAG, "file: " + local);
    long length = local.length();
    Log.d(LOG_TAG, "Asset: " + deviceFileName + ", local length: " + length);
    if ((!allowBigger && length == fileSize)
        || (allowBigger && length >= fileSize)) {
      Log.d(LOG_TAG, "file size ok");
      return;
    }
    Log.d(LOG_TAG, "copying file");
    // int assetSize = getAssetSize(context, fileName);
    // Log.d(LOG_TAG, "internal size is: " + assetSize);
    try {
      copyAsset(context, assetFileName, deviceFileName);
    } catch (IOException e) {
      if (local.exists()) {
        local.delete();
      }
      throw (e);
    }
  }

  public static void copyAsset(Context context, String assetFileName,
                               String deviceFileName) throws IOException
  {
    AssetManager assetManager = context.getAssets();
    InputStream in = null;
    InputStream zin = null;
    OutputStream out = null;

    in = assetManager.open(assetFileName);
    zin = new GZIPInputStream(in);
    File filesDir = context.getFilesDir();
    File outFile = new File(filesDir, deviceFileName);
    out = new FileOutputStream(outFile);
    copyFile(zin, out);
    zin.close();
    in.close();
    zin = null;
    in = null;
    out.flush();
    out.close();
    out = null;
  }

  public static int getAssetSize(Context context, String filename)
  {
    AssetManager assetManager = context.getAssets();
    InputStream in = null;
    int total = 0;
    try {
      in = assetManager.open(filename);
      byte[] buf = new byte[1024];
      while (true) {
        int read = in.read(buf);
        if (read < 0) {
          break;
        }
        total += read;
      }
      in.close();
    } catch (Exception e) {
      Log.e("data", "getAssetSize failed: " + e.getMessage());
      Log.e("data", "exception type: " + e.getClass());
    }
    return total;
  }

  private static void copyFile(InputStream in, OutputStream out)
      throws IOException
  {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
  }

  public static File newFile(File dir, String... parts)
  {
    File file = dir;
    for (String part : parts) {
      file = new File(file, part);
    }
    return file;
  }

  public static File newFileSlashDelimitedPath(File dir, String path)
  {
    String[] parts = path.split("/");
    return newFile(dir, parts);
  }

  public static void unzipTo(File dir, InputStream input) throws IOException
  {
    dir.mkdirs();

    ZipInputStream zip = new ZipInputStream(input);

    ZipEntry entry = null;
    while ((entry = zip.getNextEntry()) != null) {
      String path = entry.getName();
      if (entry.isDirectory()) {
        File subdir = FileUtil.newFileSlashDelimitedPath(dir, path);
        Log.i(LOG_TAG, "Creating dir: " + subdir);
        subdir.mkdirs();
      } else {
        byte[] buf = new byte[4096];
        File file = FileUtil.newFileSlashDelimitedPath(dir, path);
        Log.i(LOG_TAG, "Unzipping " + file);
        FileOutputStream fos = new FileOutputStream(file);
        for (int c = zip.read(buf); c != -1; c = zip.read(buf)) {
          fos.write(buf, 0, c);
        }
        fos.close();
      }
      zip.closeEntry();
    }
    zip.close();
  }

  public static void wipeFiles(Context context)
  {
    File filesDir = context.getFilesDir();
    String[] fileList = context.fileList();
    for (String filename : fileList) {
      File file = new File(filesDir, filename);
      if (file.isDirectory()) {
        recurse(file);
      } else {
        Log.i(LOG_TAG, "file here: " + filename);
        boolean deleted = context.deleteFile(filename);
        Log.i(LOG_TAG, "successfully deleted: " + deleted);
      }
    }
  }

  private static void recurse(File dir)
  {
    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        recurse(file);
      } else {
        handle(file);
      }
    }
    boolean delete = dir.delete();
    Log.i(LOG_TAG, "deleting directory '" + dir + "'");
    Log.i(LOG_TAG, "successfully deleted: " + delete);
  }

  private static void handle(File file)
  {
    Log.i(LOG_TAG, "recursed to file: " + file);
    boolean delete = file.delete();
    Log.i(LOG_TAG, "successfully deleted: " + delete);
  }

  public static boolean hasEnoughSpaceAtLocation(File file, int size)
  {
    long space = file.getFreeSpace();
    return space >= size;
  }

}
