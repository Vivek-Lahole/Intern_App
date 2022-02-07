package com.farmigo.app.Models;

public class APMClist_Distance {

    private String apmc_name;
    private String lat;
    private String lon;

    private int distance;

    public APMClist_Distance(String apmc_name, String lat, String lon) {
        this.apmc_name = apmc_name;
        this.lat = lat;
        this.lon = lon;
    }


    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public APMClist_Distance() {
    }

    public String getApmc_name() {
        return apmc_name;
    }

    public void setApmc_name(String apmc_name) {
        this.apmc_name = apmc_name;
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






}
