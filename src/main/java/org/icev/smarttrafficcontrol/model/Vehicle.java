package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String plate;
    private Road currentRoad;

    public Vehicle(String plate, Road currentRoad) {
        this.plate = plate;
        this.currentRoad = currentRoad;
    }

    public String getPlate() {
        return plate;
    }

    public Road getCurrentRoad() {
        return currentRoad;
    }

    public void setCurrentRoad(Road currentRoad) {
        this.currentRoad = currentRoad;
    }
}

