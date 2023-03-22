package com.example.demo.model;

public class Emergency {
    private String emegencyId;
    private String distance;
    private String userId;
    private String hospitalId;
    private String latitudeUser;
    private String longitudeUser;

    public Emergency (){}

    public Emergency(String emegencyId, String distance, String userId, String hospitalId, String latitudeUser, String longitudeUser) {
        this.emegencyId = emegencyId;
        this.distance = distance;
        this.userId = userId;
        this.hospitalId = hospitalId;
        this.latitudeUser = latitudeUser;
        this.longitudeUser = longitudeUser;
    }

    public String getEmegencyId() {
        return emegencyId;
    }

    public void setEmegencyId(String emegencyId) {
        this.emegencyId = emegencyId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getLatitudeUser() {
        return latitudeUser;
    }

    public void setLatitudeUser(String latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public String getLongitudeUser() {
        return longitudeUser;
    }

    public void setLongitudeUser(String longitudeUser) {
        this.longitudeUser = longitudeUser;
    }
}
