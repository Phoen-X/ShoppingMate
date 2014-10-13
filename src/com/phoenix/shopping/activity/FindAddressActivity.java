package com.phoenix.shopping.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.phoenix.shopping.R;
import com.phoenix.shopping.adaptor.FoundShopsAdapter;
import com.phoenix.shopping.data.DataProvider;
import com.phoenix.shopping.data.SQLiteDataProvider;

import java.io.IOException;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;
import static android.widget.AdapterView.OnItemClickListener;

/**
 * Class description here.
 *
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class FindAddressActivity extends Activity {

    public static final String DATA_KEY = "location";
    private ListView foundShopsList;
    private Button addBtn;
    private EditText searchStr;
    private DataProvider db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_shop);
        db = new SQLiteDataProvider(this);
        foundShopsList = (ListView) findViewById(R.id.listFoundShops);
        foundShopsList.setEmptyView(findViewById(R.id.emptyListView));
        searchStr = (EditText) findViewById(R.id.searchStr);
        addBtn = (Button) findViewById(R.id.addShopBtn);

        addBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });
    }

    public void btnSearchShopsClick(View view) {
        final FoundShopsAdapter currAdapter = (FoundShopsAdapter) foundShopsList.getAdapter();
        if (currAdapter != null) {
            currAdapter.clear();
            currAdapter.notifyDataSetChanged();
        }
        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> places = geocoder.getFromLocationName(String.valueOf(searchStr.getText()), 20);
            if (places.isEmpty()) {
                Toast.makeText(this, "Nothing found", Toast.LENGTH_SHORT).show();
            }

            final FoundShopsAdapter adapter = new FoundShopsAdapter(this, places);
            foundShopsList.setAdapter(adapter);
            foundShopsList.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                    adapter.check(position);
                    adapter.notifyDataSetChanged();
                    addBtn.setEnabled(true);
                }
            });
        } catch (IOException e) {
            foundShopsList.setEmptyView(findViewById(android.R.id.empty));
            Toast.makeText(this, "Cannot find shop: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void btnAddShopClick(View view) {

        final Address checked = ((FoundShopsAdapter) foundShopsList.getAdapter()).getChecked();
        Intent result = new Intent();
        Location loc = new Location("NO_PROVIDER");
        loc.setLongitude(checked.getLongitude());
        loc.setLatitude(checked.getLatitude());
        result.putExtra(DATA_KEY, loc);
        //db.addAddress(checked);
        setResult(RESULT_OK, result);
        finish();
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        final View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), HIDE_IMPLICIT_ONLY);
        }
    }
}
