package app.clearcreek.catering.data.model;

import com.google.firebase.firestore.GeoPoint;

/*
 * Custom Java Class
 *
 * Allows creation of java objects for
 * each restaurant location to be utilized/managed
 * within the application
 *
 */
public class AddressLocation {
    private String name;
    private String address;
    private GeoPoint geopoint;

    // Basic  empty Class Constructor
    public AddressLocation() {}

    // Basic Class Constructor for creation with location information
    public AddressLocation(String name, String address, GeoPoint geopoint) {
        this.name = name;
        this.address = address;
        this.geopoint = geopoint;
    }

    // Getters and Setters for the Class

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

    public GeoPoint getGeoPoint() {
        return geopoint;
    }

    public void setGeoPoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }
}
