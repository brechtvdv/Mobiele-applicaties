package be.ugent.groep3.bikebuddy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.ugent.groep3.bikebuddy.DataSingleton;
import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.logica.RestClient;
import be.ugent.groep3.bikebuddy.logica.Tools;

public class UserFragment extends Fragment {

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private TextView mName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("test","onCreate user fragment");
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        if(Tools.isInternetAvailable(this.getActivity().getApplicationContext()) ) {
            RestClient restClient = new RestClient(getResources().getString(R.string.rest_scoreboard));
            try {
                restClient.Execute(RestClient.RequestMethod.GET);
            } catch (Exception e) {
                Log.i("test","message is " + e.toString());
                e.printStackTrace();
            }
            String response = restClient.getResponse();
            Log.i("test","response is " + response);
        }

        mName = (TextView) view.findViewById(R.id.userFragmentName);
        if(DataSingleton.getData().getName() != null){
            mName.setText(DataSingleton.getData().getName());
        }else if(DataSingleton.getData().getEmail() != null){
            mName.setText(DataSingleton.getData().getEmail());
        }

        return view;
    }
}
