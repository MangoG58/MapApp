package com.example.mapsonapp;



import java.util.ArrayList;
public class Trail extends SharePoint {
    protected ArrayList<MyGeoPoint> trail;

    public Trail() {}
    public Trail(String name, String text, double x1, double y1, double x2, double y2, String photo, String creatorName) {
        super(name , text, x1, y1, photo, creatorName);
        trail = new ArrayList<>();
        trail.add((new MyGeoPoint(x1,y1)));
        trail.add((new MyGeoPoint(x2,y2)));
    }

    public ArrayList<MyGeoPoint> getTrail() {
        return trail;
    }

    public void setTrail(ArrayList<MyGeoPoint> trail) {
        this.trail = trail;
    }
    public void addCord(double latitude, double longitude){
        trail.add((new MyGeoPoint(latitude,longitude)));
    }
}
