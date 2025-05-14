package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.model.TrafficLight;

import java.io.Serializable;

public class TrafficLightController implements Serializable {
    private static final long serialVersionUID = 1L;

    private LinkedList<TrafficLight> semaforos;
    private int modelo = 1; // Padrão: Modelo 1 (fixo)

    public TrafficLightController(LinkedList<TrafficLight> semaforos) {
        this.semaforos = semaforos;
    }

    public void setModelo(int modelo) {
        this.modelo = modelo;
    }

    public void update(int modelo) {
        switch (modelo) {
            case 1:
                modeloFixo();
                break;
            case 2:
                modeloPorFila();
                break;
            case 3:
                modeloEnergetico();
                break;
            default:
                modeloFixo();
        }
    }

    private void modeloFixo() {
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            atual.getData().update();
            atual = atual.getNext();
        }
    }

    private void modeloPorFila() {
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            TrafficLight semaforo = atual.getData();
            // Exemplo: ciclo mais longo se simulado mais veículos na interseção (mock)
            int filaMock = (int) (Math.random() * 10); // simulação
            semaforo.setDuration(3 + filaMock); // ajusta dinamicamente
            semaforo.update();
            atual = atual.getNext();
        }
    }

    private void modeloEnergetico() {
        Node<TrafficLight> atual = semaforos.getHead();
        while (atual != null) {
            TrafficLight semaforo = atual.getData();
            int horaDoDia = (int) (System.currentTimeMillis() / 1000) % 24; // mock do relógio
            if (horaDoDia >= 7 && horaDoDia <= 9 || horaDoDia >= 17 && horaDoDia <= 19) {
                semaforo.setDuration(6); // horários de pico
            } else {
                semaforo.setDuration(3); // economia
            }
            semaforo.update();
            atual = atual.getNext();
        }
    }

    public LinkedList<TrafficLight> getTrafficLights() {
        return semaforos;
    }
}