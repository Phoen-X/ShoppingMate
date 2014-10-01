package com.phoenix.shopping.activity;

import android.app.Activity;
import android.os.Bundle;
import com.phoenix.shopping.R;
import com.phoenix.shopping.fragment.MainPreferencesFragment;

/**
 * Class description here.
 *
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class MainPreferencesActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_fragment);
        getFragmentManager().beginTransaction()
                .add(R.id.mainFragment, new MainPreferencesFragment())
                .commit();
    }
}
