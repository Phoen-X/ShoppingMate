package com.phoenix.shopping.activity.shop;

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
public class AddShopByCoordsActivity extends Activity {

  EditText description;
  EditText longitude;
  EditText latitude;
  private DataProvider db;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_shop_by_coords);
    this.description = (EditText) findViewById(R.id.description);
    this.longitude = (EditText) findViewById(R.id.longVal);
    this.latitude = (EditText) findViewById(R.id.latVal);
    db = new SQLiteDataProvider(this);
  }

  public void cleanupErrorsAddShopByCoords(View v) {
    description.setError(null);
    longitude.setError(null);
    latitude.setError(null);
  }

  public void addShopByCoords(View view) {
    if(ValidationUtils.isEmpty(description)) {
      description.setError("Enter description");
      return;
    }
    if(ValidationUtils.isEmpty(latitude) || !ValidationUtils.isDouble(latitude)) {
      latitude.setError("Enter latitude");
      return;
    }
    if(ValidationUtils.isEmpty(longitude) || !ValidationUtils.isDouble(longitude)) {
      longitude.setError("Enter longitude");
      return;
    }

    db.addAddress(Double.valueOf(latitude.getText().toString()), Double.valueOf(longitude.getText().toString()), description.getText().toString());
    setResult(RESULT_OK);
    finish();

  }
}
