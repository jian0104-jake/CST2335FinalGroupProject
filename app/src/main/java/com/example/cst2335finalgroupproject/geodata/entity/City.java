package com.example.cst2335finalgroupproject.geodata.entity;

public class City {
    private String name;
    private String Country;
    private String Region;
    private String currency;
    private double latitude;
    private double longitude;

    public City(String name,String region, String country,  String currency, double latitude, double longitude) {
        this.name = name;
        Country = country;
        Region = region;
        this.currency = currency;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public City() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
