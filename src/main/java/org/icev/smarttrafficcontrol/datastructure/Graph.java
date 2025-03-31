package org.icev.smarttrafficcontrol.datastructure;

public class Graph<T> {
    private LinkedList<GraphNode> nodes;

    public Graph() {
        this.nodes = new LinkedList<>();
    }

    public GraphNode getNode(T data){
        Node<GraphNode> actual =  nodes.getHead();

        while(actual != null){
            if (actual.getData().getData().equals(data)){
                return actual.getData();
            }
            actual = actual.getNext();
        }
        return null;
    }


}
