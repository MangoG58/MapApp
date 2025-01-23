package com.example.mapsonapp;

import android.widget.ImageView;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class SharePoint {
    protected String Name;
    protected MyGeoPoint placement;
    protected String Text;
    protected ArrayList<ImageView > Photos;

    public SharePoint() {}


    public SharePoint(String name, String text, double x, double y) {
        this.Name = name;
        this.Text = text;
        this.placement = new MyGeoPoint(x,y);
        this.Photos = new ArrayList<ImageView>();
    }

    public void addPhoto(ImageView image)
    {
        Photos.add(image);
    }

    public boolean IsPoint() {return true;}


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<ImageView> getPhotos() {
        return Photos;
    }

    public MyGeoPoint getPlacement() {
        return placement;
    }

    public void setPlacement(MyGeoPoint placement) {
        this.placement = placement;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
