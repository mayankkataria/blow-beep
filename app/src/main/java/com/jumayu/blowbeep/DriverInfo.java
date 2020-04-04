package com.jumayu.blowbeep;

import android.location.Location;

import java.util.ArrayList;

class DriverId {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

class DriverInfo extends DriverId{
    String phNo;
    Location location;
    ArrayList<String> stops;

    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
