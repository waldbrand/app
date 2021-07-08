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

package de.waldbrandapp;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.android.maps.utils.label.Label;

public class PoiLabel extends Label
{

  private final int waldbrandType;
  private final TIntObjectHashMap<String> tags;

  public PoiLabel(int x, int y, String text, int placeType, int id,
                  int waldbrandType, TIntObjectHashMap<String> tags)
  {
    super(x, y, text, placeType, id);
    this.waldbrandType = waldbrandType;
    this.tags = tags;
  }

  public int getWaldbrandType()
  {
    return waldbrandType;
  }

  public TIntObjectHashMap<String> getTags()
  {
    return tags;
  }

}
