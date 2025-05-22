package org.icev.smarttrafficcontrol.model;

import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private int tempoParado = 0;
    private int tempoTotal = 0;
    private Queue<Vertex> rota;

    public Vehicle(String id, Queue<Vertex> rota) {
        this.id = id;
        if (rota == null) {
            throw new IllegalArgumentException("Rota não pode ser null");
        }
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

    public void incrementarEspera() {
        tempoParado++;
        tempoTotal++;
    }

    public void incrementarTempo() {
        tempoTotal++;
    }

    public int getTempoParado() {
        return tempoParado;
    }

    public int getTempoTotal() {
        return tempoTotal;
    }

    public void mover() {
        if (!rota.isEmpty()) {
            rota.dequeue();
        }
        incrementarTempo();
    }
}
