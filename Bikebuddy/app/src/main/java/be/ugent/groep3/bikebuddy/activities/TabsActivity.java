package be.ugent.groep3.bikebuddy.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.WindowManager;

import com.astuetz.PagerSlidingTabStrip;
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
import be.ugent.groep3.bikebuddy.logica.RestClient;
import be.ugent.groep3.bikebuddy.logica.Tools;
import be.ugent.groep3.bikebuddy.sqlite.MySQLiteHelper;


public class TabsActivity extends FragmentActivity {

    private TabsPagerAdapter tabsPagerAdapter;
    private ViewPager viewPager;
    private ActionBar actionBar;

    private LocationListFragment loclistfrag;
    private LocationMapFragment locmapfrag;
    public static boolean stationsLoaded = false;
    public static boolean visibleLoaded = false;

    public static boolean customsearch = false;

    public static List<BikeStation> bikestations; // holds all realtime loaded bikestations
    public static List<BikeStation> visibleBikeStations; // holds all visible stations for adapter so user doesn't get to see all stations in one list directly
    public static List<BikeStation> mapStations;
    public static List<User> users;

    public static boolean loadingMore = false;
    private List<BikeStation> myListItems; // temp variable for updating listview

    //Runnable to load the items
    public Runnable loadMoreListItems = new Runnable() {
        @Override
        public void run() {
            //synchronized (this) {
               // while (!stationsLoaded) {
                    //Set flag so we cant load new items 2 at the same time
                    loadingMore = true;
                    //Reset the array that holds the new items
                    myListItems = new ArrayList<BikeStation>();

                    int verschil = bikestations.size() - (loclistfrag.PAGE*loclistfrag.LOAD)+loclistfrag.LOAD;

                    if(verschil < loclistfrag.LOAD) {
                        for (int i = (loclistfrag.PAGE * loclistfrag.LOAD); i < verschil; i++)
                            myListItems.add(TabsActivity.bikestations.get(i));
                    } else if(TabsActivity.bikestations.size()>0){
                        for (int i = (loclistfrag.PAGE * loclistfrag.LOAD); i < (loclistfrag.PAGE * loclistfrag.LOAD) + loclistfrag.LOAD; i++)
                            myListItems.add(TabsActivity.bikestations.get(i));
                    }

                    loclistfrag.PAGE++;
                    //Done! now continue on the UI thread
                    runOnUiThread(returnRes);
                //}
            //}
        }
    };

    //Since we cant update our UI from a thread this Runnable takes care of that!
    public Runnable returnRes = new Runnable() {
        @Override
        public void run() {
            //Loop thru the new items and add them to the adapter
            if(myListItems != null && myListItems.size() > 0){
                for(int i=0;i < myListItems.size();i++){
                    //loclistfrag.adapter.add(myListItems.get(i));
                    visibleBikeStations.add(myListItems.get(i));
                }
            }
            int aantal = loclistfrag.adapter.getCount();

            if(loclistfrag.listView.getFooterViewsCount()==0){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loclistfrag.addFooter();
                    }
                });
            }

            loclistfrag.updateGUIList();

            //Done loading more.
            loadingMore = false;
            visibleLoaded = true;
        }
    };

    //Runnable to load the items
    public Runnable loadUsers = new Runnable() {
        @Override
        public void run() {
            // online
            if (Tools.isInternetAvailable(getApplicationContext())) {
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
    };

    public Runnable loadStations = new Runnable() {
        @Override
        public void run() {
            //synchronized(this) {
                if(!customsearch){
                    if(!stationsLoaded){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loclistfrag.addPatience();
                               //ProgressBar pb = (ProgressBar) findViewById(R.id.pb_loading_rest3);
                                //ProgressBar pbMap = (ProgressBar) findViewById(R.id.pb_loading_rest2);
                                //pb.setVisibility(ProgressBar.VISIBLE);
                                //pbMap.setVisibility(ProgressBar.VISIBLE);
                            }
                        });

                        // SQLite data inladen
                        // bikestations inladen
                        MySQLiteHelper sqlite = new MySQLiteHelper(getApplicationContext());

                        // aantal stations opvragen in geheugen
                        final int aantal = sqlite.getAllBikeStations().size();

                        if (Tools.isInternetAvailable(getApplicationContext())) {
                            InputStream source = Tools.retrieveStream(getResources().getString(R.string.rest_stations));
                            Gson gson = new Gson();
                            Reader reader = new InputStreamReader(source);
                            bikestations = gson.fromJson(reader, new TypeToken<List<BikeStation>>() {
                            }.getType());

                            for (BikeStation station : bikestations) {
                                if (aantal == 0) sqlite.addBikeStation(station);
                                else { // update with realtime info
                                    BikeStation s = sqlite.getBikeStation(station.getId());
                                    s.setBonuspoints(station.getBonuspoints());
                                    s.setAvailable_bike_stands(station.getAvailable_bike_stands());
                                    s.setAvailable_bikes(station.getAvailable_bikes());
                                    sqlite.updateBikeStation(s);
                                }
                            }
                        } else {
                            // geheugen
                            for (BikeStation station : sqlite.getAllBikeStations()) {
                                station.setBonuspoints(0);
                                station.setDistance(0);
                                sqlite.updateBikeStation(station);
                            }
                            bikestations = sqlite.getAllBikeStations();
                        }
                        stationsLoaded = true;
                    }
                    // had to put this here, otherwise synchronizing errors
                    Thread threadContent = new Thread(null, loadVisibleStations);
                    threadContent.start();

                    mapStations = bikestations;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loclistfrag.addFooter();
                            loclistfrag.deletePatience();
                            //ProgressBar pb = (ProgressBar) findViewById(R.id.pb_loading_rest3);
                            //ProgressBar pbMap = (ProgressBar) findViewById(R.id.pb_loading_rest2);
                            //pb.setVisibility(ProgressBar.GONE);
                            //pbMap.setVisibility(ProgressBar.GONE);
                        }
                    });
                }
            }
        //}
    };

    public Runnable loadVisibleStations = new Runnable() {
        @Override
        public void run() {
            //synchronized (this) {
                //while (!stationsLoaded) {
                    //is the bottom item visible & not loading more already ? Load more !
                    if(!(loadingMore) && loclistfrag.PAGE+loclistfrag.LOAD < TabsActivity.bikestations.size()){
                        Thread thread =  new Thread(null, loadMoreListItems);
                        thread.start();
                    }
                //}
            //}
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        bikestations = new ArrayList<BikeStation>();
        visibleBikeStations = new ArrayList<BikeStation>();
        mapStations = new ArrayList<BikeStation>();

        loclistfrag = new LocationListFragment();
        locmapfrag = new LocationMapFragment();

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
            Thread threadUsers =  new Thread(null, loadUsers);
            threadUsers.start();
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
            fragments.add(loclistfrag);
            fragments.add(locmapfrag);
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

