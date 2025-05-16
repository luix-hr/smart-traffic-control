package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.TrafficLight;

import java.io.Serializable;

public class TrafficLightController implements Serializable {
    private static final long serialVersionUID = 1L;

    private LinkedList<IntersectionController> intersecoes;
    private SimConfig config;
    private int modelo = 1;

    public TrafficLightController(LinkedList<IntersectionController> intersecoes, SimConfig config) {
        this.intersecoes = intersecoes;
        this.config = config;
    }

    public void setModelo(int modelo) {
        this.modelo = modelo;
    }

    public void update() {
        Node<IntersectionController> atual = intersecoes.getHead();
        while (atual != null) {
            atual.getData().update(modelo); // usa o modelo salvo no próprio controller
            atual = atual.getNext();
        }
    }

    public LinkedList<TrafficLight> getTodosSemaforos() {
        LinkedList<TrafficLight> todos = new LinkedList<>();
        Node<IntersectionController> atual = intersecoes.getHead();
        while (atual != null) {
            todos.concat(atual.getData().getTodosSemaforos());
            atual = atual.getNext();
        }
        return todos;
    }

    public LinkedList<IntersectionController> getIntersecoes() {
        return intersecoes;
    }
}
