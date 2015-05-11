package be.ugent.groep3.bikebuddy.logica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.fragments.LocationMapFragment;

/**
 * Created by brechtvdv on 11/05/15.
 */
public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;

    public MarkerInfoWindowAdapter(LayoutInflater inflater)
    {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        View v  = inflater.inflate(R.layout.infowindow_layout, null);

        BikeStation station = LocationMapFragment.mMarkersHashMap.get(marker);

        ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);
        TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);
        TextView markerBikes = (TextView)v.findViewById(R.id.marker_available_bikes);
        TextView markerBonuspoints = (TextView)v.findViewById(R.id.marker_bonuspoints);

        markerIcon.setImageResource(R.drawable.bike_icon);
        markerLabel.setText(station.getName());
        markerBikes.setText("Available bikes: " + station.getAvailable_bikes() + "/" + station.getBike_stands());
        markerBonuspoints.setText("Bonuspoints: " + station.getBonuspoints());

        return v;
    }
}
