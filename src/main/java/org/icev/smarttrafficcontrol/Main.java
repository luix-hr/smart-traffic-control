package org.icev.smarttrafficcontrol;

import org.icev.smarttrafficcontrol.datastructure.Deck;
import org.icev.smarttrafficcontrol.datastructure.Graph;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Queue;

public class Main {
    public static void main(String[] args) {

        Graph<String> cityMap = new Graph<>();

        cityMap.insertVertex("A");
        cityMap.insertVertex("B");
        cityMap.insertVertex("C");
        cityMap.insertVertex("D");

        cityMap.insertEdge("A", "B", 2.5);
        cityMap.insertEdge("A", "C", 1.0);
        cityMap.insertEdge("B", "D", 3.0);
        cityMap.insertEdge("C", "D", 4.5);

        cityMap.print();



    }
}