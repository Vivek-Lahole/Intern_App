package com.farmigo.app.Models;

public class CropViewCommodity implements Comparable<CropViewCommodity>  {

    private String name;
    private String category;
    private String state;
    private String price;
    private String date;
    private int distance;
    private String apmc_name;
    private String rise;
    private String percentage;
    private String img;
    private String color1;
    private String color2;
    private String type;
    private String days;
    private boolean bookmark;

    public CropViewCommodity() {
    }

    public CropViewCommodity(String state, String apmc_name, String name, String category, String price, String date, String rise, String img, String color1, String color2, String type, String percentage, String days, boolean bookmark) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.color1 = color1;
        this.color2 = color2;
        this.date = date;
        this.rise = rise;
        this.img = img;
        this.type = type;
        this.percentage = percentage;
        this.days= days;
        this.bookmark= bookmark;
        this.apmc_name=apmc_name;
        this.state=state;

    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getApmc_name() {
        return apmc_name;
    }

    public void setApmc_name(String apmc_name) {
        this.apmc_name = apmc_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRise() {
        return rise;
    }

    public void setRise(String rise) {
        this.rise = rise;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }


    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }


    public boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }


    @Override
    public int compareTo(CropViewCommodity cropViewCommodity) {

        if(this.getDistance()> cropViewCommodity.getDistance())
        {
            return 1;
        }
        else if(this.getDistance()== cropViewCommodity.getDistance())
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
}