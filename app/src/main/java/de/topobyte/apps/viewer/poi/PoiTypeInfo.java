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

package de.topobyte.apps.viewer.poi;

import android.util.Log;

import com.slimjars.dist.gnu.trove.map.TObjectIntMap;
import com.slimjars.dist.gnu.trove.map.hash.TObjectIntHashMap;
import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import java.util.List;

import de.topobyte.apps.viewer.poi.category.Category;
import de.topobyte.apps.viewer.poi.category.DatabaseCategory;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.model.SqPoiType;

public class PoiTypeInfo
{

  private static PoiTypeInfo instance = null;

  public static PoiTypeInfo getInstance(IConnection ldb)
  {
    if (instance == null) {
      instance = new PoiTypeInfo();
      instance.build(ldb);
    }
    return instance;
  }

  // Available database types
  private List<SqPoiType> types;
  // Maps database type keys to database type ids
  private TObjectIntMap<String> typeToTypeId;

  private final TIntSet peakIds = new TIntHashSet();

  private void build(IConnection ldb)
  {
    typeToTypeId = new TObjectIntHashMap<>(10, 0.5f, -1);

    try {
      types = Dao.getTypes(ldb);
      for (SqPoiType type : types) {
        typeToTypeId.put(type.getName(), type.getId());
      }
    } catch (QueryException e) {
      Log.e("render-config", "Error while retrieving types", e);
    }

    peakIds.add(typeToTypeId.get("peak"));
    peakIds.add(typeToTypeId.get("volcano"));
  }

  public List<SqPoiType> getTypes()
  {
    return types;
  }

  // Returns -1 if not found
  public int getTypeId(String type)
  {
    return typeToTypeId.get(type);
  }

  TIntSet getPeakIds()
  {
    return peakIds;
  }

  public TIntSet determineOthers(Categories categories)
  {
    TIntSet ids = new TIntHashSet();
    for (SqPoiType type : types) {
      ids.add(type.getId());
    }

    for (Group group : categories.getGroups()) {
      for (Category category : group.getChildren()) {
        if (category instanceof DatabaseCategory) {
          DatabaseCategory dcat = (DatabaseCategory) category;
          for (String type : dcat.getIdentifiers()) {
            int id = typeToTypeId.get(type);
            if (id < 0) {
              continue;
            }
            ids.remove(id);
          }
        }
      }
    }

    return ids;
  }
}
