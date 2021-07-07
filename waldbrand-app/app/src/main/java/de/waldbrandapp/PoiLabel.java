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
