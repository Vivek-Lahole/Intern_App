package com.farmigo.app.Models;

public class News {

    private String heading;
    private String artical;
    private String date;
    private String src;
    private String img;


    public News() {
    }


    public News(String heading,String artical, String src, String date ,String img) {
        this.heading = heading;
        this.artical = artical;
        this.src = src;
        this.date = date;
        this.img = img;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getArtical() {
        return artical;
    }

    public void setArtical(String artical) {
        this.artical = artical;
    }
}
