package com.phoenixgroup10.simplemoneytracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.phoenixgroup10.simplemoneytracer.activity.CategoryActivity;
import com.phoenixgroup10.simplemoneytracer.activity.SettingsActivity;
import com.phoenixgroup10.simplemoneytracer.fragment.ActivityFragment;
import com.phoenixgroup10.simplemoneytracer.fragment.AddActivityFragment;
import com.phoenixgroup10.simplemoneytracer.fragment.ReportFragment;
import com.phoenixgroup10.simplemoneytracer.service.NotificationService;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private boolean activeReportFragment;
    static private Intent mServiceIntent = null;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SimpleMoneyTracer);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetNavigationDrawer();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setFragment(new ActivityFragment());
    }

    // for ReportFragment land mode
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
            || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (activeReportFragment) {
                setFragment(new ReportFragment());
            }
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.frame, fragment);
        trans.commit();
    }

    private void SetNavigationDrawer()
    {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                activeReportFragment = false;
                Fragment frag = null;
                int itemId = item.getItemId();

                if (itemId == R.id.menuActivity)
                {
                    frag = new ActivityFragment();
                }
                else if (itemId == R.id.menuReport)
                {
                    frag = new ReportFragment();
                    activeReportFragment = true;
                }
                else if (itemId == R.id.menuCategory)
                {
                    startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                    return true;
                }
                else if (itemId == R.id.menuSettings)
                {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    return true;
                }

                if (frag != null)
                {
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.replace(R.id.frame, frag);
                    trans.commit();
                    mDrawerLayout.closeDrawers();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(getApplicationContext(), NotificationService.class);
            startService(mServiceIntent);
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        // If notification service is active, turn off
        if (mServiceIntent != null) {
            // Set Notification Timer off (set 0)
            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putLong("notificationDateTime", 0);
            ed.commit();

            stopService(mServiceIntent);
            mServiceIntent = null;
        }
        super.onStart();
    }
}