package org.icev.smarttrafficcontrol.service;

import org.icev.smarttrafficcontrol.datastructure.*;

public class Dijkstra<T> {

    public Queue<GraphNode<T>> findShortestPath(LinkedList<GraphNode<T>> vertices,
                                                GraphNode<T> start,
                                                GraphNode<T> end) {

        LinkedList<DistanceEntry<T>> distances = new LinkedList<>();
        LinkedList<PreviousEntry<T>> previous = new LinkedList<>();

        Node<GraphNode<T>> current = vertices.getHead();
        while (current != null) {
            double initialDist = current.getData().equals(start) ? 0 : Double.POSITIVE_INFINITY;
            distances.insert(new DistanceEntry<>(current.getData(), initialDist));
            previous.insert(new PreviousEntry<>(current.getData(), null));
            current = current.getNext();
        }

        while (!allVisited(distances)) {
            DistanceEntry<T> min = getMinDistance(distances);
            if (min == null || min.node.equals(end)) break;
            min.visited = true;

            Node<Edge> edgeNode = min.node.getAdjacentNodes().getHead();
            while (edgeNode != null) {
                GraphNode<T> neighbor = edgeNode.getData().getDestination();
                double newDist = min.distance + edgeNode.getData().getWeight();
                DistanceEntry<T> neighborEntry = getEntry(distances, neighbor);

                if (!neighborEntry.visited && newDist < neighborEntry.distance) {
                    neighborEntry.distance = newDist;
                    setPrevious(previous, neighbor, min.node);
                }

                edgeNode = edgeNode.getNext();
            }
        }

        return buildPath(previous, start, end);
    }

    private boolean allVisited(LinkedList<DistanceEntry<T>> list) {
        Node<DistanceEntry<T>> current = list.getHead();
        while (current != null) {
            if (!current.getData().visited) return false;
            current = current.getNext();
        }
        return true;
    }

    private DistanceEntry<T> getMinDistance(LinkedList<DistanceEntry<T>> list) {
        Node<DistanceEntry<T>> current = list.getHead();
        DistanceEntry<T> min = null;

        while (current != null) {
            DistanceEntry<T> entry = current.getData();
            if (!entry.visited && (min == null || entry.distance < min.distance)) {
                min = entry;
            }
            current = current.getNext();
        }
        return min;
    }

    private DistanceEntry<T> getEntry(LinkedList<DistanceEntry<T>> list, GraphNode<T> node) {
        Node<DistanceEntry<T>> current = list.getHead();
        while (current != null) {
            if (current.getData().node.equals(node)) {
                return current.getData();
            }
            current = current.getNext();
        }
        return null;
    }

    private void setPrevious(LinkedList<PreviousEntry<T>> list, GraphNode<T> node, GraphNode<T> previousNode) {
        Node<PreviousEntry<T>> current = list.getHead();
        while (current != null) {
            if (current.getData().node.equals(node)) {
                current.getData().previous = previousNode;
                return;
            }
            current = current.getNext();
        }
    }

    private Queue<GraphNode<T>> buildPath(LinkedList<PreviousEntry<T>> previous,
                                          GraphNode<T> start,
                                          GraphNode<T> end) {

        Stack<GraphNode<T>> stack = new Stack<>();
        Queue<GraphNode<T>> path = new Queue<>();

        GraphNode<T> current = end;
        while (current != null) {
            stack.push(current);
            current = getPreviousNode(previous, current);
        }

        while (!stack.isEmpty()) {
            path.enqueue(stack.pop());
        }

        return path;
    }

    private GraphNode<T> getPreviousNode(LinkedList<PreviousEntry<T>> list, GraphNode<T> node) {
        Node<PreviousEntry<T>> current = list.getHead();
        while (current != null) {
            if (current.getData().node.equals(node)) {
                return current.getData().previous;
            }
            current = current.getNext();
        }
        return null;
    }
}
