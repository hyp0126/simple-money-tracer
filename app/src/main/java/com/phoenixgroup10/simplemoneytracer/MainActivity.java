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
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.phoenixgroup10.simplemoneytracer.activity.CategoryActivity;
import com.phoenixgroup10.simplemoneytracer.activity.SettingsActivity;
import com.phoenixgroup10.simplemoneytracer.fragment.ActivityFragment;
import com.phoenixgroup10.simplemoneytracer.fragment.ReportFragment;
import com.phoenixgroup10.simplemoneytracer.service.NotificationService;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetNavigationDrawer();
    }

    private void SetNavigationDrawer()
    {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment frag = null;
                int itemId = item.getItemId();

                if (itemId == R.id.menuActivity)
                {
                    frag = new ActivityFragment();
                }
                else if (itemId == R.id.menuReport)
                {
                    frag = new ReportFragment();
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
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
        ed.putInt("notificationHour", 15);
        ed.putInt("notificationMin", 05);
        ed.commit();

        startService(new Intent(getApplicationContext(), NotificationService.class));

        super.onStop();
    }
}