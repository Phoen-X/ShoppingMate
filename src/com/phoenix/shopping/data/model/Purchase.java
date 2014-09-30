package com.phoenix.shopping.data.model;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class Purchase {
  private int id;
  private String name;
  private String type;
  private String quantity;
  private boolean sold;

  public Purchase() {
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(final String quantity) {
    this.quantity = quantity;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public boolean isSold() {
    return sold;
  }

  public void setSold(final boolean sold) {
    this.sold = sold;
  }

  @Override
  public String toString() {
    return String.format("%s : %s %s", name, quantity, type);
  }
}
