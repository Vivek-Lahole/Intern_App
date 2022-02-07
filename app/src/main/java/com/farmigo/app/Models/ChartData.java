package com.farmigo.app.Models;



public class ChartData  {

    private String date;
    private String price;
    private String rise;

    public ChartData() {
    }


    public ChartData(String date, String price,String rise) {
        this.date = date;
        this.price = price;
        this.rise = rise;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRise() {
        return rise;
    }

    public void setRise(String rise) {
        this.rise = rise;
    }
}
