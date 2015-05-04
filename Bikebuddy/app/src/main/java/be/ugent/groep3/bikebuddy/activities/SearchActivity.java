package be.ugent.groep3.bikebuddy.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import be.ugent.groep3.bikebuddy.CustomViews.ClearableAutoCompleteTextView;
import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.logica.PlaceAutocompleteAdapter;
import be.ugent.groep3.bikebuddy.logica.RestClient;

public class SearchActivity extends FragmentActivity
        implements SeekBar.OnSeekBarChangeListener,View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private final int MAX_DISTANCE = 2;
    private final String [] radiusMessages = { "Not feeling it?", "Ok!","Good!","Very sportive!"};

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;
    private SeekBar sbDistance;

    private TextView tv_radius;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds MAPS_BOUNDS = new LatLngBounds(
            new LatLng(48.993117, 1.906756), new LatLng(51.602933, 6.499041));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the Google API Client if it has not been initialised yet.
        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
        }

        setContentView(R.layout.activity_search);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.actionbar_search_list_item,
                MAPS_BOUNDS, null);

        // Action bar instellen:
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_search);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ClearableAutoCompleteTextView mAutocompleteView = (ClearableAutoCompleteTextView) actionBar.getCustomView().findViewById(R.id.ab_location_search);
        ImageButton btnBack = (ImageButton) actionBar.getCustomView().findViewById(R.id.ab_back_button);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        btnBack.setOnClickListener(this);

        // Seekbar radius instellen:
        tv_radius = (TextView) findViewById(R.id.radius_result);
        sbDistance = (SeekBar) findViewById(R.id.seekbar_radius);
        sbDistance.setOnSeekBarChangeListener(this);
        onProgressChanged(sbDistance,sbDistance.getProgress(),true);
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully

                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            /* Format details of the place for display and show it in a TextView.
            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));*/

        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }


    /**
     * Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
     * functionality.
     * This automatically sets up the API client to handle Activity lifecycle events.
     */
    protected synchronized void rebuildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and connection failed
        // callbacks should be returned, which Google APIs our app uses and which OAuth 2.0
        // scopes our app requests.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

        // Disable API access in the adapter because the client was not initialised correctly.
        mAdapter.setGoogleApiClient(null);

    }


    @Override
    public void onConnected(Bundle bundle) {
        // Successfully connected to the API client. Pass it to the adapter to enable API access.
        mAdapter.setGoogleApiClient(mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ab_back_button:
                Intent intent;
                intent = NavUtils.getParentActivityIntent(this);
                NavUtils.navigateUpTo(this, intent);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tv_radius.setText(
                Double.toString(((double)(progress * MAX_DISTANCE)) / (double) seekBar.getMax()) + " km - "
                + radiusMessages[((int) Math.floor((double) progress / (double) (seekBar.getMax()+1) * radiusMessages.length))]);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void SearchSubmit(View view) {
        new Thread(new Runnable() {
            public void run() {
                double distance = ((double) (sbDistance.getProgress() * MAX_DISTANCE)) / (double) sbDistance.getMax();
                int responsecode = 0;

                RestClient restClient = new RestClient(getResources().getString(R.string.rest_stations));

                try {
                    restClient.Execute(RestClient.RequestMethod.GET);
                    responsecode = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String response = restClient.getResponse();
                Intent intent = new Intent();
                intent.putExtra("STATIONS", response);
                setResult(responsecode, intent);
                finish();
            }
        }).start();
    }
}