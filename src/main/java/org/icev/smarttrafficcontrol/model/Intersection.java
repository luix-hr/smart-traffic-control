package org.icev.smarttrafficcontrol.model;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;

import java.io.Serializable;

public class Intersection implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private double latitude;
    private double longitude;
    private LinkedList<Road> connectedRoads;

    public Intersection(String id) {
        this.id = id;
        this.connectedRoads = new LinkedList<>();
    }

    public void setId(String id) {
        this.id = id;
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

    public void setConnectedRoads(LinkedList<Road> connectedRoads) {
        this.connectedRoads = connectedRoads;
    }

    public String getId() {
        return id;
    }

    public LinkedList<Road> getConnectedRoads() {
        return connectedRoads;
    }

    public void connectRoad(Road road) {
        connectedRoads.insert(road);
    }
}
