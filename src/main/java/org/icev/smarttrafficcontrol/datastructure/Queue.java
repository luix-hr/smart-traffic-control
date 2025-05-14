package org.icev.smarttrafficcontrol.datastructure;

import java.io.Serializable;

public class Queue<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Node<T> head;
    private Node<T> tail;

    public Queue(){
        this.head = null;
        this.tail = null;
    }

    public void enqueue(T data){
        Node newNode = new Node<T>(data);

        if (head == null){
            head = newNode;
            tail = newNode;
        }else {
            tail.setNext(newNode);
            tail = newNode;
        }
    }

    public T dequeue(){
        if (head == null){
            throw new IllegalArgumentException("Fila vazia");
        } else {
            Node actual = head;
            head = actual.getNext();
            actual.setNext(null);

            return (T) actual.getData();
        }
    }

    public T peek() {
        if (head == null) {
            throw new IllegalArgumentException("Fila vazia");
        }
        return head.getData();
    }


    public void print(){
        Node actual = head;
        while (actual != null){
            System.out.print(actual.getData() + " -> ");
            actual = actual.getNext();
        }
        System.out.println("null");
    }


    public boolean isEmpty() {
        if (head == null){
            return true;
        }
        return false;
    }
}
