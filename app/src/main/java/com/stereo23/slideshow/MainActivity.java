package com.stereo23.slideshow;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;


public class MainActivity extends Activity {
    boolean isScheduleStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        isScheduleStart = intent.getBooleanExtra("schedule",false);
        setContentView(R.layout.activity_main);
        String folderPath = PreferenceManager.getDefaultSharedPreferences(this).getString("path", "null");
        int interval = PreferenceManager.getDefaultSharedPreferences(this).getInt("interval", 0);
        if (savedInstanceState == null || isScheduleStart) {
                Fragment fragment = new SlideshowFragment();
                if (isScheduleStart) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("schedule",true);
                    fragment.setArguments(bundle);
                }
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.settings) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public Fragment getActiveFragment() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
        return getFragmentManager().findFragmentByTag(tag);
    }

}
