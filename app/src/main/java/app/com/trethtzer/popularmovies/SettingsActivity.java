package app.com.trethtzer.popularmovies;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import app.com.trethtzer.popularmovies.utilities.AppCompatPreferenceActivity;


/* Creo un settings activity y me quedo con lo que me interesa */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_general);

    }
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }



}