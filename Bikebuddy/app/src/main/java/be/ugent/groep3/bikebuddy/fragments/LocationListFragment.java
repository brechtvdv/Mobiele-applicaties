package be.ugent.groep3.bikebuddy.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.beans.Bikelocation;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationListFragment extends Fragment {

    public LocationListFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);
        // Inflate the layout for this fragment
        List<Bikelocation> bikelocations = new ArrayList<>();
        bikelocations.add(new Bikelocation("DE SMET", "PLACE HENRI DE SMET",2,345));
        bikelocations.add(new Bikelocation("PANNENHUIS", "RUE CHARLES DE GAULLE",1,699));
        bikelocations.add(new Bikelocation("WOESTE", "PLACE AGORA",0,110));
        bikelocations.add(new Bikelocation("NOORDSTATION", "NOORDSTATIONSTRAAT 1",0,380));
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(new CustomListAdapter(getActivity(),bikelocations));

        return view;
    }

    private class CustomListAdapter extends BaseAdapter{

        private Activity activity;
        private LayoutInflater inflater;
        private List<Bikelocation> bikeLocations;

        public CustomListAdapter(Activity activity, List<Bikelocation> bikeLocations){
            this.activity = activity;
            this.bikeLocations = bikeLocations;}

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

            Bikelocation bikelocation = bikeLocations.get(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(bikelocation.getLocationName());
            TextView address = (TextView) convertView.findViewById(R.id.address);
            address.setText(bikelocation.getLocationAddress());
            TextView points = (TextView) convertView.findViewById(R.id.points);
            points.setText(Integer.toString(bikelocation.getNumberOfPoints()));
            TextView distance = (TextView) convertView.findViewById(R.id.distance);
            distance.setText(Integer.toString(bikelocation.getDistance()) + "m");

            return convertView;
        }
    }
}
