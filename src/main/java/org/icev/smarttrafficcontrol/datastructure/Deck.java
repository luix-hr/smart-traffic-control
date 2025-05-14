package org.icev.smarttrafficcontrol.datastructure;

import java.io.Serializable;

public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;
    private Node head;
    private Node tail;

    public void addFront(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
        }
    }

    public void rotation(int k){
        if(head == null || head == tail) return;

        if (k > 0){
            Node current = head;
            int count = 0;

            while(count != k){
                head = current.getNext();
                current.getNext().setPrev(null);
                tail.setNext(current);
                current.setPrev(tail);
                tail = current;
                current.setNext(null);
                current = head;
                count++;

            }
        } else {
            Node last = tail;
            int count = 0;

            while (count != k){
                tail = last.getPrev();
                tail.setNext(null);
                last.setNext(head);
                head.setPrev(last);
                last.setPrev(null);
                head = last;

                last = tail;
                count--;
            }
        }
    }

    public void print() {
        if (head == null) {
            System.out.println("Deque vazio!");
            return;
        }
        Node temp = head;
        while (temp != null) {
            System.out.print(temp.getData() + " ⇄ ");
            temp = temp.getNext();
        }
        System.out.println("NULL");
    }
}
