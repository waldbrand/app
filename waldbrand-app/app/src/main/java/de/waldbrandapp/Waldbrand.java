package de.waldbrandapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Waldbrand
{

  private static List<String> labelTypes = new ArrayList<>();
  private static Map<String, String> typeToName = new HashMap<>();

  static {
    add("hydrant-underground", "Unterflurhydrant");
    add("hydrant-pillar", "Überflurhydrant");
    add("water-tank", "Wasserspeicher");
    add("fire-water-pond", "Löschwasserteich");
    add("suction-point", "Saugstelle");
    add("hydrant-pipe", "Flachspiegelbrunnen");
  }

  private static void add(String type, String name)
  {
    labelTypes.add(type);
    labelTypes.add(type + "2");
    typeToName.put(type, name);
    typeToName.put(type + "2", name);
  }

  public static List<String> getLabelTypes()
  {
    return labelTypes;
  }

  public static String getName(String type)
  {
    return typeToName.get(type);
  }

}
