package org.icev.smarttrafficcontrol.datastructure;

public class Edge<T> {
    private GraphNode destination;
    private double weight;

    public Edge(GraphNode destination, double weight){
        this.destination = destination;
        this.weight = weight;
    }

    public GraphNode getDestination(){
        return destination;
    }

    public double getWeight() {
        return weight;
    }
}
