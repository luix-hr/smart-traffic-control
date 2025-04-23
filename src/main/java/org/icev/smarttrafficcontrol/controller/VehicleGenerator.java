package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.model.Road;
import org.icev.smarttrafficcontrol.model.Vehicle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

class VehicleGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    private LinkedList<Road> roads;
    private transient Random random = new Random();

    public VehicleGenerator(LinkedList<Road> roads) {
        this.roads = roads;
    }

    public void generate() {
        if (roads.isEmpty()) return;

        int roadIndex = random.nextInt(roads.getSize());
        Node<Road> current = roads.getHead();
        for (int i = 0; i < roadIndex; i++) {
            if (current.getNext() != null) {
                current = current.getNext();
            }
        }

        Road selectedRoad = current.getData();
        Vehicle vehicle = new Vehicle("V" + random.nextInt(1000), selectedRoad);
        System.out.println("Vehicle " + vehicle.getPlate() + " added to road " + selectedRoad.getName());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.random = new Random(); // reinicializa após desserialização
    }
}
