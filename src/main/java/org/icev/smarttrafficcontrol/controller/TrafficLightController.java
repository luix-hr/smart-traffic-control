package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.model.Intersection;

import java.io.Serializable;

class TrafficLightController implements Serializable {
    private static final long serialVersionUID = 1L;

    private LinkedList<Intersection> intersections;
    private int timeCounter = 0;

    public TrafficLightController(LinkedList<Intersection> intersections) {
        this.intersections = intersections;
    }

    public void update() {
        timeCounter++;
        Node<Intersection> current = intersections.getHead();
        while (current != null) {
            System.out.println("Updated traffic light at intersection " + current.getData().getId());
            current = current.getNext();
        }
    }
}
