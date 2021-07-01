package de.topobyte.apps.viewer.label;

import de.topobyte.android.maps.utils.label.Label;

public class Poi
{

  private final String type;
  private final Label label;

  public Poi(String type, Label label)
  {
    this.type = type;
    this.label = label;
  }

  public String getType()
  {
    return type;
  }

  public Label getLabel()
  {
    return label;
  }

}
