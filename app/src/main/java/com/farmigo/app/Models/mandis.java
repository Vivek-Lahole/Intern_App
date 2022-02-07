package com.farmigo.app.Models;

public class mandis {
    private String name;
    private String add;
    private String lat;
    private String lon;
    private String state;
    private int placeDistance;


    public mandis() {
    }

    public mandis(String name, String add, String lat, String lon,String state,int placeDistance) {
        this.name = name;
        this.add = add;
        this.lat = lat;
        this.lon = lon;
        this.state = state;
        this.placeDistance = placeDistance;


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPlaceDistance() {
        return placeDistance;
    }

    public void setPlaceDistance(int placeDistance) {
        this.placeDistance = placeDistance;
    }



}
