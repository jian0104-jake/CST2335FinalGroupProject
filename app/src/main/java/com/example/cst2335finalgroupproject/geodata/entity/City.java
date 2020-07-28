package com.example.cst2335finalgroupproject.geodata.entity;

public class City {

    /**
     *  the database id for saved cities
     */
    private long id;

    /**
     * stores the name of the city
     */
    private String name;

    /**
     * stores the Country that the city belongs to
     */
    private String country;

    /**
     * stores the region that the city belongs to
     */
    private String region;

    /**
     * stores the currency in the local area
     */
    private String currency;

    /**
     * stores the latitude of the city
     */
    private double latitude;

    /**
     * stores the longitude of the city
     */
    private double longitude;

    /**
     * constructor to create city
     * @param name city name
     * @param region city region
     * @param country country the city belongs to
     * @param currency currency that the region uses
     * @param latitude latitude of the city
     * @param longitude longitude of the city
     */
    public City(String name,String region, String country,  String currency, double latitude, double longitude) {
        this.name = name;
        this.country = country;
        this.region = region;
        this.currency = currency;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * default constructor
     */
    public City() {
    }

    /**
     *getter for name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * setter for name
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return Country
     */
    public String getCountry() {
        return country;
    }

    /**
     * setter for country
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * getter for region
     * @return region
     */
    public String getRegion() {
        return region;
    }

    /**
     * setter for region
     * @param region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * getter for currency
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * setter for currency
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * getter for latitude
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * setter for latitude
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * getter for longitude
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * setter for longitude
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * getter for id
     * @return id
     */

    public long getId() {
        return id;
    }

    /**
     * setter for id
     * @param id database id of saved cities
     */
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name + ", " + region + ", " + country +", " + currency + "(" + latitude + ", "+ longitude + ")";
    }
}
