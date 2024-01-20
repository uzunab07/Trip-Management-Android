package com.example.hw10;

import java.io.Serializable;

public class Trip implements Serializable {
    String name,debut,end,status;
    double distance;
    MyLocation start,finish;

    public Trip(String name, String debut, String end, String status, double distance, MyLocation start, MyLocation finish) {
        this.name = name;
        this.debut = debut;
        this.end = end;
        this.status = status;
        this.distance = distance;
        this.start = start;
        this.finish = finish;
    }

    public Trip(String name, String debut, MyLocation start, String status) {
        this.name = name;
        this.debut = debut;
        this.start = start;
        this.status = status;

    }

    public MyLocation getStart() {
        return start;
    }

    public void setStart(MyLocation start) {
        this.start = start;
    }

    public MyLocation getFinish() {
        return finish;
    }

    public void setFinish(MyLocation finish) {
        this.finish = finish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "name='" + name + '\'' +
                ", distance=" + distance +
                ", start=" + start +
                ", finish=" + finish +
                ", status=" + status +
                '}';
    }
}
