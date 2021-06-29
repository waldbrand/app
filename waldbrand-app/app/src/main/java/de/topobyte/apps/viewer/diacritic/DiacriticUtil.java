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

package de.topobyte.apps.viewer.diacritic;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class DiacriticUtil
{
  // http://www.rgagnon.com/javadetails/java-0456.html

  private static final Pattern pattern = Pattern
      .compile("\\p{InCombiningDiacriticalMarks}+");

  private static final String PLAIN_ASCII =
      "Ll"     // polish L and l
      ;

  private static final String UNICODE =
      "\u0141\u0142";

  public String simplify(String s)
  {
    if (s == null) {
      return null;
    }

    // First use normalizer
    String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
    s = pattern.matcher(temp).replaceAll("");

    // Then use manual mapping for some special cases
    StringBuilder sb = new StringBuilder();
    int n = s.length();
    for (int i = 0; i < n; i++) {
      char c = s.charAt(i);
      int pos = UNICODE.indexOf(c);
      if (pos > -1) {
        sb.append(PLAIN_ASCII.charAt(pos));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
