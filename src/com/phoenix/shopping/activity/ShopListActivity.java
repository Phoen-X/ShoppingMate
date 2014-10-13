package com.phoenix.shopping.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import com.phoenix.shopping.R;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;
import com.phoenix.shopping.data.model.ShopAddress;
import com.phoenix.shopping.listeners.SwipeDismissListener;

import java.io.IOException;
import java.util.List;

/**
 * Class description here.
 *
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class ShopListActivity extends Activity {
    public static final int ADD_SHOP_REQUEST = 1;

    private ListView shopList;
    private DataProvider db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_list);

        this.shopList = (ListView) findViewById(R.id.addrList);
        this.db = new SQLiteDataProvider(this);

        fillShopList();
    }

    private void fillShopList() {
        final ArrayAdapter<ShopAddress> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, db.getShopList());
        shopList.setAdapter(adapter);
        final SwipeDismissListener touchListener = new SwipeDismissListener(shopList,
                getDismissCallback(adapter));
        shopList.setOnTouchListener(touchListener);
        shopList.setOnScrollListener(touchListener.makeScrollListener());
        shopList.setEmptyView(findViewById(R.id.emptyListView));
    }

    private SwipeDismissListener.DismissCallbacks getDismissCallback(final ArrayAdapter<ShopAddress> adapter) {
        return new SwipeDismissListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(final int position) {
                return true;
            }

            @Override
            public void onDismiss(final ListView listView,
                                  final int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    ShopAddress itemToDelete = adapter.getItem(position);
                    db.removeAddress(itemToDelete);
                    adapter.remove(itemToDelete);
                }
                adapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_shop: {
                showAddShopMenu(findViewById(item.getItemId()), this);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAddShopMenu(final View itemView, final Context context) {
        PopupMenu popupMenu = new PopupMenu(context, itemView);
        popupMenu.inflate(R.menu.add_shop);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_add_shop_coords: {
                        Intent addShopIntent = new Intent(context, AddShopByCoordsActivity.class);
                        startActivityForResult(addShopIntent, ADD_SHOP_REQUEST);
                        break;
                    }
                    case R.id.menu_add_shop_addr: {
                        Intent intent = new Intent(context, FindAddressActivity.class);
                        startActivityForResult(intent, ADD_SHOP_REQUEST);
                        break;
                    }
                    case R.id.menu_add_on_map: {
                        Intent intent = new Intent(context, AddShopOnMapActivity.class);
                        startActivityForResult(intent, ADD_SHOP_REQUEST);
                        break;
                    }
                }
                return true;
            }
        });

        popupMenu.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (data != null && resultCode == RESULT_OK && data.getExtras() != null) {
            final Object extra = data.getExtras().get(FindAddressActivity.DATA_KEY);
            if (extra != null && extra instanceof Location) {
                Location loc = (Location) extra;
                String description = "Unknown name";

                Geocoder geocoder = new Geocoder(this);
                final List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    if(!addresses.isEmpty()){
                        description = addresses.get(0).getAddressLine(1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                db.addAddress(loc.getLatitude(), loc.getLongitude(), description);
            }
        }
        fillShopList();
    }
}
