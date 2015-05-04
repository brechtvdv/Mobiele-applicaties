package be.ugent.groep3.bikebuddy.beans;

/**
 * Created by brechtvdv on 04/05/15.
 */
public class Station {

    private int id;
    private int number;
    private String name;
    private String address;
    private float longitude;
    private float latitude;
    private String status;
    private int bike_stands;
    private int available_bike_stands;
    private int available_bikes;
    private int bonuspoints;

    public Station(){};

    public Station(int number, String name, String address, float longitude, float latitude, String status, int bike_stands, int available_bike_stands, int available_bikes, int bonuspoints) {
        super();
        this.number = number;
        this.name = name;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.bike_stands = bike_stands;
        this.available_bike_stands = available_bike_stands;
        this.available_bikes = available_bikes;
        this.bonuspoints = bonuspoints;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBike_stands() {
        return bike_stands;
    }

    public void setBike_stands(int bike_stands) {
        this.bike_stands = bike_stands;
    }

    public int getAvailable_bike_stands() {
        return available_bike_stands;
    }

    public void setAvailable_bike_stands(int available_bike_stands) {
        this.available_bike_stands = available_bike_stands;
    }

    public int getAvailable_bikes() {
        return available_bikes;
    }

    public void setAvailable_bikes(int available_bikes) {
        this.available_bikes = available_bikes;
    }

    public int getBonuspoints() {
        return bonuspoints;
    }

    public void setBonuspoints(int bonuspoints) {
        this.bonuspoints = bonuspoints;
    }

    @Override
    public String toString(){
        return "Station [id=" + id + ", number=" + number + ", bonuspoints=" + bonuspoints
                + "]";
    }


}
