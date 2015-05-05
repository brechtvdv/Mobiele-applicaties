package be.ugent.groep3.bikebuddy.beans;

import java.io.Serializable;

/**
 * Created by Jan on 3/05/2015.
 */
public class BikeStation implements Serializable{
    int id,number,bike_stands,available_bike_stands, available_bikes,bonuspoints;
    String name,address,status;
    double longitude,latitude;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLong() {
        return longitude;
    }

    public void setLong(double longitude) {
        this.longitude = longitude;
    }

    public double getLat() {
        return latitude;
    }

    public void setLat(double latitude) {
        this.latitude = latitude;
    }
}
