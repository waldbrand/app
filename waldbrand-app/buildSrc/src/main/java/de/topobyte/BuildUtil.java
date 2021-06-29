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

package de.topobyte;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.digest.DigestUtils;

public class BuildUtil
{

  public static long unGzippedSize(File file) throws IOException
  {
    GZIPInputStream is = new GZIPInputStream(new FileInputStream(file));
    long size = 0;

    while (is.available() > 0) {
      byte[] buf = new byte[1024];
      int read = is.read(buf);
      if (read > 0) size += read;
    }

    return size;
  }

  public static String md5(File file) throws IOException
  {
    InputStream is = new FileInputStream(file);
    return DigestUtils.md5Hex(is);
  }

}
