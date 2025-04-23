package com.example.mapsonapp;

public class Community {
    private String name;
    private String password;
    private Boolean isOpen;
    private String creatorUserName;

    public Community(String name, String password,String creatorUserName, Boolean isOpen) {
        this.name =name;
        this.password = password;
        this.isOpen = isOpen;
        this.creatorUserName = creatorUserName;
    }

    public Community() {}

    public String getCreatorUserName() { return creatorUserName; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }
}
