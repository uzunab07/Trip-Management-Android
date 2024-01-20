package com.example.hw10;

public class MyLocation {
    public double Lat,Long;

    public MyLocation(double lat, double aLong) {
        Lat = lat;
        Long = aLong;
    }
    public MyLocation(){
    }

    @Override
    public String toString() {
        return "MyLocation{" +
                "Lat=" + Lat +
                ", Long=" + Long +
                '}';
    }
}
