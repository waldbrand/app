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

import com.slimjars.dist.gnu.trove.map.TIntObjectMap;
import com.slimjars.dist.gnu.trove.map.TObjectIntMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;
import com.slimjars.dist.gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.mapocado.mapformat.io.StringPool;

public class Waldbrand
{

  public static final int UNDERGROUND = 1;
  public static final int PILLAR = 2;
  public static final int WATER_TANK = 3;
  public static final int FIRE_WATER_POND = 4;
  public static final int SUCTION_POINT = 5;
  public static final int PIPE = 6;
  public static final int RETTUNGSPUNKT = 7;

  private static List<String> labelTypes = new ArrayList<>();
  private static Map<String, String> typeToName = new HashMap<>();
  private static TIntObjectMap<String> constantToName = new TIntObjectHashMap<>();
  private static TObjectIntMap<String> typeToConstant = new TObjectIntHashMap<>();
  private static StringPool stringPool;

  static {
    add2("hydrant-underground", "Unterflurhydrant", UNDERGROUND);
    add2("hydrant-pillar", "Überflurhydrant", PILLAR);
    add2("water-tank", "Wasserspeicher", WATER_TANK);
    add2("fire-water-pond", "Löschwasserteich", FIRE_WATER_POND);
    add2("suction-point", "Saugstelle", SUCTION_POINT);
    add2("hydrant-pipe", "Flachspiegelbrunnen", PIPE);
    add("rettungspunkt", "Rettungspunkt", RETTUNGSPUNKT);
  }

  private static void add(String type, String name, int constant)
  {
    add(type, name, constant, false);
  }

  private static void add2(String type, String name, int constant)
  {
    add(type, name, constant, true);
  }

  private static void add(String type, String name, int constant, boolean add2)
  {
    constantToName.put(constant, name);
    // the map file contains '<type>' for nodes and '<type>2' for ways
    String[] types = add2 ? new String[]{type, type + "2"} : new String[]{type};
    for (String t : types) {
      labelTypes.add(t);
      typeToName.put(t, name);
      typeToConstant.put(t, constant);
    }
  }

  public static List<String> getLabelTypes()
  {
    return labelTypes;
  }

  public static String getName(String type)
  {
    return typeToName.get(type);
  }

  public static String getName(int constant)
  {
    return constantToName.get(constant);
  }

  public static int getConstant(String type)
  {
    return typeToConstant.get(type);
  }

  public static void setStringPool(StringPool stringPool)
  {
    Waldbrand.stringPool = stringPool;
  }

  public static int getStringId(String string)
  {
    return stringPool.getId(string);
  }

}
