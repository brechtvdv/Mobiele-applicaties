package be.ugent.groep3.bikebuddy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.sqlite.MySQLiteHelper;


public class LocationMapFragment extends Fragment implements OnMapReadyCallback{

    private List<LatLng> positions;
    private final double EDGE = 0.05;

    public LocationMapFragment() {
        // Required empty public constructor
        positions = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_map, container, false);

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MySQLiteHelper sqlite = new MySQLiteHelper(getActivity());

        for(BikeStation station : sqlite.getAllBikeStations()){
            LatLng ll = new LatLng(station.getLatitude(), station.getLongitude());
            Log.d("latlng", ll.latitude + "," + ll.longitude);
            positions.add(ll);
            googleMap.addMarker(new MarkerOptions()
                    .position(ll)
                    .title(station.getName()));
        }
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(calculateBoundingBox(), 0));
            }
        });
    }

    private LatLngBounds calculateBoundingBox(){
        double maxLong = Double.MIN_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLong = Double.MAX_VALUE;
        double minLat = Double.MAX_VALUE;
        for ( LatLng pos : positions ){
            if ( maxLong < pos.longitude ) maxLong = pos.longitude;
            if ( minLong > pos.longitude ) minLong = pos.longitude;
            if ( maxLat < pos.longitude ) maxLat = pos.latitude;
            if ( minLat > pos.longitude ) minLat = pos.latitude;
        }
        return new LatLngBounds(
                new LatLng(minLat - EDGE, minLong - EDGE),
                new LatLng(maxLat + EDGE, maxLong + EDGE));
    }
}
