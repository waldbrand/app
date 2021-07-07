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

  private static List<String> labelTypes = new ArrayList<>();
  private static Map<String, String> typeToName = new HashMap<>();
  private static TIntObjectMap<String> constantToName = new TIntObjectHashMap<>();
  private static TObjectIntMap<String> typeToConstant = new TObjectIntHashMap<>();
  private static StringPool stringPool;

  static {
    add("hydrant-underground", "Unterflurhydrant", UNDERGROUND);
    add("hydrant-pillar", "Überflurhydrant", PILLAR);
    add("water-tank", "Wasserspeicher", WATER_TANK);
    add("fire-water-pond", "Löschwasserteich", FIRE_WATER_POND);
    add("suction-point", "Saugstelle", SUCTION_POINT);
    add("hydrant-pipe", "Flachspiegelbrunnen", PIPE);
  }

  private static void add(String type, String name, int constant)
  {
    constantToName.put(constant, name);
    for (String t : new String[]{type, type + "2"}) {
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
