package be.ugent.groep3.bikebuddy.activities;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.fragments.LocationListFragment;
import be.ugent.groep3.bikebuddy.fragments.LocationMapFragment;
import be.ugent.groep3.bikebuddy.listeners.SimpleOnPageChangeListener;
import be.ugent.groep3.bikebuddy.listeners.TabListener;


public class TabsActivity extends FragmentActivity {

    private TabsPagerAdapter tabsPagerAdapter;
    private ViewPager viewPager;
    private ActionBar actionBar;

    public static List<BikeStation> bikestations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        // Viewpager en adapter voor weergeven tabs:
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(tabsPagerAdapter);

        // Actionbar registreren:
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        ActionBar.TabListener tabListener = new TabListener(viewPager);
        for (int i = 0; i < tabsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setIcon(tabsPagerAdapter.getIcon(i))
                    .setTabListener(tabListener));
        }
        viewPager.setOnPageChangeListener(new SimpleOnPageChangeListener(actionBar));

        // Laat het toetsenbord niet zien:
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Pager adapter voor het tonen van de verschillende tabpgn's:
    public class TabsPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;
        private List<Integer> icons;

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments = new ArrayList<>();
            icons = new ArrayList<>();
            fragments.add(new LocationListFragment());
            fragments.add(new LocationMapFragment());
            icons.add(R.drawable.list_icon);
            icons.add(R.drawable.location_menu_icon);
        }

        public int getIcon(int i){ return icons.get(i); }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}

