package be.ugent.groep3.bikebuddy.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.activities.DetailActivity;
import be.ugent.groep3.bikebuddy.activities.SearchActivity;
import be.ugent.groep3.bikebuddy.activities.TabsActivity;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.logica.Tools;
import be.ugent.groep3.bikebuddy.sqlite.MySQLiteHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationListFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final int RESULT_OK = 1;
    public ListView listView;
    Double[] place;
    public static CustomListAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private View footerView;
    private View patienceView;

    public int LOAD = 5; // amount of stations to show
    public int PAGE;

    public LocationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);
        listView = (ListView) view.findViewById(R.id.list);

        if(Tools.isInternetAvailable(getActivity().getApplicationContext())) {
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSearch(v);
                }
            });
        }

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        PAGE = 0;

        adapter = new CustomListAdapter(getActivity(),TabsActivity.visibleBikeStations,this);
        listView.setAdapter(adapter);

        TabsActivity tab = (TabsActivity) getActivity();
        if(!TabsActivity.stationsLoaded) {
            Thread threadStations = new Thread(null, tab.loadStations);
            threadStations.start();
        } else if(TabsActivity.customsearch) {
            onRefresh(); // thread loadStations doesn't run properly when executed second time
        }
        updateGUIList();

        //hide keyboard :
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return view;
    }

    public void addFooter(){
        if(listView.getFooterViewsCount()==0) {
            footerView = ((LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loading_bikelocations, null, false);
            footerView.setBackgroundColor(getResources().getColor(R.color.sa));
            listView.addFooterView(footerView);
            footerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadNext();
                }
            });
        }
    }

    public void deleteFooter(){
        listView.removeFooterView(footerView);
    }

    public void addPatience(){
        patienceView = ((LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loading_have_patience, null, false);
        listView.addHeaderView(patienceView);
    }

    public void deletePatience(){
        listView.removeHeaderView(patienceView);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(),DetailActivity.class);
        BikeStation station = (BikeStation)v.getTag(v.getId());
        intent.putExtra("STATION",station);
        startActivity(intent);
    }

    public void doSearch(View view){
        Intent intent = new Intent(getActivity(),SearchActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            final Bundle extraBundle = data.getExtras();

            ArrayList<Integer> stationids = extraBundle.getIntegerArrayList("STATIONIDS");
            MySQLiteHelper sqlite = new MySQLiteHelper(getActivity());

            TabsActivity.mapStations.clear();
            adapter.bikeLocations.clear();
            for(Integer i : stationids){
                BikeStation station = sqlite.getBikeStation(i);
                TabsActivity.visibleBikeStations.add(station);
                TabsActivity.mapStations.add(station);
            }

            String orderCriteria = extraBundle.getString("SORTCRITERIA");
            if(orderCriteria == "DISTANCE")
                adapter.sortByDistance();
            else adapter.sortByBonuspoints();

            deleteFooter();

            updateGUIList();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.pb_loading_rest);
//                    ProgressBar pbMap = (ProgressBar) getActivity().findViewById(R.id.pb_loading_rest2);
//                    pb.setVisibility(ProgressBar.GONE);
//                    pbMap.setVisibility(ProgressBar.GONE);
                }
            });
        } else {
            onRefresh();
        }

        //hide keyboard :
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void updateGUIList(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        TabsActivity.customsearch = false;
        deleteFooter();
        TabsActivity act = (TabsActivity) this.getActivity();
        swipeLayout.setRefreshing(true);

        TabsActivity.visibleBikeStations.clear();
        TabsActivity.stationsLoaded = false;
        Thread t = new Thread(null,act.loadStations);
        t.start();
        Thread tt = new Thread(null,act.loadUsers);
        tt.start();
        Log.i("user", "load User info");
        Thread ttt = new Thread(null, act.loadUserInfo);
        ttt.start();
        adapter.sortByBonuspoints();
        updateGUIList();

        swipeLayout.setRefreshing(false);
    }

    private void loadNext() {
            //is the bottom item visible & not loading more already ? Load more !
            if(!(TabsActivity.loadingMore)){
                TabsActivity tab = (TabsActivity) getActivity();
                Thread thread =  new Thread(null, tab.loadMoreListItems);
                thread.start();
            }
    }


    public class CustomListAdapter extends BaseAdapter{

        private Activity activity;
        private LocationListFragment fragment;
        private LayoutInflater inflater;
        private List<BikeStation> bikeLocations;

        public CustomListAdapter(Activity activity, List<BikeStation> stations, LocationListFragment fragment){
            this.activity = activity;
            this.bikeLocations = stations;
            this.fragment = fragment;
        }

        public void clear(){
            bikeLocations.clear();
        }

        @Override
        public int getCount() {
            return bikeLocations.size();
        }

        @Override
        public Object getItem(int position) {
            return bikeLocations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.location_list_row, null);

            // Bikelocations in component steken:
            BikeStation bikestation = bikeLocations.get(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(bikestation.getName());
            TextView address = (TextView) convertView.findViewById(R.id.address);
            address.setText(bikestation.getAddress());
            TextView points = (TextView) convertView.findViewById(R.id.points);
            points.setText(Integer.toString(bikestation.getBonuspoints()));
            TextView distance = (TextView) convertView.findViewById(R.id.distance);
            distance.setText(bikestation.getDistance() + "m");
            convertView.setTag(convertView.getId(),bikestation);
            convertView.setOnClickListener(fragment);

            return convertView;
        }

        public void add(BikeStation bikeStation) {
            bikeLocations.add(bikeStation);
        }

        public void sortByDistance(){
            //Sorting using Anonymous inner class type
            Collections.sort(bikeLocations, new Comparator() {
                @Override
                public int compare(Object s1, Object s2) {
                    int distance1 = ((BikeStation) s1).getDistance();
                    int distance2 = ((BikeStation) s2).getDistance();

                    return distance1 - distance2;
                }
            });
        }

        public void sortByBonuspoints(){
            //Sorting using Anonymous inner class type
            Collections.sort(bikeLocations, new Comparator() {
                @Override
                public int compare(Object s1, Object s2) {
                    int bonus1 = ((BikeStation) s1).getBonuspoints();
                    int bonus2 = ((BikeStation) s2).getBonuspoints();

                    return bonus2 - bonus1;
                }
            });
        }


    }
}
