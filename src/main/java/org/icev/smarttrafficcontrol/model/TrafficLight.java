package org.icev.smarttrafficcontrol.model;

import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;

import java.io.Serializable;

public class TrafficLight implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum State {
        GREEN, YELLOW, RED
    }

    private String id;
    private State state;
    private int duracaoVerde;
    private int duracaoAmarelo;
    private int duracaoVermelho;
    private int timer;
    private transient Vertex vinculo;

    public TrafficLight(String id) {
        this.id = id;
        this.state = State.RED;
        this.duracaoVerde = 5;
        this.duracaoAmarelo = 2;
        this.duracaoVermelho = 5;
        this.timer = 0;
    }

    public void updateCycle(int verde, int amarelo, int vermelho) {
        this.duracaoVerde = verde;
        this.duracaoAmarelo = amarelo;
        this.duracaoVermelho = vermelho;

        timer++;

        switch (state) {
            case GREEN:
                if (timer >= duracaoVerde) {
                    state = State.YELLOW;
                    timer = 0;
                }
                break;
            case YELLOW:
                if (timer >= duracaoAmarelo) {
                    state = State.RED;
                    timer = 0;
                }
                break;
            case RED:
                if (timer >= duracaoVermelho) {
                    state = State.GREEN;
                    timer = 0;
                }
                break;
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        this.timer = 0;
    }

    public String getId() {
        return id;
    }

    public int getTimer() {
        return timer;
    }

    public void setVinculo(Vertex v) {
        this.vinculo = v;
    }

    public Vertex getVinculo() {
        return vinculo;
    }

    @Override
    public String toString() {
        String duracaoAtual = switch (state) {
            case GREEN -> duracaoVerde + "";
            case YELLOW -> duracaoAmarelo + "";
            case RED -> duracaoVermelho + "";
        };

        return "Semaforo " + id + " - Estado: " + state + " (" + timer + "/" + duracaoAtual + ")";
    }
}
