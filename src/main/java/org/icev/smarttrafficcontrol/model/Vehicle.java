package org.icev.smarttrafficcontrol.model;

import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private Queue<Vertex> rota;

    public Vehicle(String id, Queue<Vertex> rota) {
        this.id = id;
        this.rota = rota;
    }

    public String getId() {
        return id;
    }

    public Queue<Vertex> getRota() {
        return rota;
    }

    public Vertex getProximoDestino() {
        return rota.isEmpty() ? null : rota.peek();
    }

    public void mover() {
        if (!rota.isEmpty()) {
            rota.dequeue(); // anda para o próximo vértice
        }
    }
}

