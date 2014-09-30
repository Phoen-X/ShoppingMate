package com.phoenix.shopping.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.example.shopping.R;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class MainPreferencesActivity extends PreferenceActivity {

  private static final String TAG = "SHOP_LIST_PREFS";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.main_preferences);
  }

  /*@Override
  protected void onStop() {
    Intent gpsTracker = new Intent(this, LocationListenerService.class);
    if(stopService(gpsTracker)) {
      Log.d(TAG, "GPS tracker stopped");
    } else {
      Log.d(TAG, "GPS tracker failed to stop");
    }

    startService(gpsTracker);

    super.onStop();
  }*/
}
