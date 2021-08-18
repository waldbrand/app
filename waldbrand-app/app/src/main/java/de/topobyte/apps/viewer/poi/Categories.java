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

package de.topobyte.apps.viewer.poi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.topobyte.apps.viewer.poi.category.Category;
import de.topobyte.apps.viewer.poi.category.DatabaseCategory;
import de.topobyte.apps.viewer.poi.category.MapfileCategory;
import de.topobyte.apps.viewer.poi.category.SpecialCategory;
import de.waldbrandapp.R;

public class Categories
{

  public static final String PREF_PREFIX_LABELS = "d:";
  public static final String PREF_PREFIX_SEARCH = "s:";

  public static final String PREF_KEY_STREETS = "c:streets";
  public static final String PREF_KEY_OTHERS = "c:other";
  public static final String TYPE_NAME_HOUSENUMBERS = "housenumbers";
  public static final String TYPE_NAME_HOUSENUMBERS_BUILDINGS = "housenumbers-buildings";

  private static Categories labelInstance = null;
  private static Categories searchInstance = null;

  private enum Type
  {
    Labels, Search, All
  }

  public static Categories getLabelInstance()
  {
    if (labelInstance == null) {
      labelInstance = new Categories(PREF_PREFIX_LABELS, Type.Labels);
    }
    return labelInstance;
  }

  public static Categories getSearchInstance()
  {
    if (searchInstance == null) {
      searchInstance = new Categories(PREF_PREFIX_SEARCH, Type.Search);
    }
    return searchInstance;
  }

  public static Categories getAllInstance()
  {
    if (labelInstance == null) {
      labelInstance = new Categories("a:", Type.All);
    }
    return labelInstance;
  }

  private List<Group> groups;
  private Set<Group> special;
  private TIntSet specialIndices;
  private Map<Category, Group> backlinks;

  private final String prefix;

  private Categories(String prefix, Type type)
  {
    this.prefix = prefix;

    prepareListData(type);

    initSpecial();

    initBacklinks();
  }

  private void initBacklinks()
  {
    backlinks = new HashMap<>();
    for (Group group : groups) {
      for (Category category : group.getChildren()) {
        backlinks.put(category, group);
      }
    }
  }

  private void initSpecial()
  {
    specialIndices = new TIntHashSet();
    for (int i = 0; i < groups.size(); i++) {
      if (special.contains(groups.get(i))) {
        specialIndices.add(i);
      }
    }
  }

  private Category streets;
  private Group foodDrink;

  private void prepareListData(Type type)
  {
    groups = new ArrayList<>();
    special = new HashSet<>();

    if (type == Type.Search || type == Type.All) {
      Group orientation = new Group(R.string.cat_orientation);
      groups.add(orientation);
      streets = new SpecialCategory(R.string.cat_streets,
          PREF_KEY_STREETS);
      orientation.add(streets);
      orientation.add(new DatabaseCategory(R.string.cat_places,
          "c:places", "city", "town", "village", "hamlet", "island", "islet",
          "borough", "suburb", "quarter", "neighborhood"));
    }

    Group waldbrand = new Group(R.string.cat_waldbrand);
    groups.add(waldbrand);
    waldbrand.add(new DatabaseCategory(R.string.cat_underground, "c:underground",
        "hydrant-underground", "hydrant-underground2"));
    waldbrand.add(new DatabaseCategory(R.string.cat_pillar, "c:pillar",
        "hydrant-pillar", "hydrant-pillar2"));
    waldbrand.add(new DatabaseCategory(R.string.cat_pipe, "c:pipe",
        "hydrant-pipe", "hydrant-pipe2"));
    waldbrand.add(new DatabaseCategory(R.string.cat_water_tank, "c:water_tank",
        "water-tank", "water-tank2"));
    waldbrand.add(new DatabaseCategory(R.string.cat_water_ponds, "c:water_pond",
        "fire-water-pond", "fire-water-pond2"));
    waldbrand.add(new DatabaseCategory(R.string.cat_suction_point, "c:suction_point",
        "suction-point", "suction-point2"));
    waldbrand
        .add(new DatabaseCategory(R.string.cat_emergency_access_point, "c:emergency_access_point",
            "rettungspunkt"));

    Group transportation = new Group(R.string.cat_transportation);
    groups.add(transportation);
    transportation.add(new DatabaseCategory(R.string.cat_railstop,
        "c:railwaystop", "railwaystation", "railwayhalt"));
    transportation.add(new DatabaseCategory(R.string.cat_tramstop,
        "c:tramstop", "tramstop"));
    transportation.add(new DatabaseCategory(R.string.cat_busstop,
        "c:busstop", "busstop", "busstation"));

    Group nature = new Group(R.string.cat_nature);
    groups.add(nature);
    nature.add(new DatabaseCategory(R.string.cat_parks, "c:park", "park"));
    nature.add(new DatabaseCategory(R.string.cat_water, "c:water", "water"));
    nature.add(new DatabaseCategory(R.string.cat_peaks, "c:peak", "peak",
        "volcano"));
    nature.add(new DatabaseCategory(R.string.cat_playgrounds,
        "c:playground", "playground"));
    nature.add(new DatabaseCategory(R.string.cat_cemeteries, "c:cemetery",
        "cemetery"));

    foodDrink = new Group(R.string.cat_foods_drinks);
    groups.add(foodDrink);
    foodDrink.add(new DatabaseCategory(R.string.cat_restaurant,
        "c:restaurants", "restaurant"));
    foodDrink.add(new DatabaseCategory(R.string.cat_fastfood, "c:fastfood",
        "fastfood"));
    foodDrink
        .add(new DatabaseCategory(R.string.cat_cafe, "c:cafes", "cafe"));
    foodDrink.add(new DatabaseCategory(R.string.cat_bakeries, "c:bakeries",
        "bakery"));
    foodDrink.add(new DatabaseCategory(R.string.cat_pubs, "c:pubs", "pub",
        "biergarten"));
    foodDrink.add(new DatabaseCategory(R.string.cat_bars, "c:bars", "bar"));
    foodDrink.add(new DatabaseCategory(R.string.cat_nightclubs,
        "c:nightclubs", "nightclub"));

    Group culture = new Group(R.string.cat_arts_culture);
    groups.add(culture);
    culture.add(new DatabaseCategory(R.string.cat_museums, "c:museums",
        "museum", "gallery"));
    culture.add(new DatabaseCategory(R.string.cat_attraction,
        "c:attractions", "attraction"));
    culture.add(new DatabaseCategory(R.string.cat_memorial, "c:memorial",
        "memorial"));
    culture.add(new DatabaseCategory(R.string.cat_religious, "c:religious",
        "christian", "muslim", "jewish"));
    culture.add(new DatabaseCategory(R.string.cat_theatres, "c:theaters",
        "theatre"));
    culture.add(new DatabaseCategory(R.string.cat_cinemas, "c:cinemas",
        "cinema"));

    Group accommodation = new Group(R.string.cat_accommodation);
    groups.add(accommodation);
    accommodation.add(new DatabaseCategory(R.string.cat_hotels, "c:hotels",
        "hotel"));
    accommodation.add(new DatabaseCategory(R.string.cat_hostels,
        "c:hostels", "hostel"));
    accommodation.add(new DatabaseCategory(R.string.cat_other,
        "c:other-accommodation", "guesthouse", "campsite"));

    Group education = new Group(R.string.cat_education);
    groups.add(education);
    education.add(new DatabaseCategory(R.string.cat_kindergarten,
        "c:kindergarten", "kindergarten"));
    education.add(new DatabaseCategory(R.string.cat_school, "c:school",
        "school"));
    education.add(new DatabaseCategory(R.string.cat_universty,
        "c:university", "university"));
    education.add(new DatabaseCategory(R.string.cat_library, "c:library",
        "library"));

    Group healthcare = new Group(R.string.cat_health);
    groups.add(healthcare);
    healthcare.add(new DatabaseCategory(R.string.cat_hospitals,
        "c:hospitals", "hospital"));
    healthcare.add(new DatabaseCategory(R.string.cat_doctors, "c:doctors",
        "doctor"));
    healthcare.add(new DatabaseCategory(R.string.cat_dentists,
        "c:dentists", "dentist"));
    healthcare.add(new DatabaseCategory(R.string.cat_pharmacies,
        "c:pharmacies", "pharmacy"));
    healthcare.add(new DatabaseCategory(R.string.cat_veterinary,
        "c:veterinary", "veterinary"));

    Group shops = new Group(R.string.cat_shops);
    groups.add(shops);
    shops.add(new DatabaseCategory(R.string.cat_supermarkets,
        "c:supermarkets", "supermarket", "chemist"));
    shops.add(new DatabaseCategory(R.string.cat_convenience,
        "c:convenience", "convenience"));
    shops.add(new DatabaseCategory(R.string.cat_diy, "c:diy",
        "doityourself"));
    shops.add(new DatabaseCategory(R.string.cat_clothes, "c:clothes",
        "clothes", "boutique"));
    shops.add(new DatabaseCategory(R.string.cat_shoes, "c:shoes", "shoes"));
    shops.add(new DatabaseCategory(R.string.cat_jewelry, "c:jewelry",
        "jewelry"));
    shops.add(new DatabaseCategory(R.string.cat_electronics,
        "c:electronics", "electronics"));
    shops.add(new DatabaseCategory(R.string.cat_books, "c:books",
        "bookshop"));
    shops.add(new DatabaseCategory(R.string.cat_flowers, "c:florist",
        "florist"));
    shops.add(new DatabaseCategory(R.string.cat_carshops, "c:carshop",
        "carshop"));
    shops.add(new DatabaseCategory(R.string.cat_bikeshops, "c:bikeshop",
        "bikeshop"));
    shops.add(new DatabaseCategory(R.string.cat_furniture, "c:furniture",
        "furniture"));
    shops.add(new DatabaseCategory(R.string.cat_other, "c:other-shops",
        "other-shops", "beauty", "deli", "travel_agency", "optician",
        "beverages"));

    Group misc = new Group(R.string.cat_misc);
    groups.add(misc);
    misc.add(new DatabaseCategory(R.string.cat_banks_atms, "c:banks",
        "bank", "atm"));
    misc.add(new DatabaseCategory(R.string.cat_embassies, "c:embassy",
        "embassy"));
    if (type == Type.Labels || type == Type.All) {
      misc.add(new DatabaseCategory(R.string.cat_post_boxes, "c:postbox",
          "post_box"));
      misc.add(new DatabaseCategory(R.string.cat_telephones,
          "c:telephone", "telephone"));
      misc.add(new DatabaseCategory(R.string.cat_parking, "c:parking",
          "parking"));
      misc.add(new DatabaseCategory(R.string.cat_toilets, "c:toilets",
          "toilets"));
      misc.add(new DatabaseCategory(R.string.cat_recycling,
          "c:recycling", "recycling"));
    }

    Group others = new Group(R.string.cat_none);
    groups.add(others);
    if (type == Type.Labels) {
      others.add(new MapfileCategory(R.string.cat_housenumbers,
          "c:housenumbers", TYPE_NAME_HOUSENUMBERS));
    }
    others.add(new DatabaseCategory(R.string.cat_other, PREF_KEY_OTHERS,
        "other"));
    special.add(others);
  }

  public List<Group> getGroups()
  {
    return groups;
  }

  public Set<Group> getSpecial()
  {
    return special;
  }

  public TIntSet getSpecialIndices()
  {
    return specialIndices;
  }

  public Group getGroup(Category category)
  {
    return backlinks.get(category);
  }

  public boolean isEnabled(SharedPreferences prefs, Category category)
  {
    return prefs.getBoolean(prefix + category.getPreferenceKey(), true);
  }

  public boolean isEnabled(SharedPreferences prefs, String prefKey)
  {
    return prefs.getBoolean(prefix + prefKey, true);
  }

  public void setEnabled(Editor editor, Group group, boolean status)
  {
    for (Category category : group.getChildren()) {
      setEnabled(editor, category, status);
    }
  }

  public void setEnabled(Editor editor, Category category, boolean status)
  {
    editor.putBoolean(prefix + category.getPreferenceKey(), status);
  }

  public void pickAll(Context context)
  {
    setAllTo(context, true);
  }

  public void pickNone(Context context)
  {
    setAllTo(context, false);
  }

  public void pickStreets(Context context)
  {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(context);
    Editor editor = prefs.edit();
    setAllTo(editor, false);
    setEnabled(editor, streets, true);
    editor.commit();
  }

  public void pickFood(Context context)
  {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(context);
    Editor editor = prefs.edit();
    setAllTo(editor, false);
    setEnabled(editor, foodDrink, true);
    editor.commit();
  }

  public void setAllTo(Context context, boolean status)
  {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(context);
    Editor editor = prefs.edit();
    setAllTo(editor, status);
    editor.commit();
  }

  public void setAllTo(Editor editor, boolean status)
  {
    for (Group group : getGroups()) {
      for (Category category : group.getChildren()) {
        setEnabled(editor, category, status);
      }
    }
  }

}
