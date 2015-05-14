package be.ugent.groep3.bikebuddy.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.activities.TabsActivity;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.logica.MarkerInfoWindowAdapter;
import be.ugent.groep3.bikebuddy.logica.Tools;
import be.ugent.groep3.bikebuddy.sqlite.MySQLiteHelper;


public class LocationMapFragment extends Fragment implements OnMapReadyCallback{

    private List<LatLng> positions;
    private final double EDGE = 0.05;
    private GoogleMap googleMap;
    private int previous = 0; // locations
    private static View view;
    private LayoutInflater inflater;
    public static HashMap<Marker, BikeStation> mMarkersHashMap;

    public LocationMapFragment() {
        // Required empty public constructor
        positions = new ArrayList<>();
        mMarkersHashMap = new HashMap<Marker, BikeStation>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try{
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_location_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed() && TabsActivity.visibleLoaded) {
            UIUpdate();
        }
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

    public void UIUpdate(){
        if (previous != TabsActivity.mapStations.size()) {
            positions.clear();
            googleMap.clear();
            for (BikeStation station : TabsActivity.mapStations) {
                LatLng ll = new LatLng(station.getLatitude(), station.getLongitude());
                Log.d("latlng", ll.latitude + "," + ll.longitude);
                positions.add(ll);
                Marker m;
                if (Tools.isInternetAvailable(getActivity().getApplicationContext())) {
                    m = googleMap.addMarker(new MarkerOptions()
                            .position(ll));
                    googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(inflater));
                    mMarkersHashMap.put(m, station);
                } else {
                    m = googleMap.addMarker(new MarkerOptions()
                            .position(ll)
                            .title(station.getName()));
                }
                if (station.getBonuspoints() > 0)
                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
            previous = TabsActivity.mapStations.size();
        }
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(calculateBoundingBox(), 0));
            }
        });
    }
}
