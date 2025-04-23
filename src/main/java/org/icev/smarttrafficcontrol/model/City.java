package org.icev.smarttrafficcontrol.model;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;

import java.io.Serializable;

public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private LinkedList<Intersection> intersections;
    private LinkedList<Road> roads;

    public City(String name) {
        this.name = name;
        this.intersections = new LinkedList<>();
        this.roads = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public LinkedList<Intersection> getIntersections() {
        return intersections;
    }

    public LinkedList<Road> getRoads() {
        return roads;
    }

    public void addIntersection(Intersection intersection) {
        intersections.insert(intersection);
    }

    public void addRoad(Road road) {
        roads.insert(road);
    }
}
