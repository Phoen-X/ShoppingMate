package com.phoenix.shopping.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import com.phoenix.shopping.data.model.Purchase;
import com.phoenix.shopping.data.model.ShopAddress;
import com.phoenix.shopping.util.AddressUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class SQLiteDataProvider extends SQLiteOpenHelper implements DataProvider {
  private static final String DB_NAME = "com.phoenix.shopping.data.BUYLIST_DB";
  private static final int DB_VERSION = 2;
  public static final String PURCHASE_LIST_TABLE = "PURCHASE_LIST";
  public static final String PURCHASE_TYPES_TABLE = "PURCHASE_TYPE";
  public static final String SHOP_ADDRESSES_TABLE = "SHOP_ADD";

  private static final String CREATE_PURCHASE_TABLE_SQL = "CREATE TABLE " + PURCHASE_LIST_TABLE + " " +
                                                          "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                          "type VARCHAR(10), " +
                                                          "name VARCHAR(50) NOT NULL, " +
                                                          "sold VARCHAR(1), " +
                                                          "quantity VARCHAR(10) );";

  private static final String CREATE_PURCHASE_TYPES_TABLE_SQL = "CREATE TABLE " + PURCHASE_TYPES_TABLE + " " +
                                                                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                "name VARCHAR(10) NOT NULL);";

  private static final String CREATE_SHOP_ADDRESSES_TABLE_SQL = "CREATE TABLE " + SHOP_ADDRESSES_TABLE+ " " +
                                                                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                "latitude DOUBLE NOT NULL, " +
                                                                "longitude DOUBLE NOT NULL," +
                                                                "description VARCHAR(100) NOT NULL DEFAULT '[NO_DESCRIPTION]');";

  private static final String INSERT_PURCHASE_TYPE_SQL = "INSERT INTO PURCHASE_TYPE (name) VALUES ('%s');";
  private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s;" ;
  private static final String DROP_PURCHASE_TABLE_SQL = String.format(DROP_TABLE_SQL, PURCHASE_LIST_TABLE);
  private static final String DROP_PURCHASE_TYPES_SQL = String.format(DROP_TABLE_SQL, PURCHASE_TYPES_TABLE);
  private static final String DROP_SHOP_ADDRESSES_SQL = String.format(DROP_TABLE_SQL, SHOP_ADDRESSES_TABLE);

  List<String> purchaseTypes = Arrays.asList("шт");

  public SQLiteDataProvider(final Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public List<String> getPurchaseTypes() {
    List<String> list = new ArrayList<>();
    Cursor cursor = getReadableDatabase().rawQuery("SELECT _id, name FROM PURCHASE_TYPE", null);
    int nameIndex = cursor.getColumnIndex("name");
    while (cursor.moveToNext()) {
      list.add(cursor.getString(nameIndex));
    }

    return list;
  }

  @Override
  public List<Purchase> getPurchaseList(final PurchaseFilter filter) {
    List<Purchase> list = new ArrayList<>();
    for (Purchase purchase : getPurchaseList()) {
      if (filter.apply(purchase)) {
        list.add(purchase);
      }
    }
    return list;
  }

  @Override
  public List<Purchase> getPurchaseList() {
    List<Purchase> list = new ArrayList<>();
    Cursor cursor = getReadableDatabase().rawQuery("SELECT _id, " +
                                                   "       name, " +
                                                   "       type, " +
                                                   "       quantity, " +
                                                   "       sold " +
                                                   "FROM PURCHASE_LIST l ", null);
    int idIndex = cursor.getColumnIndex("_id");
    int nameIndex = cursor.getColumnIndex("name");
    int quantityIndex = cursor.getColumnIndex("quantity");
    int typeNameIndex = cursor.getColumnIndex("type");
    int soldIndex = cursor.getColumnIndex("sold");

    while (cursor.moveToNext()) {
      Purchase purchase = new Purchase();
      purchase.setId(cursor.getInt(idIndex));
      purchase.setName(cursor.getString(nameIndex));
      purchase.setQuantity(cursor.getString(quantityIndex));
      purchase.setType(cursor.getString(typeNameIndex));
      purchase.setSold(cursor.getString(soldIndex) != null);
      list.add(purchase);
    }
    return list;
  }

  @Override
  public Purchase getSingle(final int id) {
    return null;
  }

  @Override
  public void addPurchase(final Purchase purchase) {
    ContentValues row = new ContentValues();
    row.put("name", purchase.getName());
    row.put("quantity", purchase.getQuantity());
    row.put("type", purchase.getType());
    getWritableDatabase().insert("PURCHASE_LIST", "name", row);
  }

  @Override
  public void removePurchase(final Purchase itemToDelete) {
    getWritableDatabase().delete(PURCHASE_LIST_TABLE, "_id = ?", new String[]{String.valueOf(itemToDelete.getId())});
  }

  @Override
  public void onCreate(final SQLiteDatabase db) {
    db.execSQL(DROP_PURCHASE_TABLE_SQL);
    db.execSQL(DROP_PURCHASE_TYPES_SQL);
    db.execSQL(DROP_SHOP_ADDRESSES_SQL);

    db.execSQL(CREATE_PURCHASE_TABLE_SQL);
    db.execSQL(CREATE_PURCHASE_TYPES_TABLE_SQL);
    db.execSQL(CREATE_SHOP_ADDRESSES_TABLE_SQL);
    for (String purchaseType : purchaseTypes) {
      db.execSQL(String.format(INSERT_PURCHASE_TYPE_SQL, purchaseType));
    }
  }

  @Override
  public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    onCreate(db);
  }

  @Override
  public void addPurchaseType(String type) {
    ContentValues row = new ContentValues();
    row.put("name", type);
    getWritableDatabase().insert(PURCHASE_TYPES_TABLE, "name", row);
  }

  @Override
  public void removePurchaseType(String type) {
    getWritableDatabase().delete(PURCHASE_TYPES_TABLE, "name = ?", new String[]{type});
  }

  @Override
  public void addAddress(final Address address) {
    addAddress(address.getLatitude(), address.getLongitude(), AddressUtils.buildAddress(address));
  }

  @Override
  public void addAddress(final double latitude, final double longitude, final String description) {
    ContentValues row = new ContentValues();
    row.put("longitude", longitude);
    row.put("latitude", latitude);
    row.put("description", description);
    getWritableDatabase().insert(SHOP_ADDRESSES_TABLE, null, row);
  }

  @Override
  public List<ShopAddress> getShopList() {
    List<ShopAddress> list = new ArrayList<>();
    Cursor cursor = getReadableDatabase().rawQuery("SELECT _id, " +
                                                   "       latitude, " +
                                                   "       longitude, " +
                                                   "       description " +
                                                   "FROM " + SHOP_ADDRESSES_TABLE, null);
    int idIndex = cursor.getColumnIndex("_id");
    int latIndex = cursor.getColumnIndex("latitude");
    int longIndex = cursor.getColumnIndex("longitude");
    int descrIndex = cursor.getColumnIndex("description");
    while (cursor.moveToNext()) {
      ShopAddress address = new ShopAddress(cursor.getInt(idIndex),
                                            cursor.getDouble(latIndex),
                                            cursor.getDouble(longIndex),
                                            cursor.getString(descrIndex));
      list.add(address);
    }
    return list;
  }

  @Override
  public void removeAddress(final ShopAddress itemToDelete) {
    getWritableDatabase().delete(SHOP_ADDRESSES_TABLE, "_id = ? ", new String[] {String.valueOf(itemToDelete.getId())});
  }

  @Override
  public void sell(final int id, final boolean sold) {
    ContentValues row = new ContentValues();
    row.put("sold", sold ? "X" : null);
    getWritableDatabase().update(PURCHASE_LIST_TABLE, row, "_id = " + id, null);
  }
}
