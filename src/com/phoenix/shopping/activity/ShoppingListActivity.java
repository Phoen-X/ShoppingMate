package com.phoenix.shopping.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.phoenix.shopping.R;
import com.phoenix.shopping.activity.shop.ShopListActivity;
import com.phoenix.shopping.adaptor.BuyListAdapter;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;
import com.phoenix.shopping.data.model.Purchase;
import com.phoenix.shopping.listeners.SwipeDismissListener;
import com.phoenix.shopping.service.LocationListenerService;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class ShoppingListActivity extends Activity {
  private static final int ADD_PURCHASE = 1;
  private DataProvider db;
  private ListView     listView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.shopping_list);
    synchronized (LocationListenerService.SERVICE_LOCK) {
      if (!LocationListenerService.IS_ACTIVE) {
        startService(new Intent(this, LocationListenerService.class));
      }
    }

    listView = (ListView) findViewById(R.id.buyListView);
    listView.setEmptyView(findViewById(R.id.emptyView));

    db = new SQLiteDataProvider(this);

    loadBuyList();
  }

  @Override
  protected void onDestroy() {
    //stopService(locationListener);
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_types_settings: {
        Intent typeListActivity = new Intent(this, PurchaseTypesListActivity.class);
        startActivity(typeListActivity);
        break;
      }
      case R.id.menu_add_purchase: {
        Intent addActivity = new Intent(this, AddPurchaseActivity.class);
        startActivityForResult(addActivity, ADD_PURCHASE);
        break;
      }
      case R.id.menu_address_settings: {
        Intent shopsActivity = new Intent(this, ShopListActivity.class);
        startActivity(shopsActivity);
        break;
      }
      case R.id.menu_settings: {
        Intent prefsIntent = new Intent(this, MainPreferencesActivity.class);
        startActivity(prefsIntent);
        break;
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    loadBuyList();
  }

  private void loadBuyList() {
    final BuyListAdapter adapter = new BuyListAdapter(this, db.getPurchaseList());
    listView.setAdapter(adapter);
    final SwipeDismissListener touchListener = new SwipeDismissListener(listView,
                                                                        getDismissCallback(adapter));
    listView.setOnTouchListener(touchListener);
  /*View emptyView = getLayoutInflater().inflate(R.layout.empty_list_view, null);
    listView.setEmptyView(emptyView);*/
    listView.setOnScrollListener(touchListener.makeScrollListener());
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (position < adapter.getCount()) {
          final Purchase item = adapter.getItem(position);
          item.setSold(!item.isSold());
          db.sell(item.getId(), item.isSold());
          adapter.notifyDataSetChanged();
        }
      }
    });
  }

  private SwipeDismissListener.DismissCallbacks getDismissCallback(final BuyListAdapter adapter) {
    return new SwipeDismissListener.DismissCallbacks() {
      @Override
      public boolean canDismiss(final int position) {
        return true;
      }

      @Override
      public void onDismiss(final ListView listView,
                            final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
          Purchase itemToDelete = adapter.getItem(position);
          db.removePurchase(itemToDelete);
          adapter.remove(position);
        }
        adapter.notifyDataSetChanged();
      }
    };
  }
}
