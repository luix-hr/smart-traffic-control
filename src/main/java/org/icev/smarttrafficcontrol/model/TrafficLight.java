package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;

public class TrafficLight implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private int greenTime;
    private int redTime;
    private int yellowTime;

    public TrafficLight(String id, int greenTime, int yellowTime, int redTime) {
        this.id = id;
        this.greenTime = greenTime;
        this.yellowTime = yellowTime;
        this.redTime = redTime;
    }

    public String getId() {
        return id;
    }

    public int getGreenTime() {
        return greenTime;
    }

    public int getRedTime() {
        return redTime;
    }

    public int getYellowTime() {
        return yellowTime;
    }
}
