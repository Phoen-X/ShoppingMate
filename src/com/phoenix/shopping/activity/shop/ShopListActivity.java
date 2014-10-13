package com.phoenix.shopping.activity.shop;

import static com.phoenix.shopping.activity.shop.ShopDescriptionSetupActivity.REQUEST_GET_DESCRIPTION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import com.phoenix.shopping.R;
import com.phoenix.shopping.adaptor.ShopListAdapter;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;
import com.phoenix.shopping.data.model.ShopAddress;
import com.phoenix.shopping.listeners.SwipeDismissListener;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class ShopListActivity extends Activity {
  public static final int ADD_SHOP_REQUEST    = 1;
  public static final int ADD_SHOP_BY_ADDRESS = 2;

  private ListView     shopList;
  private DataProvider db;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.shop_list);

    this.shopList = (ListView) findViewById(R.id.addrList);
    this.db = new SQLiteDataProvider(this);

    fillShopList();
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

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    if (data != null && resultCode == RESULT_OK && data.getExtras() != null) {
      final Object extra = data.getExtras().get(FindAddressActivity.DATA_KEY);
      if (extra != null && extra instanceof ShopAddress) {
        ShopAddress addr = (ShopAddress) extra;
        if (requestCode == ADD_SHOP_BY_ADDRESS) {
          Intent intent = new Intent(this, ShopDescriptionSetupActivity.class);
          intent.putExtra("address", addr);
          startActivityForResult(intent, REQUEST_GET_DESCRIPTION);
        } else {
          db.addAddress(addr.getLatitude(), addr.getLongitude(), addr.getDescription());
        }
      }
    }
    fillShopList();
  }

  private void fillShopList() {
    final ShopListAdapter adapter = new ShopListAdapter(this, db.getShopList());
    shopList.setAdapter(adapter);
    final SwipeDismissListener touchListener = new SwipeDismissListener(shopList,
                                                                        getDismissCallback(adapter));
    shopList.setOnTouchListener(touchListener);
    shopList.setOnScrollListener(touchListener.makeScrollListener());
    shopList.setEmptyView(findViewById(R.id.emptyListView));
  }

  private SwipeDismissListener.DismissCallbacks getDismissCallback(final ShopListAdapter adapter) {
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
            startActivityForResult(intent, ADD_SHOP_BY_ADDRESS);
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
}
