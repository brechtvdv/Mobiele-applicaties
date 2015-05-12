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
        Log.i("test", "got view"+view);
        mName = (TextView) view.findViewById(R.id.userFragmentName);
        Log.i("test","got Namefield");
        if(DataSingleton.getData().getName() != null){
            mName.setText(DataSingleton.getData().getName());
            Log.i("test", "set name");
        }else if(DataSingleton.getData().getEmail() != null){
            mName.setText("Testname");
            mName.setText(DataSingleton.getData().getEmail());

            Log.i("test", "set email" + DataSingleton.getData().getEmail());
            Log.i("test", "test is " + mName.getText());
        }

        return inflater.inflate(R.layout.fragment_user, container, false);
    }
}
