package com.example.mapsonapp;


public class SharePoint {
    protected String name;
    protected String  creatorName;
    protected MyGeoPoint placement;
    protected String text;
    protected String photo;

    public SharePoint() {}


    public SharePoint(String name, String text, double x, double y, String photo, String creatorName) {
        this.name = name;
        this.text = text;
        this.photo = photo;
        this.placement = new MyGeoPoint(x,y);
        this.creatorName = creatorName;
    }


    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public MyGeoPoint getPlacement() {
        return placement;
    }

    public void setPlacement(MyGeoPoint placement) {
        this.placement = placement;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
