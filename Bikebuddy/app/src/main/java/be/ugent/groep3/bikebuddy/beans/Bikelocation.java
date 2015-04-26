package be.ugent.groep3.bikebuddy.beans;

/**
 * Created by Jan on 25/04/2015.
 */
public class Bikelocation {

    private String locationName, locationAddress;
    private int numberOfPoints, distance;

    public Bikelocation(String locationName, String locationAddress, int numberOfPoints, int distance){
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.numberOfPoints = numberOfPoints;
        this.distance = distance;
    }

    public Bikelocation(){}

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
