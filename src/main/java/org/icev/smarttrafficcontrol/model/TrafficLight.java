package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;

public class TrafficLight implements Serializable {
    private static final long serialVersionUID = 1L;

    public State getEstado() {
        return state;
    }

    public enum State {
        GREEN, YELLOW, RED
    }

    private String id;
    private State state;
    private int duration;
    private int timer;

    public TrafficLight(String id) {
        this.id = id;
        this.state = State.RED;
        this.duration = 5;
        this.timer = 0;
    }

    public void update() {
        timer++;
        if (timer >= duration) {
            switch (state) {
                case RED:
                    state = State.GREEN;
                    break;
                case GREEN:
                    state = State.YELLOW;
                    break;
                case YELLOW:
                    state = State.RED;
                    break;
            }
            timer = 0;
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        this.timer = 0;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public int getTimer() {
        return timer;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Semáforo " + id + " - Estado: " + state + " (" + timer + "/" + duration + ")";
    }
}