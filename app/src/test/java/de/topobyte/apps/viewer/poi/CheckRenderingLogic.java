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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import de.topobyte.adt.multicollections.HashMultiSet;
import de.topobyte.adt.multicollections.MultiSet;
import de.topobyte.apps.maps.atestcity.BuildConfig;
import de.topobyte.apps.viewer.FileUtil;
import de.topobyte.apps.viewer.ResourceConstants;
import de.topobyte.apps.viewer.poi.category.Category;
import de.topobyte.apps.viewer.poi.category.DatabaseCategory;
import de.topobyte.apps.viewer.poi.category.MapfileCategory;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.jdbc.JdbcConnection;
import de.topobyte.mapocado.styles.directory.StyleDirectory;
import de.topobyte.mapocado.styles.labels.elements.IconLabel;
import de.topobyte.mapocado.styles.labels.elements.LabelContainer;
import de.topobyte.mapocado.styles.labels.elements.LabelType;
import de.topobyte.mapocado.styles.labels.elements.Rule;
import de.topobyte.nomioc.luqe.model.SqPoiType;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CheckRenderingLogic
{
  private static final boolean INITIAL = false;

  @Test
  public void test() throws SQLException
  {
    System.out.println("Checking integrity of rendering logic");

    StyleDirectory sd = getStyleDirectory();
    List<Rule> rules = sd.getLabelRules();
    Set<String> styleTypes = new HashSet<>();
    for (Rule rule : rules) {
      System.out.println(rule.getKey());
      styleTypes.add(rule.getKey());
    }
    Map<Rule, LabelContainer> styles = sd.getLabelStyles();
    for (Entry<Rule, LabelContainer> e : styles.entrySet()) {
      if (e.getValue().getType() == LabelType.ICON) {
        IconLabel il = (IconLabel) e.getValue().getLabel();
        String image = il.getImage();
        if (image == null) {
          System.out.println("UNDEFINED IMAGE: "
              + e.getKey().getKey());
        }
      }
    }

    IConnection db = openConnection();

    Set<String> dbTypes = new HashSet<>();

    PoiTypeInfo typeInfo = PoiTypeInfo.getInstance(db);
    for (SqPoiType type : typeInfo.getTypes()) {
      if (INITIAL) {
        System.out.println("DB: " + type.getId() + ": "
            + type.getName());
      }
      dbTypes.add(type.getName());
    }

    Set<String> orderTypes = new HashSet<>();
    for (String r : DrawingOrder.order) {
      if (INITIAL) {
        System.out.println("Order: " + r);
      }
      orderTypes.add(r);
    }

    Set<String> catTypes = new HashSet<>();
    MultiSet<String> countedCats = new HashMultiSet<>();
    Categories categories = Categories.getSearchInstance();
    for (Group group : categories.getGroups()) {
      List<Category> children = group.getChildren();
      for (Category c : children) {
        // System.out.println(c.getPreferenceKey());
        if (c instanceof DatabaseCategory) {
          DatabaseCategory dc = (DatabaseCategory) c;
          List<String> identifiers = dc.getIdentifiers();
          catTypes.addAll(identifiers);
          countedCats.addAll(identifiers);
        } else if (c instanceof MapfileCategory) {
          MapfileCategory mc = (MapfileCategory) c;
        }
      }
    }

    Set<String> listDrawableTypes = ListDrawables.drawables.keySet();

    print(without(dbTypes, orderTypes), "## DB minus order:");
    print(without(orderTypes, dbTypes), "## Order minus DB:");

    print(without(dbTypes, styleTypes), "## DB minus style:");
    print(without(styleTypes, dbTypes), "## Style minus DB:");

    print(without(catTypes, orderTypes), "## Categories minus order:");
    print(without(orderTypes, catTypes), "## Order minus categories:");

    print(without(catTypes, dbTypes), "## Categories minus DB:");
    print(without(dbTypes, catTypes), "## DB minus categories:");

    System.out.println("DB types: " + sorted(dbTypes));
    System.out.println("Category types: " + sorted(catTypes));

    print(without(dbTypes, listDrawableTypes),
        "## DB minus list drawables:");

    print(without(listDrawableTypes, dbTypes),
        "## list drawables minus DB:");

    System.out.println("## Multiply defined types in categories");
    boolean none = true;
    for (String a : countedCats.keySet()) {
      int occs = countedCats.occurences(a);
      if (occs > 1) {
        System.out.println(occs + ": " + a);
        none = false;
      }
    }
    if (none) {
      System.out.println("None");
    }
  }

  private static List<String> sorted(Set<String> elements)
  {
    List<String> list = new ArrayList<>(elements);
    Collections.sort(list);
    return list;
  }

  private static void print(Set<String> elements, String string)
  {
    System.out.println(string);
    if (elements.isEmpty()) {
      System.out.println("no elements");
    } else {
      List<String> list = new ArrayList<>(elements);
      Collections.sort(list);
      for (String element : list) {
        System.out.println(element);
      }
    }
  }

  private static Set<String> without(Set<String> input, Set<String> remove)
  {
    HashSet<String> copy = new HashSet<>(input);
    copy.removeAll(remove);
    return copy;
  }

  private static StyleDirectory getStyleDirectory()
  {
    String stylePath = "style-default.zip";
    InputStream styleInput = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(stylePath);

    System.out.println("Asset database path: " + stylePath);
    File dir = null;
    try {
      File file = File.createTempFile("styles", ".zip");
      dir = new File(file.getPath() + ".d");
      System.out.println(dir);
      dir.mkdirs();
      dir.deleteOnExit();
      file.delete();
    } catch (IOException e) {
      System.out.println("Unable to create dir file: " + e.getMessage());
      System.exit(1);
    }

    try {
      FileUtil.unzipTo(dir, styleInput);
    } catch (IOException e) {
      System.out.println("Unable to extract style: " + e.getMessage());
      System.exit(1);
    }

    StyleDirectory styleDirectory = new StyleDirectory(dir);
    try {
      styleDirectory.init();
    } catch (Exception e) {
      System.out
          .println("Unable to initialize styles: " + e.getMessage());
      System.exit(1);
    }

    try {
      delete(dir);
    } catch (IOException e) {
      System.out.println("Unable to delete extracted style: "
          + e.getMessage());
    }

    return styleDirectory;
  }

  private static IConnection openConnection() throws SQLException
  {
    String dbPath = ResourceConstants.ASS_DATABASE_FILE;
    InputStream dbInput = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(dbPath);

    System.out.println("Asset database path: " + dbPath);
    File dbFile = null;
    try {
      dbFile = File.createTempFile("map.db", ".sqlite");
    } catch (IOException e) {
      System.out.println("Unable to create temp file: " + e.getMessage());
      System.exit(1);
    }
    dbFile.deleteOnExit();

    if (dbInput == null) {
      System.out.println("Unable to open asset database");
      System.exit(1);
    }
    System.out.println("Temporary database location: " + dbFile);
    try {
      copy(dbInput, dbFile);
    } catch (IOException e) {
      System.out.println("Unable to copy database: " + e.getMessage());
      System.exit(1);
    }

    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.out.println("sqlite driver not found: " + e.getMessage());
      System.exit(1);
    }
    Connection connection = DriverManager.getConnection("jdbc:sqlite:"
        + dbFile.getPath());
    IConnection db = new JdbcConnection(connection);
    return db;
  }

  private static void copy(InputStream dbInput, File dbFile)
      throws IOException
  {
    FileOutputStream fos = new FileOutputStream(dbFile);
    OutputStream os = new BufferedOutputStream(fos);
    InputStream zis = new GZIPInputStream(dbInput);
    byte[] buffer = new byte[4096];
    while (true) {
      int r = zis.read(buffer);
      if (r <= 0) {
        break;
      }
      os.write(buffer, 0, r);
    }
    os.close();
    zis.close();
  }

  private static void delete(File f) throws IOException
  {
    if (f.isDirectory()) {
      for (File c : f.listFiles()) {
        delete(c);
      }
    }
    if (!f.delete()) {
      throw new FileNotFoundException("Failed to delete file: " + f);
    }
  }
}
