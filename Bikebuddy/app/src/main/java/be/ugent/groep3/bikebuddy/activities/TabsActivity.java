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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.beans.User;
import be.ugent.groep3.bikebuddy.fragments.LocationListFragment;
import be.ugent.groep3.bikebuddy.fragments.LocationMapFragment;
import be.ugent.groep3.bikebuddy.fragments.ScoreboardFragment;
import be.ugent.groep3.bikebuddy.fragments.UserFragment;
import be.ugent.groep3.bikebuddy.listeners.SimpleOnPageChangeListener;
import be.ugent.groep3.bikebuddy.listeners.TabListener;
import be.ugent.groep3.bikebuddy.logica.RestClient;
import be.ugent.groep3.bikebuddy.logica.Tools;
import be.ugent.groep3.bikebuddy.sqlite.MySQLiteHelper;


public class TabsActivity extends FragmentActivity {

    private TabsPagerAdapter tabsPagerAdapter;
    private ViewPager viewPager;
    private ActionBar actionBar;

    public static List<BikeStation> bikestations;
    public static List<User> users;

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

        // load on boot, not when returning from DetailActivity
        if(getCallingActivity() == null){
            loadStations();
            loadUsers();
        }
    }


    public void loadUsers() {
        // online
        if(Tools.isInternetAvailable(getApplicationContext()) ) {
            if (Tools.isInternetAvailable(getApplicationContext())) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        RestClient restClient = new RestClient(getResources().getString(R.string.rest_scoreboard));
                        try {
                            restClient.Execute(RestClient.RequestMethod.GET);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String response = restClient.getResponse();
                        Gson gson = new Gson();
                        users = gson.fromJson(response, new TypeToken<List<User>>() {
                        }.getType());
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadStations() {
// SQLite data inladen
        // bikestations inladen
        MySQLiteHelper sqlite = new MySQLiteHelper(getApplicationContext());

        // aantal stations opvragen in geheugen
        final int aantal = sqlite.getAllBikeStations().size();

        if(Tools.isInternetAvailable(getApplicationContext())) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    MySQLiteHelper sqlite = new MySQLiteHelper(getApplicationContext());
                    InputStream source = Tools.retrieveStream(getResources().getString(R.string.rest_stations));
                    Gson gson = new Gson();
                    Reader reader = new InputStreamReader(source);
                    TabsActivity.bikestations = gson.fromJson(reader, new TypeToken<List<BikeStation>>() {}.getType());

                    for (BikeStation station : TabsActivity.bikestations) {
                        if(aantal == 0) sqlite.addBikeStation(station);
                        else{ // update with realtime info
                            BikeStation s = sqlite.getBikeStation(station.getId());
                            s.setBonuspoints(station.getBonuspoints());
                            s.setAvailable_bike_stands(station.getAvailable_bike_stands());
                            s.setAvailable_bikes(station.getAvailable_bikes());
                            sqlite.updateBikeStation(s);
                        }
                    }
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            // geheugen
            for (BikeStation station : sqlite.getAllBikeStations()){
                station.setBonuspoints(0);
                station.setDistance(0);
                sqlite.updateBikeStation(station);
            }
            TabsActivity.bikestations = sqlite.getAllBikeStations();
        }
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

