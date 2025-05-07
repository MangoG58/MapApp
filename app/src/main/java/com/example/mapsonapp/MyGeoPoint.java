package com.example.mapsonapp;

import org.osmdroid.util.GeoPoint;
public class MyGeoPoint extends GeoPoint {
    public MyGeoPoint(double aLatitude, double aLongitude) {
        super(aLatitude, aLongitude);
    }
    public MyGeoPoint() {
        super(35,35);
    }
}
