package de.topobyte.apps.viewer;

public enum CoordinateSystem
{

  WGS84("wgs84"),
  UTM("utm");

  private String identifier;

  CoordinateSystem(String identifier)
  {
    this.identifier = identifier;
  }

}
