package com.phoenix.shopping.data.model;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class ShopAddress {
  private int id;
  private double latitude;
  private double longitude;
  private String description;

  public ShopAddress(final int id, final double latitude, final double longitude, final String description) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.description = description;
  }

  public int getId() {
    return id;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getDescription() {
    return description;
  }

  public String toString() {
    return String.format("%s (%s,%s)", description, longitude, latitude);
  }
}
