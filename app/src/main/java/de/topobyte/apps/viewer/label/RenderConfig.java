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

package de.topobyte.apps.viewer.label;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.slimjars.dist.gnu.trove.list.TIntList;
import com.slimjars.dist.gnu.trove.list.array.TIntArrayList;
import com.slimjars.dist.gnu.trove.map.TIntIntMap;
import com.slimjars.dist.gnu.trove.map.TObjectIntMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntIntHashMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;
import com.slimjars.dist.gnu.trove.map.hash.TObjectIntHashMap;
import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.topobyte.apps.viewer.Database;
import de.topobyte.apps.viewer.poi.Categories;
import de.topobyte.apps.viewer.poi.DrawingOrder;
import de.topobyte.apps.viewer.poi.Group;
import de.topobyte.apps.viewer.poi.PoiTypeInfo;
import de.topobyte.apps.viewer.poi.category.Category;
import de.topobyte.apps.viewer.poi.category.DatabaseCategory;
import de.topobyte.apps.viewer.poi.category.MapfileCategory;
import de.topobyte.luqe.android.AndroidConnection;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.mapocado.android.style.MapRenderConfig;
import de.topobyte.mapocado.styles.directory.StyleDirectory;
import de.topobyte.mapocado.styles.labels.elements.LabelContainer;
import de.topobyte.mapocado.styles.labels.elements.Rule;
import de.topobyte.nomioc.luqe.model.SqPoiType;

public class RenderConfig
{

  private final static String LOG_TAG = "pois";

  private final MapRenderConfig mapRenderConfig;

  private final PoiTypeInfo typesInfo;

  private final TObjectIntMap<String> typeToClassId;
  private final TIntIntMap typeIdToClassId;

  // This defines the rendering order
  private final List<RenderClass> renderClasses;
  private final TIntObjectHashMap<RenderClass> renderClassMap;

  private final TIntSet enabledIds = new TIntHashSet();

  private int idFactory = 0;
  private final Map<String, RenderClass> typeToClass = new HashMap<>();
  private final Map<RenderClass, String> classToType = new HashMap<>();

  public RenderConfig(MapRenderConfig mapRenderConfig, Context context)
  {
    this.mapRenderConfig = mapRenderConfig;

    WindowManager windowManager = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics displaymetrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getMetrics(displaymetrics);
    float density = displaymetrics.density;

    String filename = Database.getDatabasePath(context);
    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(filename, null);
    IConnection ldb = new AndroidConnection(db);

    StyleDirectory style = mapRenderConfig.getStyleDirectory();

    typesInfo = PoiTypeInfo.getInstance(ldb);
    typeToClassId = new TObjectIntHashMap<>(10, 0.5f, -1);
    typeIdToClassId = new TIntIntHashMap();

    db.close();

    // Keep a list of types that have not been covered by rules so that we
    // can later add them to the wildcard rule
    Set<String> left = new HashSet<>();
    for (SqPoiType type : typesInfo.getTypes()) {
      left.add(type.getName());
    }

    Rule othersRule = null; // Rule with the wildcard '*'

    for (Rule rule : style.getLabelRules()) {
      String type = rule.getKey();
      int typeId = typesInfo.getTypeId(type);
      if (typeId >= 0) { // valid database rule
        left.remove(type);
      } else { // invalid database rule,
        // so it's either a mapfile rule or the wildard
        Log.w(LOG_TAG, "typeId < 0: " + rule.getKey());
        if (rule.getKey().equals("*")) {
          othersRule = rule;
          continue;
        }
      }

      LabelContainer lc = style.getLabelStyles().get(rule);
      addRule(rule, rule.getKey(), typeId, lc, density);
    }

    if (othersRule != null) {
      LabelContainer lc = style.getLabelStyles().get(othersRule);
      for (String type : left) {
        int typeId = typesInfo.getTypeId(type);
        if (typeId < 0) {
          Log.w(LOG_TAG, "typeId < 0: " + type);
          continue;
        }
        Log.i(LOG_TAG, "Adding to others: '" + type + "'");
        addRule(othersRule, type, typeId, lc, density);
      }
    }

    renderClasses = new ArrayList<>();
    for (String type : DrawingOrder.order) {
      RenderClass renderClass = typeToClass.get(type);
      classToType.remove(renderClass);
      if (renderClass == null) {
        Log.w(LOG_TAG, "unmapped type, no class found: " + type);
        continue;
      }
      renderClasses.add(renderClass);
    }
    if (!classToType.isEmpty()) {
      for (Entry<RenderClass, String> entry : classToType.entrySet()) {
        Log.w(LOG_TAG,
            "unmapped class, not in order: " + entry.getValue());
      }
    }

    renderClassMap = new TIntObjectHashMap<>();
    for (RenderClass renderClass : renderClasses) {
      renderClassMap.put(renderClass.classId, renderClass);
    }

    reloadVisibility(context);
  }

  private void addRule(Rule rule, String type, int typeId, LabelContainer lc,
                       float density)
  {
    LabelClass labelClass = new LabelClass(lc.getType(), lc.getLabel(), 1,
        density);

    int classId = idFactory++;
    RenderClass renderClass = new RenderClass(classId, typeId,
        rule.getMinZoom(), rule.getMaxZoom(), labelClass);

    Log.i(LOG_TAG, "Mapping type '" + type + " (" + typeId
        + ")' to class '" + classId + "'");

    typeToClass.put(type, renderClass);
    classToType.put(renderClass, type);
    typeToClassId.put(type, classId);
    if (typeId >= 0) {
      typeIdToClassId.put(typeId, classId);
    }
  }

  public TIntHashSet getAllClassIds()
  {
    TIntHashSet set = new TIntHashSet();
    for (RenderClass renderClass : renderClasses) {
      set.add(renderClass.classId);
    }
    return set;
  }

  public TIntList getRelevantClassIds(int zoom)
  {
    TIntList ids = new TIntArrayList();
    for (RenderClass renderClass : renderClasses) {
      if (zoom >= renderClass.minZoom && zoom <= renderClass.maxZoom
          && enabledIds.contains(renderClass.classId)) {
        ids.add(renderClass.classId);
      }
    }
    return ids;
  }

  public TIntList getRelevantTypeIds(int zoom)
  {
    TIntList ids = new TIntArrayList();
    for (RenderClass renderClass : renderClasses) {
      if (zoom >= renderClass.minZoom && zoom <= renderClass.maxZoom
          && enabledIds.contains(renderClass.classId)) {
        if (renderClass.typeId >= 0) {
          ids.add(renderClass.typeId);
        }
      }
    }
    return ids;
  }

  public int getClassIdForTypeId(int typeId)
  {
    return typeIdToClassId.get(typeId);
  }

  public RenderClass get(int id)
  {
    return renderClassMap.get(id);
  }

  public boolean areHousenumbersRelevant(int zoom)
  {
    int classId = typeToClassId.get(Categories.TYPE_NAME_HOUSENUMBERS);
    RenderClass renderClass = renderClassMap.get(classId);
    return zoom >= renderClass.minZoom && zoom <= renderClass.maxZoom
        && enabledIds.contains(renderClass.classId);
  }

  public int getHousenumberClassId()
  {
    return typeToClassId.get(Categories.TYPE_NAME_HOUSENUMBERS);
  }

  public void reloadVisibility(Context context)
  {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(context);

    LabelDrawerPreferenceAbstraction pa = new LabelDrawerPreferenceAbstraction(
        prefs);
    LabelMode labelMode = pa.determineLabelMode();

    enabledIds.clear();

    if (labelMode == LabelMode.MINIMAL) {
      configureMinimal();
    } else {
      configureByConfig(prefs);
    }
  }

  public static final String[] places = new String[]{"city", "town",
      "village", "hamlet", "suburb", "borough", "quarter", "neighborhood"};

  private void configureMinimal()
  {
    for (String place : places) {
      int id = typeToClassId.get(place);
      enabledIds.add(id);
    }
  }

  private void configureByConfig(SharedPreferences prefs)
  {
    TIntSet other = new TIntHashSet();
    other.addAll(typeToClassId.valueCollection());
    other.remove(typeToClassId.get(Categories.TYPE_NAME_HOUSENUMBERS));

    for (String place : places) {
      int id = typeToClassId.get(place);
      enabledIds.add(id);
      other.remove(id);
    }

    Categories categories = Categories.getLabelInstance();
    for (Group group : categories.getGroups()) {
      for (Category category : group.getChildren()) {
        boolean enabled = categories.isEnabled(prefs, category);
        if (category instanceof DatabaseCategory) {
          DatabaseCategory dc = (DatabaseCategory) category;
          for (String type : dc.getIdentifiers()) {
            int id = typeToClassId.get(type);
            if (id < 0) {
              Log.w(LOG_TAG, "No id found for '" + type + "'");
              continue;
            }
            other.remove(id);
            if (enabled) {
              enabledIds.add(id);
            }
          }
        } else if (category instanceof MapfileCategory) {
          // MapfileCategory mc = (MapfileCategory) category;
          if (enabled) {
            int id = typeToClassId
                .get(Categories.TYPE_NAME_HOUSENUMBERS);
            enabledIds.add(id);
          }
        }
      }
    }

    boolean addOther = categories.isEnabled(prefs,
        Categories.PREF_KEY_OTHERS);
    if (addOther) {
      enabledIds.addAll(other);
    }
  }

  public File getSymbol(String image)
  {
    return mapRenderConfig.getSymbol(image);
  }

}
