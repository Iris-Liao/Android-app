package com.iris.android.inews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by iris on 2017-02-03.
 */

public class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            Preference searchBy = findPreference(getString(R.string.settings_search_by_key));
            bindPreferenceSummaryToValue(searchBy);
        }

        /*
                Since this is the first ListPreference that the EarthquakePreferenceFragment is encountering,
                update the onPreferenceChange() method in EarthquakePreferenceFragment
                 to properly update the summary of a ListPreference (using the label, instead of the key).

         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            ListPreference listPreference = (ListPreference) preference;//casting preference to a listpreference
            //0-default value-politics;1-economy;2-history
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                CharSequence[] labels = listPreference.getEntries();//array entries
                preference.setSummary(labels[prefIndex]);//set the label of listPreference as summary,cuz only labels will be translated when localizing an app
            }

            preference.setSummary(stringValue);//used for the first time calling onPreferenceChange in the bind...ToValue method

            return true;

        }

        /*
        1.•	However, we still need to update the preference summary
        when the settings activity is launched. Given the key of a preference,
        we can use PreferenceFragment's findPreference() method to get the Preference object,
        and setup the preference using a helper method called bindPreferenceSummaryToValue().
2.only used when the setting Activity is started-to set summary for each preference*/
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");//if the current key is not related to any value, then return null which is the default value
           onPreferenceChange(preference, preferenceString);
        }
    }
}
