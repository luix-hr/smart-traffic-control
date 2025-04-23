package org.icev.smarttrafficcontrol.datastructure;

public class PreviousEntry<T> {
    public GraphNode<T> node;
    public GraphNode<T> previous;

    public PreviousEntry(GraphNode<T> node, GraphNode<T> previous) {
        this.node = node;
        this.previous = previous;
    }
}
