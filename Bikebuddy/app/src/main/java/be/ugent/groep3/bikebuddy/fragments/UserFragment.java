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
import be.ugent.groep3.bikebuddy.activities.TabsActivity;
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

        mName = (TextView) view.findViewById(R.id.userFragmentName);
        if(DataSingleton.getData().getUser() != null){
            Log.i("test", "created user " + DataSingleton.getData().getUser());
            mName.setText((CharSequence) DataSingleton.getData().getUser().getName());
        }else{
            Log.i("test", "created no user ");
        }
        return view;
    }
}
