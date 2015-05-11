package be.ugent.groep3.bikebuddy.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import be.ugent.groep3.bikebuddy.beans.BikeStation;

/**
 * Created by brechtvdv on 04/05/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    ////////
    // DB //
    ////////

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "StationDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create station table
        String CREATE_STATION_TABLE = "CREATE TABLE stations ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "number INTEGER, "+
                "name TEXT, "+
                "address TEXT, "+
                "longitude FLOAT, "+
                "latitude FLOAT, "+
                "status TEXT, "+
                "bike_stands INTEGER, "+
                "available_bike_stands INTEGER, "+
                "available_bikes INTEGER, "+
                "bonuspoints INTEGER, "+
                "distance)";

        // create books table
        db.execSQL(CREATE_STATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS stations");

        // create fresh books table
        this.onCreate(db);
    }

    ///////////
    // TABLE //
    ///////////

    // Stations table name
    private static final String TABLE_STATIONS = "stations";

    // Stations Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_STATUS = "status";
    private static final String KEY_BIKE_STANDS = "bike_stands";
    private static final String KEY_AVAILABLE_BIKE_STANDS = "available_bike_stands";
    private static final String KEY_AVAILABLE_BIKES = "available_bikes";
    private static final String KEY_BONUSPOINTS = "bonuspoints";
    private static final String KEY_DISTANCE = "distance";

    private static final String[] COLUMNS = {KEY_ID,KEY_NUMBER,KEY_NAME,KEY_ADDRESS,KEY_LONGITUDE,KEY_LATITUDE,KEY_STATUS,KEY_BIKE_STANDS,KEY_AVAILABLE_BIKE_STANDS,KEY_AVAILABLE_BIKES,KEY_BONUSPOINTS,KEY_DISTANCE};

    public void addBikeStation(BikeStation station){
        //for logging
        Log.d("addStation", station.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER, station.getNumber()); // get number
        values.put(KEY_NAME, station.getName()); // get name
        values.put(KEY_ADDRESS, station.getAddress()); // get address
        values.put(KEY_LONGITUDE, station.getLongitude()); // get longitude
        values.put(KEY_LATITUDE, station.getLatitude()); // get latitude
        values.put(KEY_STATUS, station.getStatus()); // get status
        values.put(KEY_BIKE_STANDS, station.getBike_stands()); // get number of bike stands
        values.put(KEY_AVAILABLE_BIKE_STANDS, station.getAvailable_bike_stands()); // get number of available bike stands
        values.put(KEY_AVAILABLE_BIKES, station.getAvailable_bikes()); // get number of available bikes
        values.put(KEY_BONUSPOINTS, station.getBonuspoints()); // get number of bonuspoints
        values.put(KEY_DISTANCE, 0); // holds distance to destination

        // 3. insert
        db.insert(TABLE_STATIONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public BikeStation getBikeStation(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        BikeStation station = new BikeStation();

        // 2. build query
        try {
            cursor =
                    db.query(TABLE_STATIONS, // a. table
                            COLUMNS, // b. column names
                            " id = ?", // c. selections
                            new String[]{String.valueOf(id)}, // d. selections args
                            null, // e. group by
                            null, // f. having
                            null, // g. order by
                            null); // h. limit

            // 3. if we got results get the first one
            if (cursor != null)
                cursor.moveToFirst();

            // 4. build station object
            //BikeStation station = new BikeStation();
            station.setId(cursor.getInt(0));
            station.setNumber(cursor.getInt(1));
            station.setName(cursor.getString(2));
            station.setAddress(cursor.getString(3));
            station.setLongitude(cursor.getFloat(4));
            station.setLatitude(cursor.getFloat(5));
            station.setStatus(cursor.getString(6));
            station.setBike_stands(cursor.getInt(7));
            station.setAvailable_bike_stands(cursor.getInt(8));
            station.setAvailable_bikes(cursor.getInt(9));
            station.setBonuspoints(cursor.getInt(10));
            station.setDistance(cursor.getInt(11));
        } finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        //log
        Log.d("getStation(" + id + ")", station.toString());

        // 5. return station
        return station;
    }

    public List<BikeStation> getAllBikeStations() {
        List<BikeStation> stations = new LinkedList<BikeStation>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_STATIONS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            // 3. go over each row, build station and add it to list
            BikeStation station = null;
            if (cursor.moveToFirst()) {
                do {
                    station = new BikeStation();
                    station.setId(Integer.parseInt(cursor.getString(0)));
                    station.setNumber(Integer.parseInt(cursor.getString(1)));
                    station.setName(cursor.getString(2).toString());
                    station.setAddress(cursor.getString(3).toString());
                    station.setLongitude(Float.parseFloat(cursor.getString(4)));
                    station.setLatitude(Float.parseFloat(cursor.getString(5)));
                    station.setStatus(cursor.getString(6).toString());
                    station.setBike_stands(Integer.parseInt(cursor.getString(7)));
                    station.setAvailable_bike_stands(Integer.parseInt(cursor.getString(8)));
                    station.setAvailable_bikes(Integer.parseInt(cursor.getString(9)));
                    station.setBonuspoints(Integer.parseInt(cursor.getString(10)));
                    station.setDistance(Integer.parseInt(cursor.getString(11)));


                    // Add book to books
                    stations.add(station);
                } while (cursor.moveToNext());
            }
        } finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        Log.d("getAllStations()", stations.toString());

        // return books
        return stations;
    }

    public int updateBikeStation(BikeStation station) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER, station.getNumber()); // get number
        values.put(KEY_NAME, station.getName()); // get name
        values.put(KEY_ADDRESS, station.getAddress()); // get address
        values.put(KEY_LONGITUDE, station.getLongitude()); // get longitude
        values.put(KEY_LATITUDE, station.getLatitude()); // get latitude
        values.put(KEY_STATUS, station.getStatus()); // get status
        values.put(KEY_BIKE_STANDS, station.getBike_stands()); // get number of bike stands
        values.put(KEY_AVAILABLE_BIKE_STANDS, station.getAvailable_bike_stands()); // get number of available bike stands
        values.put(KEY_AVAILABLE_BIKES, station.getAvailable_bikes()); // get number of available bikes
        values.put(KEY_BONUSPOINTS, station.getBonuspoints()); // get number of bonuspoints
        values.put(KEY_DISTANCE, station.getDistance()); // get number of bonuspoints

        // 3. updating row
        int i = db.update(TABLE_STATIONS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(station.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteStation(BikeStation station) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_STATIONS, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(station.getId()) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteStation", station.toString());

    }

    public void deleteAllStations(){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_STATIONS, //table name
                null,  // selections
                null); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteAllStations", "");
    }
}
