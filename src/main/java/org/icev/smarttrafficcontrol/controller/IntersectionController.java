package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;

import java.io.Serializable;

public class IntersectionController implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LinkedList<TrafficLight> grupo1;
    private LinkedList<TrafficLight> grupo2;

    private int fase;
    private SimConfig config;

    public IntersectionController(String id, LinkedList<TrafficLight> grupo1, LinkedList<TrafficLight> grupo2, SimConfig config) {
        this.id = id;
        this.grupo1 = grupo1;
        this.grupo2 = grupo2;
        this.config = config;
        this.fase = 0;

        setGrupoState(grupo1, TrafficLight.State.GREEN);
        setGrupoState(grupo2, TrafficLight.State.RED);
    }

    public void update(int modelo, Queue<Vehicle> filaVeiculos) {
        switch (modelo) {
            case 1 -> updateFixo();
            case 2 -> updateBaseadoEmFila(filaVeiculos);
            case 3 -> updateEconomico();
            default -> updateFixo();
        }
    }

    private void updateFixo() {
        int verde = config.getCicloVerde();
        int amarelo = config.getCicloAmarelo();
        int vermelho = config.getCicloVermelho();

        LinkedList<TrafficLight> grupoAtual = getGrupoAtual();
        Node<TrafficLight> node = grupoAtual.getHead();
        while (node != null) {
            node.getData().updateCycle(verde, amarelo, vermelho);
            node = node.getNext();
        }

        if (grupoAtualTodosRed(grupoAtual)) alternarFase();
    }

    private void updateBaseadoEmFila(Queue<Vehicle> filaVeiculos) {
        int filaGrupo1 = contarVeiculosEsperando(grupo1, filaVeiculos);
        int filaGrupo2 = contarVeiculosEsperando(grupo2, filaVeiculos);

        int verde = config.getCicloVerde();
        if (fase == 0 && filaGrupo1 > 5) verde += 2;
        else if (fase == 1 && filaGrupo2 > 5) verde += 2;

        int amarelo = config.getCicloAmarelo();
        int vermelho = config.getCicloVermelho();

        LinkedList<TrafficLight> grupoAtual = getGrupoAtual();
        Node<TrafficLight> node = grupoAtual.getHead();
        while (node != null) {
            node.getData().updateCycle(verde, amarelo, vermelho);
            node = node.getNext();
        }

        if (grupoAtualTodosRed(grupoAtual)) alternarFase();
    }

    private void updateEconomico() {
        int verde = config.getCicloVerde();
        int vermelho = config.getCicloVermelho();
        int amarelo = config.getCicloAmarelo();

        if (config.isModoPico()) {
            verde += 3;
            vermelho = Math.max(vermelho - 1, amarelo + 1);
        }

        LinkedList<TrafficLight> grupoAtual = getGrupoAtual();
        Node<TrafficLight> node = grupoAtual.getHead();
        while (node != null) {
            node.getData().updateCycle(verde, amarelo, vermelho);
            node = node.getNext();
        }

        if (grupoAtualTodosRed(grupoAtual)) alternarFase();
    }


    private boolean grupoAtualTodosRed(LinkedList<TrafficLight> grupo) {
        Node<TrafficLight> node = grupo.getHead();
        while (node != null) {
            if (node.getData().getState() != TrafficLight.State.RED) {
                return false;
            }
            node = node.getNext();
        }
        return true;
    }

    private void alternarFase() {
        LinkedList<TrafficLight> grupoAtual = getGrupoAtual();
        LinkedList<TrafficLight> proximoGrupo = (fase == 0) ? grupo2 : grupo1;

        setGrupoState(grupoAtual, TrafficLight.State.RED);
        setGrupoState(proximoGrupo, TrafficLight.State.GREEN);
        fase = 1 - fase;
    }

    private void setGrupoState(LinkedList<TrafficLight> grupo, TrafficLight.State estado) {
        Node<TrafficLight> atual = grupo.getHead();
        while (atual != null) {
            atual.getData().setState(estado);
            atual.getData().resetTimer();
            atual = atual.getNext();
        }
    }

    private LinkedList<TrafficLight> getGrupoAtual() {
        return (fase == 0) ? grupo1 : grupo2;
    }

    private int contarVeiculosEsperando(LinkedList<TrafficLight> grupo, Queue<Vehicle> veiculos) {
        int contador = 0;
        Node<TrafficLight> s = grupo.getHead();
        while (s != null) {
            TrafficLight semaforo = s.getData();
            Node<Vehicle> v = veiculos.getHead();
            while (v != null) {
                Vehicle veiculo = v.getData();
                if (veiculo.getProximoDestino() == semaforo.getVinculo()
                        && semaforo.getState() == TrafficLight.State.RED) {
                    contador++;
                }
                v = v.getNext();
            }
            s = s.getNext();
        }

        return contador;
    }

    public String getId() {
        return id;
    }

    public LinkedList<TrafficLight> getTodosSemaforos() {
        LinkedList<TrafficLight> todos = new LinkedList<>();
        todos.concat(grupo1);
        todos.concat(grupo2);
        return todos;
    }

    public LinkedList<TrafficLight> getGrupo1() {
        return grupo1;
    }

    public LinkedList<TrafficLight> getGrupo2() {
        return grupo2;
    }
}
