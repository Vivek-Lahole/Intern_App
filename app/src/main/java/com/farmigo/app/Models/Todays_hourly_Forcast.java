package com.farmigo.app.Models;



public class Todays_hourly_Forcast {

    private String time;
    private String temp;
    private String icon;

    public Todays_hourly_Forcast() {
    }


    public Todays_hourly_Forcast(String time, String temp, String icon) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
