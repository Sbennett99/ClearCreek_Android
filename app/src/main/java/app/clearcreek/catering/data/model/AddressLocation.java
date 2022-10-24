package app.clearcreek.catering.data.model;

import com.google.firebase.firestore.GeoPoint;

public class AddressLocation {
    private String name;
    private String address;
    private GeoPoint geopoint;

    public AddressLocation() {}

    public AddressLocation(String name, String address, GeoPoint geopoint) {
        this.name = name;
        this.address = address;
        this.geopoint = geopoint;
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

    public GeoPoint getGeoPoint() {
        return geopoint;
    }

    public void setGeoPoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }
}
