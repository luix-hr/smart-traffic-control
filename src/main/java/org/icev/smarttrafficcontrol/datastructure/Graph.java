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

    public void insertVertex(T data) {
        if (getNode(data) == null) {
            nodes.insertLast(new GraphNode<>(data));
        }
    }

    public void insertEdge(T source, T destination, double weight) {
        GraphNode<T> sourceNode = getNode(source);
        GraphNode<T> destinationNode = getNode(destination);

        if (sourceNode == null || destinationNode == null) {
            throw new IllegalArgumentException("Vértices não encontrados");
        }

        sourceNode.insertEdge(destinationNode, weight);
        destinationNode.insertEdge(sourceNode, weight);
    }

    public void print() {
        Node<GraphNode> current = nodes.getHead();
        while (current != null) {
            System.out.print(current.getData().getData() + " -> ");
            Node<Edge> edgeNode = current.getData().getAdjacentNodes().getHead();
            while (edgeNode != null) {
                System.out.print("[" + edgeNode.getData().getDestination().getData() + " (" + edgeNode.getData().getWeight() + ")] ");
                edgeNode = edgeNode.getNext();
            }
            System.out.println();
            current = current.getNext();
        }
    }


}
