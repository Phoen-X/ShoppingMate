package com.phoenix.shopping.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.phoenix.shopping.R;

import java.util.Map;

/**
 * Class description here.
 *
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class MainPreferencesFragment extends PreferenceFragment {
    private static final String TAG = "SHOP_LIST_PREFS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preferences);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        updateDetails(prefs);
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                updateDetails(prefs);
            }
        });
    }

    private void updateDetails(SharedPreferences prefs) {
        if (prefs == null || prefs.getAll() == null) {
            return;
        }

        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            final Preference preference = getPreferenceManager().findPreference(entry.getKey());
            int messageId = -1;
            Object[] params = new Object[0];
            boolean needToSet = true;
            switch (entry.getKey()) {
                case "pref_gps_on": {
                    messageId = R.string.pref_detail_gps_status;
                    int valueId = (Boolean)entry.getValue() ? R.string.pref_value_gps_on : R.string.pref_value_gps_off;
                    params = new String[]{getString(valueId)};
                    break;
                }
                case "pref_check_frequency": {
                    messageId = R.string.pref_value_frequency;
                    params = new String[] { String.valueOf(entry.getValue())};
                    break;
                }
                case "pref_forget_time": {
                    messageId = R.string.pref_value_forget_time;
                    params = new String[] {String.valueOf(entry.getValue())};
                    break;
                }
                case "pref_notify_distance":{
                    messageId = R.string.pref_value_react_distance;
                    params = new String[]{String.valueOf(entry.getValue())};
                    break;
                }
                default: {
                    needToSet = false;
                }
            }

            if (needToSet && messageId != -1) {
                String detailValue = getString(messageId, params);
                preference.setSummary(detailValue);
            }
        }

    }
}
