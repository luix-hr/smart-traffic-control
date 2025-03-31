package org.icev.smarttrafficcontrol.datastructure;

public class LinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedList(){
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void insert(final T data, final int position){
        Node newNode = new Node<T>(data);

        if (position < 0 || position > size){
            throw new IllegalArgumentException("Posição inválida!");
        }

        if (head == null){
            head = newNode;
            tail = newNode;
        }else if (position == 0){
            newNode.setNext(head);
            head = newNode;
        }else if (position == size) {
            tail.setNext(newNode);
            tail = newNode;
        } else {
            Node actual = head;
            int i = 1;
            while (i < position){
                actual = actual.getNext();
                i++;
            }
            newNode.setNext(actual.getNext());
            actual.setNext(newNode);
        }
        size++;
    }

    public void insertLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> actual = head;
            while (actual.getNext() != null) {
                actual = actual.getNext();
            }
            actual.setNext(newNode);
        }
        size++;
    }


    public void insert(final T data){
        Node newNode = new Node<T>(data);

        if (head == null){
            head = newNode;
            tail = newNode;
        }else {
            newNode.setNext(head);
            head = newNode;
        }
        size++;
    }

    public boolean remove(T data) {
        if (isEmpty()) return false;

        if (head.getData().equals(data)) {
            head = head.getNext();
            size--;
            return true;
        }

        Node<T> actual = head;
        while (actual.getNext() != null && !actual.getNext().getData().equals(data)) {
            actual = actual.getNext();
        }

        if (actual.getNext() == null) return false;

        actual.setNext(actual.getNext().getNext());
        size--;
        return true;
    }

    public Node<T> getHead() {
        return head;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public boolean contains(T data) {
        Node<T> actual = head;
        while (actual != null) {
            if (actual.getData().equals(data)) return true;
            actual = actual.getNext();
        }
        return false;
    }

    public void print(){
        Node actual = head;
        while (actual != null){
            System.out.print(actual.getData() + " -> ");
            actual = actual.getNext();
        }
        System.out.println("null");
    }

}
