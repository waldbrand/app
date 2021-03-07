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

import com.slimjars.dist.gnu.trove.map.TIntIntMap;
import com.slimjars.dist.gnu.trove.map.TObjectIntMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntIntHashMap;
import com.slimjars.dist.gnu.trove.map.hash.TObjectIntHashMap;

import java.util.List;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.model.SqPoiType;

public class ListDrawables
{

  public static TObjectIntMap<String> drawables = new TObjectIntHashMap<>();

  static {
    drawables.put("city", R.drawable.cat_place);
    drawables.put("town", R.drawable.cat_place);
    drawables.put("village", R.drawable.cat_place);
    drawables.put("hamlet", R.drawable.cat_place);
    drawables.put("borough", R.drawable.cat_place);
    drawables.put("suburb", R.drawable.cat_place);
    drawables.put("quarter", R.drawable.cat_place);
    drawables.put("neighborhood", R.drawable.cat_place);
    drawables.put("attraction", R.drawable.cat_attraction);
    drawables.put("museum", R.drawable.cat_museum);
    drawables.put("gallery", R.drawable.cat_museum);
    drawables.put("theatre", R.drawable.cat_theatre);
    drawables.put("cinema", R.drawable.cat_cinema);
    drawables.put("worship", R.drawable.cat_worship);
    drawables.put("hotel", R.drawable.cat_hotel);
    drawables.put("hostel", R.drawable.cat_hostel);
    drawables.put("guesthouse", R.drawable.cat_guesthouse);
    drawables.put("restaurant", R.drawable.cat_restaurant);
    drawables.put("fastfood", R.drawable.cat_fastfood);
    drawables.put("cafe", R.drawable.cat_cafe);
    drawables.put("pub", R.drawable.cat_pub);
    drawables.put("bar", R.drawable.cat_bar);
    drawables.put("biergarten", R.drawable.cat_biergarten);
    drawables.put("nightclub", R.drawable.cat_nightclub);
    drawables.put("clothes", R.drawable.cat_clothes);
    drawables.put("boutique", R.drawable.cat_clothes);
    drawables.put("shoes", R.drawable.cat_shoes);
    drawables.put("jewelry", R.drawable.cat_jewelry);
    drawables.put("supermarket", R.drawable.cat_supermarket);
    drawables.put("chemist", R.drawable.cat_supermarket);
    drawables.put("hospital", R.drawable.cat_hospital);
    drawables.put("railwaystation", R.drawable.cat_railstation);
    drawables.put("railwayhalt", R.drawable.cat_railstation);
    drawables.put("tramstop", R.drawable.cat_tramstop);
    drawables.put("busstop", R.drawable.cat_busstop);
    drawables.put("other", R.drawable.cat_misc);

    drawables.put("atm", R.drawable.cat_atm);
    drawables.put("bakery", R.drawable.cat_bakery);
    drawables.put("bank", R.drawable.cat_bank);
    drawables.put("bikeshop", R.drawable.cat_bicycle);
    drawables.put("bookshop", R.drawable.cat_book);
    drawables.put("carshop", R.drawable.cat_car);
    drawables.put("casino", R.drawable.cat_casino);
    drawables.put("convenience", R.drawable.cat_kiosk);
    drawables.put("dentist", R.drawable.cat_dentist);
    drawables.put("doctor", R.drawable.cat_doctors);
    drawables.put("doityourself", R.drawable.cat_diy);
    drawables.put("electronics", R.drawable.cat_shop_other);
    drawables.put("deli", R.drawable.cat_shop_other);
    drawables.put("embassy", R.drawable.cat_embassy);
    drawables.put("florist", R.drawable.cat_florist);
    drawables.put("fuelstation", R.drawable.cat_fuel);
    drawables.put("furniture", R.drawable.cat_shop_other);
    drawables.put("hairdresser", R.drawable.cat_hairdresser);
    drawables.put("kindergarten", R.drawable.cat_kindergarten);
    drawables.put("library", R.drawable.cat_library);
    drawables.put("memorial", R.drawable.cat_memorial);
    drawables.put("optician", R.drawable.cat_opticians);
    drawables.put("other-shops", R.drawable.cat_shop_other);
    drawables.put("park", R.drawable.cat_park);
    drawables.put("pharmacy", R.drawable.cat_pharmacy);
    drawables.put("playground", R.drawable.cat_playground);
    drawables.put("prison", R.drawable.cat_prison);
    drawables.put("school", R.drawable.cat_school);
    drawables.put("townhall", R.drawable.cat_townhall);
    drawables.put("university", R.drawable.cat_university);
    drawables.put("veterinary", R.drawable.cat_veterinary);
    drawables.put("water", R.drawable.cat_scrub);

    drawables.put("christian", R.drawable.cat_christian);
    drawables.put("muslim", R.drawable.cat_islamic);
    drawables.put("jewish", R.drawable.cat_jewish);

    drawables.put("airport", R.drawable.cat_airport);
    drawables.put("helipad", R.drawable.cat_helicopter);
    drawables.put("bikerental", R.drawable.cat_bikerental);
    drawables.put("busstation", R.drawable.cat_busstation);
    drawables.put("campsite", R.drawable.cat_camping);
    drawables.put("cemetery", R.drawable.cat_cemetery);
    drawables.put("firestation", R.drawable.cat_firestation);
    drawables.put("information", R.drawable.cat_information);
    drawables.put("island", R.drawable.cat_place);
    drawables.put("islet", R.drawable.cat_place);
    drawables.put("peak", R.drawable.cat_peak);
    drawables.put("volcano", R.drawable.cat_volcano);
    drawables.put("postoffice", R.drawable.cat_post_office);
    drawables.put("shelter", R.drawable.cat_shelter);
    drawables.put("viewpoint", R.drawable.cat_view_point);
  }

  private static TIntIntMap idMap = null;

  public static void initialize(IConnection db) throws QueryException
  {
    if (idMap != null) {
      return;
    }
    idMap = new TIntIntHashMap();
    List<SqPoiType> types = Dao.getTypes(db);
    for (SqPoiType type : types) {
      if (drawables.containsKey(type.getName())) {
        int id = drawables.get(type.getName());
        idMap.put(type.getId(), id);
      } else {
        idMap.put(type.getId(), R.drawable.cat_misc);
      }
    }
  }

  public static int getDrawable(int category)
  {
    return idMap.get(category);
  }

}
