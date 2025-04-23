package org.icev.smarttrafficcontrol.datastructure;

public class DistanceEntry<T> {
    public GraphNode<T> node;
    public double distance;
    public boolean visited;

    public DistanceEntry(GraphNode<T> node, double distance) {
        this.node = node;
        this.distance = distance;
        this.visited = false;
    }
}
