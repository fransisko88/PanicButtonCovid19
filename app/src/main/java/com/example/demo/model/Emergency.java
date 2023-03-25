package com.example.demo.model;

public class Emergency {
    private String emergencyId;
    private String distance;
    private String userId;
    private String hospitalId;
    private Double latitudeUser;
    private Double longitudeUser;
    private String status;
    private String address;

    public Emergency (){}

    public Emergency(String emergencyId, String distance, String userId, String hospitalId, Double latitudeUser, Double longitudeUser, String status, String address) {
        this.emergencyId = emergencyId;
        this.distance = distance;
        this.userId = userId;
        this.hospitalId = hospitalId;
        this.latitudeUser = latitudeUser;
        this.longitudeUser = longitudeUser;
        this.status = status;
        this.address = address;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
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

    public Double getLatitudeUser() {
        return latitudeUser;
    }

    public void setLatitudeUser(Double latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public Double getLongitudeUser() {
        return longitudeUser;
    }

    public void setLongitudeUser(Double longitudeUser) {
        this.longitudeUser = longitudeUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
