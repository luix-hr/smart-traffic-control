package org.icev.smarttrafficcontrol.datastructure.graph;

import java.io.Serializable;

public class Edge<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private Vertex source;
    private Vertex target;
    private double length;
    private double maxspeed;

    public Edge(String id, Vertex source, Vertex target, double length, double maxspeed) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.length = length;
        this.maxspeed = maxspeed;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getTarget() {
        return target;
    }

    public double getTravelTime() {
        if (maxspeed > 0) {
            return length / (maxspeed / 3.6);
        }
        return length;
    }


    public double getLength() {
        return length;
    }
}
