package org.icev.smarttrafficcontrol.datastructure;

public class GraphNode <T>{
    private T data;
    private LinkedList<Edge> adjacentNodes;

    public GraphNode(T data){
        this.data = data;
        this.adjacentNodes = new LinkedList<>();
    }

    public T getData() {
        return data;
    }

    public LinkedList<Edge> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void insertEdge(GraphNode destination, double weight){
        adjacentNodes.insertLast(new Edge<>(destination, weight));
    }
}
