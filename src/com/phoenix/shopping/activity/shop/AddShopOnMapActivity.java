package com.phoenix.shopping.activity.shop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.phoenix.shopping.R;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;
import com.phoenix.shopping.data.model.ShopAddress;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class AddShopOnMapActivity extends Activity {
  private static final int REQUEST_FIND_MOVE_ADDRESS = 13;
  final            Object  myLocationLock    = new Object();
  GoogleMap gmap;
  int    circleColor       = Color.argb(50, 13, 125, 142);
  int    circleBorderColor = Color.argb(100, 13, 125, 142);
  int    shopRadius        = 0;
  LatLng addedPoint        = null;
  Marker marker;
  Menu   menu;
  private DataProvider db;
  private volatile boolean myLocationChanged = false;
  private ShopAddress address;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_shop_on_map);
    MapFragment fragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
    db = new SQLiteDataProvider(this);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    shopRadius = Integer.valueOf(prefs.getString(getString(R.string.pref_key_notify_distance),
                                                 getString(R.string.default_notify_distance)));

    if (fragment != null) {
      gmap = fragment.getMap();
      gmap.setMyLocationEnabled(true);
      prepareListeners();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    clearMap();
    setUpShops();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.add_shop_on_map, menu);
    this.menu = menu;
    mapChanged();
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_move_camera: {
        Intent findAddress = new Intent(this, FindAddressActivity.class);
        startActivityForResult(findAddress, REQUEST_FIND_MOVE_ADDRESS);
        break;
      }
      case R.id.menu_markers_clear: {
        cleanMarker();
        mapChanged();
        break;
      }
      case R.id.menu_add_shop: {
        submitData();
        break;
      }
      default:
        return true;
    }
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data != null && resultCode == RESULT_OK && data.getExtras() != null) {
      switch (requestCode) {
        case REQUEST_FIND_MOVE_ADDRESS: {
          Location loc = (Location) data.getExtras().get(FindAddressActivity.DATA_KEY);
          if (loc != null) {
            LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

            gmap.animateCamera(CameraUpdateFactory.newLatLng(pos));
          }
          break;
        }
        case ShopDescriptionSetupActivity.REQUEST_GET_DESCRIPTION: {
          Intent result = new Intent();
          result.putExtra(FindAddressActivity.DATA_KEY, data.getSerializableExtra("address"));
          //db.addAddress(checked);
          setResult(RESULT_OK, result);
          finish();
          break;
        }
      }
    }
  }

  private void prepareListeners() {
    gmap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
      @Override
      public void onMyLocationChange(Location location) {
        synchronized (myLocationLock) {
          if (!myLocationChanged) {
            final LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
            myLocationChanged = true;
          }
        }
      }
    });

    gmap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
      @Override
      public void onMapLongClick(LatLng latLng) {
        if (marker != null) {
          cleanMarker();
        }
        addedPoint = latLng;
        drawPoint(latLng);
        mapChanged();
      }

      private void drawPoint(LatLng latLng) {
        marker = gmap.addMarker(new MarkerOptions().position(latLng).draggable(true));

      }
    });
  }

  private void submitData() {
    ShopAddress address = new ShopAddress();
    address.setLongitude(addedPoint.longitude);
    address.setLatitude(addedPoint.latitude);
    Intent intent = new Intent(this, ShopDescriptionSetupActivity.class);
    intent.putExtra("address", address);
    startActivityForResult(intent, ShopDescriptionSetupActivity.REQUEST_GET_DESCRIPTION);
  }

  private void setUpShops() {
    for (ShopAddress shopAddress : db.getShopList()) {
      final LatLng position = new LatLng(shopAddress.getLatitude(), shopAddress.getLongitude());
      gmap.addCircle(createCircleOpts(position));
    }
  }

  private void clearMap() {
    gmap.clear();
  }

  private CircleOptions createCircleOpts(LatLng position) {
    return new CircleOptions().center(position)
                              .fillColor(circleColor)
                              .strokeWidth(1)
                              .strokeColor(circleBorderColor)
                              .radius(shopRadius)
                              .visible(true);
  }

  private void mapChanged() {
    boolean hasMarker = marker != null;
    if (menu != null) {
      MenuItem deleteItem = menu.findItem(R.id.menu_markers_clear);
      MenuItem submitItem = menu.findItem(R.id.menu_add_shop);
      if (deleteItem != null) {
        deleteItem.setVisible(hasMarker);
      }
      if (submitItem != null) {
        submitItem.setVisible(hasMarker);
      }
    }
    invalidateOptionsMenu();
  }

  private void cleanMarker() {
    if (marker != null) {
      marker.remove();
      marker = null;
      addedPoint = null;
    }
  }
}
