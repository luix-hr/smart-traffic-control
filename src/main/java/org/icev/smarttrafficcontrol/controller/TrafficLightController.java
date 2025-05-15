package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.TrafficLight;

import java.io.Serializable;

public class TrafficLightController implements Serializable {
    private static final long serialVersionUID = 1L;

    private LinkedList<TrafficLight> semaforos;
    private SimConfig config;
    private int modelo = 1; // Modelo padrão

    public TrafficLightController(LinkedList<TrafficLight> semaforos, SimConfig config) {
        this.semaforos = semaforos;
        this.config = config;
        inicializarTempos();
    }

    private void inicializarTempos() {
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            atual.getData().updateCycle(config.getCicloVerde(), config.getCicloAmarelo(), config.getCicloVermelho());
            atual = atual.getNext();
        }
    }

    public void update(int modelo) {
        this.modelo = modelo;
        switch (modelo) {
            case 1 -> modeloFixo();
            case 2 -> modeloPorFila();
            case 3 -> modeloEnergetico();
            default -> modeloFixo();
        }
        imprimirEstadoSemaforos();
    }

    public void setModelo(int modelo) {
        this.modelo = modelo;
    }

    private void modeloFixo() {
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            TrafficLight tf = atual.getData();
            tf.updateCycle(config.getCicloVerde(), config.getCicloAmarelo(), config.getCicloVermelho());
            atual = atual.getNext();
        }
    }

    private void modeloPorFila() {
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            TrafficLight tf = atual.getData();
            Vertex vertice = tf.getVinculo();

            int filaVeiculos = (vertice != null) ? vertice.getTamanhoFilaEspera() : 0;
            int tempoVerde = config.getCicloVerde();
            if (filaVeiculos > 5) tempoVerde += 3;

            tf.updateCycle(tempoVerde, config.getCicloAmarelo(), config.getCicloVermelho());
            atual = atual.getNext();
        }
    }

    private void modeloEnergetico() {
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            TrafficLight tf = atual.getData();
            if (config.isModoPico()) {
                tf.updateCycle(config.getCicloVerde() + 5, config.getCicloAmarelo(), config.getCicloVermelho() - 2);
            } else {
                tf.updateCycle(config.getCicloVerde(), config.getCicloAmarelo(), config.getCicloVermelho());
            }
            atual = atual.getNext();
        }
    }

    private void imprimirEstadoSemaforos() {
        System.out.println("=== Estado dos Semáforos ===");
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            TrafficLight tf = atual.getData();
            System.out.println("Semáforo " + tf.getId() + " Estado: " + tf.getState() + " Timer: " + tf.getTimer());
            atual = atual.getNext();
        }
        System.out.println("============================");
    }

    public LinkedList<TrafficLight> getTrafficLights() {
        return semaforos;
    }
}
