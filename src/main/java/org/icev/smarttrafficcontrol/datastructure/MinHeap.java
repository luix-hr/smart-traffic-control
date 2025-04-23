package org.icev.smarttrafficcontrol.datastructure;

public class MinHeap<T> {
    private LinkedList<Node> heap;

    public MinHeap() {
        this.heap = new LinkedList<>();
    }

//    public void insert(Node<T> node) {
//        if (heap.isEmpty()) {
//            heap.insertLast(node);
//            return;
//        }
//
//        Node current = heap.getHead();
//        Node previous = null;
//
//        while (current != null && node.getPriority() > current.getPriority()) {
//            previous = current;
//            current = current.getNext();
//        }
//
//        if (previous == null) {
//            heap.insert(node);
//        } else {
//            heap.insertLast(previous, node);
//        }
//    }
//
//    public Node<T> extractMin() {
//        if (heap.isEmpty()) throw new IllegalArgumentException("Heap vazio");
//        return heap.removeFirst();
//    }
}
