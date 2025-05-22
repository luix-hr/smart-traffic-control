package org.icev.smarttrafficcontrol.datastructure;

import java.io.Serializable;

public class Node<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private T data;
    private Node<T> next;
    private Node<T> prev;

    public Node(){
        this.data = null;
        this.next = null;
        this.prev = null;
    }

    public Node(T data){
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }
}
