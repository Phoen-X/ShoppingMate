package com.phoenix.shopping.data;

import android.location.Address;
import com.phoenix.shopping.data.model.Purchase;
import com.phoenix.shopping.data.model.ShopAddress;

import java.util.List;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public interface DataProvider {

  List<String> getPurchaseTypes();

  List<Purchase> getPurchaseList(PurchaseFilter filter);

  List<Purchase> getPurchaseList();

  Purchase getSingle(int id);

  void addPurchase(Purchase purchase);

  void removePurchase(Purchase itemToDelete);

  void addPurchaseType(String type);

  void removePurchaseType(String type);

  void addAddress(Address address);

  void addAddress(double latitude, double longitude, String description);

  List<ShopAddress> getShopList();

  void removeAddress(ShopAddress itemToDelete);

  void sell(int id, boolean sold);
}
