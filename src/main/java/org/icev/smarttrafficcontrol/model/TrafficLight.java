package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;

import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;

public class TrafficLight implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum State {
        GREEN, YELLOW, RED
    }

    private String id;
    private State state;
    private transient Vertex vinculo;

    public TrafficLight(String id) {
        this.id = id;
        this.state = State.RED;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public void setVinculo(Vertex v) {
        this.vinculo = v;
    }

    public Vertex getVinculo() {
        return vinculo;
    }

    @Override
    public String toString() {
        return "Semáforo " + id + " - Estado: " + state;
    }
}
