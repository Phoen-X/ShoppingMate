package com.phoenix.shopping.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.shopping.R;
import com.phoenix.shopping.activity.ShoppingListActivity;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;
import com.phoenix.shopping.data.model.Purchase;
import com.phoenix.shopping.data.model.ShopAddress;

import java.util.List;

import static android.location.LocationManager.NETWORK_PROVIDER;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class LocationListenerService extends Service implements LocationListener {
  private String CHECK_TIMEOUT;
  private static final int MIN_DISTANCE = 0;
  private String FORGET_VISIT_TIME;
  private static final String LOGGING_TAG = LocationListenerService.class.getSimpleName();
  private String DISTANCE_TO_REACT;
  private static final int NOTIFICATION_ID = 1;
  private static final long MSEC_IN_MIN = 1000 * 60;
  public static boolean IS_ACTIVE = false;
  public static final Object SERVICE_LOCK = new Object();

  private LocationManager locationService;
  private DataProvider db;
  private long lastShownList;
  private Bitmap notificationIcon;
  private SharedPreferences prefs;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {
    synchronized (SERVICE_LOCK) {
      Log.i(LOGGING_TAG, "Starting location service");
      db = new SQLiteDataProvider(this);
      locationService = (LocationManager) getSystemService(LOCATION_SERVICE);
      notificationIcon = BitmapFactory.decodeResource(getResources(),
                                                      R.drawable.shopping_list);

      prefs = PreferenceManager.getDefaultSharedPreferences(this);
      setDefaultSettings();

      prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
          registerUpdatesListener();
        }
      });
      registerUpdatesListener();
      Log.d(LOGGING_TAG, "Started successfully");
      IS_ACTIVE = true;
    }
    return START_STICKY;
  }

  private void setDefaultSettings() {
    CHECK_TIMEOUT = getString(R.string.default_check_frequency);
    FORGET_VISIT_TIME = getString(R.string.default_forget_time);
    DISTANCE_TO_REACT = getString(R.string.default_notify_distance);
  }

  private void registerUpdatesListener() {
    unregisterUpdatesListener();
    //lastShownList = System.currentTimeMillis();
    long timeout = Long.valueOf(prefs.getString("pref_check_frequency", CHECK_TIMEOUT)) * MSEC_IN_MIN;
    Log.v(LOGGING_TAG, "Check_frequency is: " + timeout);
    locationService.requestLocationUpdates(NETWORK_PROVIDER, timeout, MIN_DISTANCE, this);
  }

  @Override
  public void onLocationChanged(final Location location) {
    Log.d(LOGGING_TAG, "Location changed: " + location);

    if (checkStoresLocation(db.getShopList(), location)) {
      Log.i(LOGGING_TAG, "Notifying user for shop location");

      lastShownList = System.currentTimeMillis();
      notifyUser();
    }
  }

  private void notifyUser() {
    Intent intent = new Intent(this, ShoppingListActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    Log.v(LOGGING_TAG, "Pending intent is: " + pending);
    final String title = getString(R.string.app_name);
    final String text = getString(R.string.notification_text);

    Log.v(LOGGING_TAG, String.format("title: %s, text: %s", title, text));
    NotificationCompat.Builder notificationBldr = new NotificationCompat.Builder(this).setContentTitle(title)
                                                                                      .setContentText(text)
                                                                                      .setContentIntent(pending)
                                                                                      .setAutoCancel(true)
                                                                                      .setLargeIcon(notificationIcon)
                                                                                      .setSmallIcon(R.drawable.shopping_list)
                                                                                      .setVibrate(new long[]{0, 1000});

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(NOTIFICATION_ID, notificationBldr.build());
  }

  private boolean checkStoresLocation(List<ShopAddress> shops, Location location) {
    if (serviceHasForgotShowing() && shoppingListIsNotEmpty()) {
      Log.v(LOGGING_TAG, "Checking shops to go");
      for (ShopAddress shop : shops) {
        Log.v(LOGGING_TAG, "Checking shop: " + shop.toString());
        Location shopLoc = new Location(location.getProvider());
        shopLoc.setLatitude(shop.getLatitude());
        shopLoc.setLongitude(shop.getLongitude());
        float distanceToShop = getDistanceToShop(location, shopLoc);
        Log.i(LOGGING_TAG, "Distance is " + distanceToShop + " meters");
        if (isStoreInRange(distanceToShop)) {
          return true;
        }
      }
    }
    return false;
  }

  private float getDistanceToShop(final Location location, final Location shopLoc) {
    float[] results = new float[3];
    Location.distanceBetween(shopLoc.getLatitude(), shopLoc.getLongitude(),
                             location.getLatitude(), location.getLongitude(),
                             results);
    return results[0];
  }

  private boolean isStoreInRange(final float distance) {
    int range = Integer.valueOf(prefs.getString("pref_notify_distance", DISTANCE_TO_REACT));
    return distance <= range;
  }

  private boolean shoppingListIsNotEmpty() {
    final List<Purchase> purchaseList = db.getPurchaseList();

    for (Purchase purchase : purchaseList) {
      if (!purchase.isSold()) {
        return true;
      }
    }
    return false;
  }

  private boolean serviceHasForgotShowing() {
    final long elapsed = System.currentTimeMillis() - lastShownList;
    long forgetTime = Long.valueOf(prefs.getString("pref_forget_time", FORGET_VISIT_TIME)) * MSEC_IN_MIN;
    Log.i(LOGGING_TAG, elapsed + " msec. elapsed from last showing. Needed: " + forgetTime);
    return (elapsed > forgetTime);
  }

  @Override
  public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    Log.d(LOGGING_TAG, "Status changed: " + status);
  }

  @Override
  public void onProviderEnabled(final String provider) {
    Log.d(LOGGING_TAG, "Provider enabled: " + provider);
  }

  @Override
  public void onProviderDisabled(final String provider) {
    Log.d(LOGGING_TAG, "Provider disabled: " + provider);
  }

  @Override
  public void onDestroy() {
    synchronized (SERVICE_LOCK) {
      unregisterUpdatesListener();
      IS_ACTIVE = false;
    }
    super.onDestroy();
  }

  private void unregisterUpdatesListener() {
    locationService.removeUpdates(this);
  }
}
