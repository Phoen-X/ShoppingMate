package com.phoenix.shopping.activity.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.phoenix.shopping.R;
import com.phoenix.shopping.data.model.ShopAddress;

/**
 * Class description here.
 *
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class ShopDescriptionSetupActivity extends Activity {
    public static final int REQUEST_GET_DESCRIPTION = 271;
    private EditText editDescription;
    private ShopAddress address;
    private static final String TAG = ShopDescriptionSetupActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_description_setup);
        editDescription = (EditText) findViewById(R.id.shopDesc);
        address = (ShopAddress) getIntent().getParcelableExtra("address");
        Log.d(TAG, "Extra is: " + address);
    }

    public void descriptionSubmitClick(View v) {
        final Editable description = editDescription.getText();
        if(description != null && description.length() > 0) {
            Intent result = new Intent();
            if(address != null) {
                address.setDescription(description.toString());
                result.putExtra("address", address);
            }
            setResult(RESULT_OK, result);
            finish();
        }

        //TODO data validation
    }
}
