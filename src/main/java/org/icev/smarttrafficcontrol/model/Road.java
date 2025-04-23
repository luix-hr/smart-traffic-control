package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;

public class Road implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Intersection from;
    private Intersection to;
    private double weight; // pode representar tempo de viagem ou distância

    public Road(String name, Intersection from, Intersection to, double weight) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public Intersection getFrom() {
        return from;
    }

    public Intersection getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }
}
