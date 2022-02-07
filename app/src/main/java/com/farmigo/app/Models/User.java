package com.farmigo.app.Models;

import androidx.annotation.NonNull;

import java.util.Objects;

public class User {
        public String imgurl;
        public String name;
        public String phone;
        public String email;
        public String usertype;
        public String location;
        public String timestamp;
        public String zipcode;
        public String lat;
        public String lon;

        public User() {
        }


        public User(String imgurl, String name, String phone, String email, String usertype,String timestamp,String zipcode, String lat,String lon) {
            this.imgurl = imgurl;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.usertype = usertype;
            this.timestamp = timestamp;
            this.zipcode = zipcode;
            this.lat = lat;
            this.lon = lon;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getLocationl() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getTimestamp() {
        return timestamp;
    	}

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getUsertype() {
            return usertype;
        }

        public void setUsertype(String usertype) {
            this.usertype = usertype;
        }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
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

