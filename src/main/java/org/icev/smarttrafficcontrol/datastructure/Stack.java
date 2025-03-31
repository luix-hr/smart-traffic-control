package org.icev.smarttrafficcontrol.datastructure;

public class Stack<T> {
    private Node top;

    public Stack(){
        this.top = null;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void push(T data) {
        Node newNode = new Node(data);
        newNode.setNext(top);
        top = newNode;
    }

    public T pop() {
        if (isEmpty()) {
            throw new IllegalArgumentException("Fila vazia!");
        }
        T data = (T) top.getData();
        top = top.getNext();
        return data;
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalArgumentException("Fila vazia!");
        }
        return (T) top.getData();
    }

    public void print(){
        Node actual = top;
        while (actual != null){
            System.out.print(actual.getData() + " -> ");
            actual = actual.getNext();
        }
        System.out.println("null");
    }
}
