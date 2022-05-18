package com.example.iadweatherapi;

public class City
{
    private String name_;
    private Double latitude_;
    private Double longitude_;

    public City(String name_, Double latitude_, Double longitude_)
    {
        this.name_ = name_;
        this.latitude_ = latitude_;
        this.longitude_ = longitude_;
    }

    public String getName_()
    {
        return name_;
    }

    public void setName_(String name_)
    {
        this.name_ = name_;
    }

    public Double getLatitude_()
    {
        return latitude_;
    }

    public void setLatitude_(Double latitude_)
    {
        this.latitude_ = latitude_;
    }

    public Double getLongitude_()
    {
        return longitude_;
    }

    public void setLongitude_(Double longitude_)
    {
        this.longitude_ = longitude_;
    }
}
