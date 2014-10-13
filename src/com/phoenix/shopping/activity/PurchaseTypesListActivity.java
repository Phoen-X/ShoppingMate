package com.phoenix.shopping.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.phoenix.shopping.R;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;
import com.phoenix.shopping.listeners.SwipeDismissListener;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class PurchaseTypesListActivity extends Activity {

  private DataProvider db;
  private ListView typesList;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.purchase_type_list);
    db = new SQLiteDataProvider(this);
    this.typesList = (ListView) findViewById(R.id.typesList);
    fillPurchaseTypesList();
  }

  private void fillPurchaseTypesList() {
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, db.getPurchaseTypes());
    final SwipeDismissListener touchListener = new SwipeDismissListener(typesList,
                                                                        getDismissCallback(adapter));
    typesList.setAdapter(adapter);
    typesList.setOnTouchListener(touchListener);
    typesList.setOnScrollListener(touchListener.makeScrollListener());
  }

  public void addPurchaseTypeClick(View view) {
    Intent additionAction = new Intent(this, AddPurchaseTypeActivity.class);
    startActivityForResult(additionAction, 1);
  }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    if (resultCode == RESULT_OK) {
      fillPurchaseTypesList();
    }
  }

  private SwipeDismissListener.DismissCallbacks getDismissCallback(final ArrayAdapter<String> adapter) {
    return new SwipeDismissListener.DismissCallbacks() {
      @Override
      public boolean canDismiss(final int position) {
        return adapter.getCount() > 1;
      }

      @Override
      public void onDismiss(final ListView listView,
                            final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
          String itemToDelete = adapter.getItem(position);
          db.removePurchaseType(itemToDelete);
          adapter.remove(itemToDelete);
        }
        adapter.notifyDataSetChanged();
      }
    };
  }
}
