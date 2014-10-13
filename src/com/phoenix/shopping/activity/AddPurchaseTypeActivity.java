package com.phoenix.shopping.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.phoenix.shopping.R;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;
import com.phoenix.shopping.util.ValidationUtils;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class AddPurchaseTypeActivity extends Activity {

  private EditText     typeName;
  private DataProvider db;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_purchase_type);

    typeName = (EditText) findViewById(R.id.typeName);
    db = new SQLiteDataProvider(this);
  }

  public void cleanupValidationErrors(View v) {
    typeName.setError(null);
  }

  public void addPurchaseTypeSubmit(View v) {
    if (ValidationUtils.isEmpty(typeName)) {
      typeName.setError(getString(R.string.err_purchase_type_empty));
      return;
    }

    db.addPurchaseType(typeName.getText().toString());
    setResult(RESULT_OK);
    finish();
  }
}
