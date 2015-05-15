package be.ugent.groep3.bikebuddy.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import be.ugent.groep3.bikebuddy.DataSingleton;
import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.logica.RestClient;
import be.ugent.groep3.bikebuddy.logica.Tools;

public class DetailActivity extends Activity implements View.OnClickListener, OnMapReadyCallback{

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private final String title = "Detailed location";
    private List<LatLng> positions;
    private final double EDGE = 0.005;
    private ImageButton btnBack;
    private BikeStation bikeStation;

    public DetailActivity() {
        positions = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // to refresh locationlist Fragment
        TabsActivity.customsearch = true;

        // STATION OPHALEN:
        Intent intent = getIntent();
        bikeStation = (BikeStation) intent.getSerializableExtra("STATION");
        positions.add(new LatLng(bikeStation.getLatitude(), bikeStation.getLongitude()));

        // ACTIONBAR:
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_standard);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        TextView tv_title = (TextView) actionBar.getCustomView().findViewById(R.id.ab_title);
        tv_title.setText(title);
        btnBack = (ImageButton) actionBar.getCustomView().findViewById(R.id.ab_back_button);
        btnBack.setOnClickListener(this);

        // VIEWS OPHALEN:
        TextView tvName = (TextView) findViewById(R.id.tv_detail_name);
        TextView tvAddress = (TextView) findViewById(R.id.tv_detail_address);
        TextView tvBonus = (TextView) findViewById(R.id.tv_detail_points);
        TextView tvAvailableBikes = (TextView) findViewById(R.id.tv_detail_available_bikes);
        ImageButton btnScan = (ImageButton) findViewById(R.id.scan_button);

        tvName.setText(bikeStation.getName());
        tvAddress.setText(bikeStation.getAddress());

        // online
        if(Tools.isInternetAvailable(getApplicationContext())) {
            tvBonus.setText(Integer.toString(bikeStation.getBonuspoints()));
            tvAvailableBikes.setText(Integer.toString(bikeStation.getAvailable_bikes()) + "/" + Integer.toString(bikeStation.getBike_stands()));
            btnScan.setOnClickListener(this);
        } else {
            tvBonus.setVisibility(View.GONE);
            tvAvailableBikes.setVisibility(View.GONE);
            btnScan.setVisibility(View.GONE);

            findViewById(R.id.detail_star).setVisibility(View.GONE);
            findViewById(R.id.detail_available_bikes_text).setVisibility(View.GONE);
            findViewById(R.id.detail_available_bikes_text).setVisibility(View.GONE);
            findViewById(R.id.detail_qr_text).setVisibility(View.GONE);
        }

        // KAART INLADEN:
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
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

    @Override
    public void onClick(View v) {
        if ( v.equals(btnBack) ){
            Intent intent;
            intent = NavUtils.getParentActivityIntent(this);
            NavUtils.navigateUpTo(this, intent);
        }else {
            try {
                //start the scanning activity from the com.google.zxing.client.android.SCAN intent
                Intent intent = new Intent(ACTION_SCAN);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException anfe) {
                //on catch, show the download dialog
                Log.i("QR-code", "No scanner found");
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                sendCode(contents);
                //Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                //toast.show();
            }
        }
    }

    private void sendCode(final String contents){
        Thread t = new Thread(new Runnable() {
            public void run() {
                RestClient restClient = new RestClient(getResources().getString(R.string.rest_scan));
                try {
                    restClient.AddParam("email", DataSingleton.getData().getEmail());
                    restClient.AddParam("code", contents);
                    restClient.Execute(RestClient.RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        for( LatLng pos : positions ) {
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(bikeStation.getName()));
            if(bikeStation.getBonuspoints()>0) m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

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
