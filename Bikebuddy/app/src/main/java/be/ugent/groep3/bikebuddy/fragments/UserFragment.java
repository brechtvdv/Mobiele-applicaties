package be.ugent.groep3.bikebuddy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import be.ugent.groep3.bikebuddy.DataSingleton;
import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.activities.TabsActivity;
import be.ugent.groep3.bikebuddy.beans.User;
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
    private TextView mRank;
    private TextView mPoints;
    private TextView mFreeDays;
    private Button bExchange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("test","onCreate user fragment");
        View view;
        if(Tools.isInternetAvailable(this.getActivity().getApplicationContext())) {
            view = inflater.inflate(R.layout.fragment_user, container, false);
            mName = (TextView) view.findViewById(R.id.userFragmentName);
            mRank = (TextView) view.findViewById(R.id.UserFragmentRankNr);
            mPoints = (TextView) view.findViewById(R.id.UserFragmentPointsNr);
            mFreeDays = (TextView) view.findViewById(R.id.UserFragmentFreeDays);
            bExchange = (Button) view.findViewById(R.id.UserFragmentExchangeButton);

            if (DataSingleton.getData().getUser() != null) {
                User user = DataSingleton.getData().getUser();
                mName.setText((CharSequence) user.getName());
                mRank.setText(String.valueOf(user.getRanking()));
                String freeDays = String.valueOf(user.getBonuspoints() / 10);
                mPoints.setText(String.valueOf(user.getBonuspoints()));
                mFreeDays.setText("You've earned " + freeDays + " free days.");
                if (freeDays.equals("0")) {
                    bExchange.setClickable(false);
                    bExchange.setEnabled(false);
                }
            } else {
                Log.i("test", "created no user ");
            }
        }else{
            view = inflater.inflate(R.layout.fragment_scoreboard_offline, container, false);
        }
        return view;
    }
}
