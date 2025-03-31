package org.icev.smarttrafficcontrol;

import org.icev.smarttrafficcontrol.datastructure.Deck;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Queue;

public class Main {
    public static void main(String[] args) {

        Deck fila = new Deck();

        fila.addFront(10);
        fila.addFront(20);
        fila.addFront(30);
        fila.addFront(40);
        fila.addFront(50);

        fila.print();

        fila.rotation(-2);

        fila.print();



    }
}