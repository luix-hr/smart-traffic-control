package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.TrafficLight;

import java.io.Serializable;

public class IntersectionController implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LinkedList<TrafficLight> grupo1;
    private LinkedList<TrafficLight> grupo2;

    private int timer;
    private int fase; // 0 ou 1
    private boolean faseAmarelo;
    private SimConfig config;

    public IntersectionController(String id, LinkedList<TrafficLight> grupo1, LinkedList<TrafficLight> grupo2, SimConfig config) {
        this.id = id;
        this.grupo1 = grupo1;
        this.grupo2 = grupo2;
        this.config = config;
        this.fase = 0;
        this.timer = 0;
        this.faseAmarelo = false;

        setGrupoState(grupo1, TrafficLight.State.GREEN);
        setGrupoState(grupo2, TrafficLight.State.RED);
    }

    public void update(int modelo) {
        switch (modelo) {
            case 1 -> updateFixo();
            case 2 -> updateBaseadoEmFila();
            case 3 -> updateEconomico();
            default -> updateFixo();
        }
    }

    private void updateFixo() {
        timer++;

        if (!faseAmarelo) {
            int tempoVerde = config.getCicloVerde();
            if (timer >= tempoVerde) {
                setGrupoState(getGrupoAtual(), TrafficLight.State.YELLOW);
                faseAmarelo = true;
                timer = 0;
            }
        } else {
            int tempoAmarelo = config.getCicloAmarelo();
            if (timer >= tempoAmarelo) {
                alternarFase();
                faseAmarelo = false;
                timer = 0;
            }
        }
    }

    private void updateBaseadoEmFila() {
        timer++;

        int filaGrupo1 = (int) (Math.random() * 10);
        int filaGrupo2 = (int) (Math.random() * 10);

        int tempoVerde = config.getCicloVerde();
        if (filaGrupo1 > filaGrupo2) tempoVerde += 2;

        if (!faseAmarelo) {
            if (timer >= tempoVerde) {
                setGrupoState(getGrupoAtual(), TrafficLight.State.YELLOW);
                faseAmarelo = true;
                timer = 0;
            }
        } else {
            if (timer >= config.getCicloAmarelo()) {
                alternarFase();
                faseAmarelo = false;
                timer = 0;
            }
        }
    }

    private void updateEconomico() {
        timer++;

        int tempoVerde = config.getCicloVerde();
        int tempoVermelho = config.getCicloVermelho();
        if (config.isModoPico()) tempoVerde += 3;

        if (!faseAmarelo) {
            int tempoAtual = (fase == 0) ? tempoVerde : tempoVermelho;
            if (timer >= tempoAtual) {
                setGrupoState(getGrupoAtual(), TrafficLight.State.YELLOW);
                faseAmarelo = true;
                timer = 0;
            }
        } else {
            if (timer >= config.getCicloAmarelo()) {
                alternarFase();
                faseAmarelo = false;
                timer = 0;
            }
        }
    }

    private void alternarFase() {
        if (fase == 0) {
            setGrupoState(grupo1, TrafficLight.State.RED);
            setGrupoState(grupo2, TrafficLight.State.GREEN);
            fase = 1;
        } else {
            setGrupoState(grupo2, TrafficLight.State.RED);
            setGrupoState(grupo1, TrafficLight.State.GREEN);
            fase = 0;
        }
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

    public String getId() {
        return id;
    }
}
