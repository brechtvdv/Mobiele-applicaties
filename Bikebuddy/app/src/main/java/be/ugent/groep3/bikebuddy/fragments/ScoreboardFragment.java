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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.ugent.groep3.bikebuddy.R;
import be.ugent.groep3.bikebuddy.activities.TabsActivity;
import be.ugent.groep3.bikebuddy.beans.BikeStation;
import be.ugent.groep3.bikebuddy.beans.User;
import be.ugent.groep3.bikebuddy.logica.RestClient;
import be.ugent.groep3.bikebuddy.logica.Tools;
import be.ugent.groep3.bikebuddy.sqlite.MySQLiteHelper;

public class ScoreboardFragment extends Fragment {

    private List<User> users;
    private ListView listView;


    public ScoreboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        // online
        if(Tools.isInternetAvailable(this.getActivity().getApplicationContext()) ) {
            view = inflater.inflate(R.layout.fragment_scoreboard_online, container, false);
            if(Tools.isInternetAvailable(this.getActivity().getApplicationContext())) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        RestClient restClient = new RestClient(getResources().getString(R.string.rest_scoreboard));
                        try {
                            restClient.Execute(RestClient.RequestMethod.GET);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String response = restClient.getResponse();
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            listView = (ListView)  view.findViewById(R.id.scoreboard_list);
            sortByBonuspoints(users);
            setRank(users);
            updateGUIList();
        }
        // offline
        else {
            view = inflater.inflate(R.layout.fragment_scoreboard_offline, container, false);
        }

        return view;
    }

    private void sortByBonuspoints(List<User> users){
        //Sorting using Anonymous inner class type
        Collections.sort(users, new Comparator() {
            @Override
            public int compare(Object s1, Object s2) {
                int bonus1 = ((User) s1).getBonuspoints();
                int bonus2 = ((User) s2).getBonuspoints();

                return bonus2 - bonus1;
            }
        });
    }

    private void setRank(List<User> users){
        int i = 1;
        for(User user : users)
            user.setRanking(i++);
    }

    private void updateGUIList(){
        listView.setAdapter(new CustomListAdapter(getActivity(), users, this));
    }

    private class CustomListAdapter extends BaseAdapter {

        private Activity activity;
        private ScoreboardFragment fragment;
        private LayoutInflater inflater;
        private List<User> users;

        public CustomListAdapter(Activity activity, List<User> users, ScoreboardFragment fragment){
            this.activity = activity;
            this.users = users;
            this.fragment = fragment;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
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
                convertView = inflater.inflate(R.layout.user_list_row, null);

            // User in component steken:
            User user = users.get(position);
            TextView ranking = (TextView) convertView.findViewById(R.id.ranking);
            ranking.setText(position);
            TextView user_name = (TextView) convertView.findViewById(R.id.user_name);
            user_name.setText(user.getName());
            TextView bonuspoints = (TextView) convertView.findViewById(R.id.user_bonuspoints);
            bonuspoints.setText(user.getBonuspoints());

            return convertView;
        }
    }
}
