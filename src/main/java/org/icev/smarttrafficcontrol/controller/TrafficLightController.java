package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
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
            int filaVeiculos = simularTamanhoFila(tf);
            int tempoVerde = config.getCicloVerde();
            if (filaVeiculos > 5) tempoVerde += 3;

            tf.updateCycle(tempoVerde, config.getCicloAmarelo(), config.getCicloVermelho());
            atual = atual.getNext();
        }
    }

    private int simularTamanhoFila(TrafficLight tf) {
        return (int) (Math.random() * 10);
    }

    private void modeloEnergetico() {
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            TrafficLight tf = atual.getData();
            if (config.isHorarioDePico()) {
                tf.updateCycle(config.getCicloVerde() + 5, config.getCicloAmarelo(), config.getCicloVermelho() - 2);
            } else {
                tf.updateCycle(config.getCicloVerde(), config.getCicloAmarelo(), config.getCicloVermelho());
            }
            atual = atual.getNext();
        }
    }

    public LinkedList<TrafficLight> getTrafficLights() {
        return semaforos;
    }
}
