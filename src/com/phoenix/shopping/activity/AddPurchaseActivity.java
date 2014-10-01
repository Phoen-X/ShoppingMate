package com.phoenix.shopping.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.phoenix.shopping.R;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;
import com.phoenix.shopping.data.model.Purchase;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
@SuppressWarnings("ConstantConditions")
public class AddPurchaseActivity extends Activity {
  private EditText name;
  private EditText quantity;
  private Spinner typeList;
  private DataProvider db;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_purchase);

    name = (EditText) findViewById(R.id.name);
    quantity = (EditText) findViewById(R.id.quantity);
    typeList = (Spinner) findViewById(R.id.typeList);
    db = new SQLiteDataProvider(this);

    fillPurchaseTypes();
  }

  private void fillPurchaseTypes() {
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
    DataProvider db = new SQLiteDataProvider(this);
    adapter.addAll(db.getPurchaseTypes());
    typeList.setAdapter(adapter);
  }

  public void addPurchase(View view) {
    if(validPurchase()) {

      Purchase item = createPurchase();
      db.addPurchase(item);

      cleanFields();
      name.requestFocus();
      setResult(RESULT_OK);

      //finish();
    }

  }

  private void cleanFields() {
    name.setText("");
    quantity.setText("");
    typeList.setSelection(0);
  }

  private Purchase createPurchase() {
    Purchase item = new Purchase();
    item.setName(name.getText().toString());
    item.setQuantity(quantity.getText().toString());
    item.setType(typeList.getSelectedItem().toString());
    return item;
  }

  private boolean validPurchase() {
    if(isEmpty(name)) {
      name.setError(getString(R.string.err_purchase_name_empty));
      return false;
    }
    /*if(isEmpty(quantity)) {
      quantity.setError(getString(R.string.err_purchase_quantity_empty));
      return false;
    }*/
    if(!isEmpty(quantity) && !isNumber(quantity.getText().toString())) {
      quantity.setError(getString(R.string.err_purchase_quantity_nan));
      return false;
    }
    return true;
  }

  private boolean isEmpty(EditText edit) {
    return edit == null || edit.getText() == null || edit.getText().toString().length() == 0;
  }

  public void clearEdits(View v) {
    name.setError(null);
    quantity.setError(null);
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private boolean isNumber(String value) {
    try {
      Integer.parseInt(value);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
