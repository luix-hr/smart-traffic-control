package org.icev.smarttrafficcontrol.datastructure.graph;

import org.icev.smarttrafficcontrol.model.TrafficLight;

import java.io.Serializable;

public class Vertex implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private double latitude;
    private double longitude;
    private TrafficLight trafficLight;

    public Vertex(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }

    public void setTrafficLight(TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
    }

    @Override
    public String toString() {
        return id + "(" + latitude + ", " + longitude + ")";
    }
}
