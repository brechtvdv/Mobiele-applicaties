package be.ugent.groep3.bikebuddy.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
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
public class LocationListFragment extends Fragment implements View.OnClickListener {

    private final int RESULT_OK = 1;
    private ListView listView;
    Double[] place;
    private CustomListAdapter adapter;

    public LocationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch(v);
            }
        });

        sortByBonuspoints();

        updateGUIList();

        return view;
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
        final Bundle extraBundle = data.getExtras();

        ArrayList<Integer> stationids = extraBundle.getIntegerArrayList("STATIONIDS");
        MySQLiteHelper sqlite = new MySQLiteHelper(getActivity());

        TabsActivity.bikestations.clear();
        for(Integer i : stationids) TabsActivity.bikestations.add(sqlite.getBikeStation(i));

        updateGUIList();

        String orderCriteria = extraBundle.getString("SORTCRITERIA");
        if(orderCriteria == "DISTANCE")
            sortByDistance();
        else sortByBonuspoints();

    }

    private void sortByDistance(){
        //Sorting using Anonymous inner class type
        Collections.sort(TabsActivity.bikestations, new Comparator() {
            @Override
            public int compare(Object s1, Object s2) {
                int distance1 = ((BikeStation) s1).getDistance();
                int distance2 = ((BikeStation) s2).getDistance();

                return distance1 - distance2;
            }
        });
    }

    private void sortByBonuspoints(){
        //Sorting using Anonymous inner class type
        Collections.sort(TabsActivity.bikestations, new Comparator() {
            @Override
            public int compare(Object s1, Object s2) {
                int bonus1 = ((BikeStation) s1).getBonuspoints();
                int bonus2 = ((BikeStation) s2).getBonuspoints();

                return bonus2-bonus1;
            }
        });
    }

    private void updateGUIList(){
        listView.setAdapter(new CustomListAdapter(getActivity(),this));
    }

    private class CustomListAdapter extends BaseAdapter{

        private Activity activity;
        private LocationListFragment fragment;
        private LayoutInflater inflater;
        private List<BikeStation> bikeLocations;

        public CustomListAdapter(Activity activity, LocationListFragment fragment){
            this.activity = activity;
            this.bikeLocations = TabsActivity.bikestations;
            this.fragment = fragment;
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
    }
}
