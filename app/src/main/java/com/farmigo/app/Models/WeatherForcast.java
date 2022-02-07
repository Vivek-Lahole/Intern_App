package com.farmigo.app.Models;



public class WeatherForcast {

    private String date;
    private String day;
    private String temp;
    private String icon;

    public WeatherForcast() {
    }


    public WeatherForcast(String date, String day, String temp, String icon) {
        this.date = date;
        this.day = day;
        this.temp = temp;
        this.icon = icon;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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
