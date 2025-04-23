package com.example.mapsonapp;

import android.widget.ImageView;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class SharePoint {
    protected String Name;
    protected String  CreatorName;
    protected MyGeoPoint placement;
    protected String Text;
    protected String Photo;

    public SharePoint() {}


    public SharePoint(String name, String text, double x, double y, String photo, String creatorName) {
        this.Name = name;
        this.Text = text;
        this.Photo = photo;
        this.placement = new MyGeoPoint(x,y);
        this.CreatorName = creatorName;
    }


    public String getCreatorName() {
        return CreatorName;
    }

    public void setCreatorName(String creatorName) {
        CreatorName = creatorName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }
}
