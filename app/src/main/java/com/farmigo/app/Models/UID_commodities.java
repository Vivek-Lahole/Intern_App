package com.farmigo.app.Models;

public class UID_commodities {
    private String name;
    private String apmc_name;
    private String category;
    private String img;
    private String color1;
    private String color2;
    private String type;
    private String state;



    public UID_commodities() {
    }

    public UID_commodities(String name, String apmc_name, String category, String img, String color1, String color2, String type ,String state) {
        this.name = name;
        this.apmc_name = apmc_name;
        this.category = category;
        this.color1 = color1;
        this.color2 = color2;
        this.img = img;
        this.type = type;
        this.state = state;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApmc_name() {
        return apmc_name;
    }

    public void setApmc_name(String apmc_name) {
        this.apmc_name = apmc_name;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }



}
