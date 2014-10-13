package com.phoenix.shopping.data.model;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class ShopAddress implements Serializable{
  private int id;
  private double latitude;
  private double longitude;
  private String description;


    public ShopAddress() {
    }

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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
    return String.format("%s (%s,%s)", description, longitude, latitude);
  }
}
