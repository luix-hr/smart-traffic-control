package org.icev.smarttrafficcontrol.model;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;

import java.io.Serializable;

public class Intersection implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private LinkedList<Road> connectedRoads;

    public Intersection(String id) {
        this.id = id;
        this.connectedRoads = new LinkedList<>();
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
