package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;

public class TrafficLight implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum State { GREEN, YELLOW, RED }

    private String id;
    private State state;
    private transient Vertex vinculo;
    private int tempoAtual;
    private int tempoVerde;
    private int tempoAmarelo;
    private int tempoVermelho;

    public TrafficLight(String id) {
        this.id = id;
        this.state = State.RED;
    }

    public void updateCycle() {
        tempoAtual++;

        switch (state) {
            case GREEN:
                if (tempoAtual >= tempoVerde) {
                    state = State.YELLOW;
                    tempoAtual = 0;
                }
                break;
            case YELLOW:
                if (tempoAtual >= tempoAmarelo) {
                    state = State.RED;
                    tempoAtual = 0;
                }
                break;
            case RED:
                if (tempoAtual >= tempoVermelho) {
                    state = State.GREEN;
                    tempoAtual = 0;
                }
                break;
        }
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
