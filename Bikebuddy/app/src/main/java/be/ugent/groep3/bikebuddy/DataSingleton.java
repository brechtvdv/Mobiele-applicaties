package be.ugent.groep3.bikebuddy;

import android.util.Log;

import be.ugent.groep3.bikebuddy.beans.User;

/**
 * Created by Sam on 10/05/2015.
 */
public class DataSingleton {
    private static DataSingleton data;
    private String name;
    private String email;
    private String cookie;
    private User user;

    static{
        data = new DataSingleton();
    }

    private DataSingleton(){

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCookie(){
        return cookie;
    }

    public void setCookie( String cookie ){
        this.cookie = cookie;
    }

    public String getName() {
        Log.i("test","getName");
        return name;
    }

    public void setName(String name) {
        Log.i("test","setName");
        this.name = name;
    }

    public String getEmail() {
        Log.i("test","getEmail");
        return email;
    }

    public void setEmail(String email) {
        Log.i("test","setEmail");
        this.email = email;
    }

    public static DataSingleton getData(){
        Log.i("test","getData");
        return data;
    }

}
