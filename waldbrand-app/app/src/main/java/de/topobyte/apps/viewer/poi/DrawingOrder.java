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

import java.util.ArrayList;
import java.util.List;

public class DrawingOrder
{

  public static List<String> order = new ArrayList<>();

  static {
    order.add("city");
    order.add("town");
    order.add("village");
    order.add("hamlet");
    order.add("island");
    order.add("islet");
    order.add("borough");
    order.add("suburb");
    order.add("quarter");
    order.add("neighborhood");

    order.add("airport");
    order.add("helipad");

    order.add("railwaystation");
    order.add("railwayhalt");
    order.add("tramstop");
    order.add("busstation");
    order.add("busstop");

    order.add("peak");
    order.add("volcano");

    order.add("restaurant");
    order.add("fastfood");
    order.add("cafe");
    order.add("pub");
    order.add("biergarten");
    order.add("bar");
    order.add("nightclub");

    order.add("museum");
    order.add("gallery");
    order.add("theatre");
    order.add("cinema");

    order.add("hotel");
    order.add("hostel");
    order.add("guesthouse");
    order.add("campsite");

    order.add("hospital");

    order.add("university");
    order.add("school");
    order.add("kindergarten");
    order.add("townhall");
    order.add("library");
    order.add("embassy");
    order.add("prison");

    order.add("information");
    order.add("memorial");
    order.add("attraction");

    order.add("christian");
    order.add("jewish");
    order.add("muslim");

    order.add("bank");
    order.add("atm");

    order.add("supermarket");
    order.add("bakery");
    order.add("convenience");
    order.add("clothes");
    order.add("shoes");
    order.add("jewelry");
    order.add("furniture");
    order.add("optician");
    order.add("bookshop");
    order.add("beverages");
    order.add("carwash");
    order.add("fuelstation");
    order.add("travel_agency");
    order.add("deli");
    order.add("boutique");
    order.add("casino");
    order.add("electronics");
    order.add("doityourself");
    order.add("bikeshop");
    order.add("bikerental");
    order.add("florist");
    order.add("chemist");
    order.add("hairdresser");
    order.add("other-shops");
    order.add("carshop");

    order.add("beauty");
    order.add("massage");
    order.add("brothel");
    order.add("stripclub");

    order.add("pharmacy");
    order.add("dentist");
    order.add("doctor");
    order.add("veterinary");

    order.add("water");
    order.add("park");
    order.add("cemetery");

    order.add("postoffice");
    order.add("firestation");
    order.add("playground");
    order.add("shelter");
    order.add("viewpoint");
    order.add("toilets");
    order.add("post_box");
    order.add("recycling");
    order.add("telephone");
    order.add("parking");

    order.add("other");
    order.add("housenumbers");

    order.add("hydrant-underground");
    order.add("hydrant-pillar");
    order.add("fire-water-pond");
    order.add("fire-water-pond2");
  }

}
