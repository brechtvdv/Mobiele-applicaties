package be.ugent.groep3.bikebuddy.activities;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.fragments.LocationListFragment;
import be.ugent.groep3.bikebuddy.fragments.LocationMapFragment;
import be.ugent.groep3.bikebuddy.fragments.ScoreboardFragment;
import be.ugent.groep3.bikebuddy.fragments.UserFragment;
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
       getActionBar().hide();
        viewPager.setOnPageChangeListener(new SimpleOnPageChangeListener(actionBar));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

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
    public class TabsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider{

        private List<Fragment> fragments;
        private List<Integer> icons;
        private List<String> titles;

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments = new ArrayList<>();
            icons = new ArrayList<>();
            titles = new ArrayList<>();
            fragments.add(new LocationListFragment());
            fragments.add(new LocationMapFragment());
            fragments.add(new ScoreboardFragment());
            fragments.add(new UserFragment());
            icons.add(R.drawable.list_icon);
            icons.add(R.drawable.location_menu_icon);
            icons.add(R.drawable.leaderboard);
            icons.add(R.drawable.profile);
            titles.add("Location List");
            titles.add("Location Map");
            titles.add("Scoreboard");
            titles.add("User Information");

        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public int getPageIconResId(int i) {
            return icons.get(i);
        }
    }


}

