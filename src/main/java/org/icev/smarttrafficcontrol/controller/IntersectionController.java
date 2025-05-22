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
    private boolean amareloAtivo;
    private int timer;

    private SimConfig config;

    public IntersectionController(String id, LinkedList<TrafficLight> grupo1, LinkedList<TrafficLight> grupo2, SimConfig config) {
        this.id = id;
        this.grupo1 = grupo1;
        this.grupo2 = grupo2;
        this.config = config;
        this.fase = 0;
        this.amareloAtivo = false;
        this.timer = 0;

        inicializarEstados();
    }

    private void inicializarEstados() {
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
        synchronized (this) {
            LinkedList<TrafficLight> grupoAtual = getGrupoAtual();
            LinkedList<TrafficLight> grupoOposto = getGrupoProximo();

            if (!amareloAtivo) {
                if (timer >= config.getCicloVerde()) {
                    setGrupoState(grupoOposto, TrafficLight.State.RED);
                    setGrupoState(grupoAtual, TrafficLight.State.YELLOW);
                    amareloAtivo = true;
                    timer = 0;
                }
            } else {
                if (timer >= config.getCicloAmarelo()) {
                    setGrupoState(grupoAtual, TrafficLight.State.RED);
                    setGrupoState(grupoOposto, TrafficLight.State.GREEN);
                    fase = (fase == 0) ? 1 : 0;
                    amareloAtivo = false;
                    timer = 0;
                }
            }
            timer++;
        }
    }

    private void updateBaseadoEmFila(Queue<Vehicle> filaVeiculos) {
        updateFixo();
    }


    private void updateEconomico() {
        updateFixo();
    }


    private void setGrupoState(LinkedList<TrafficLight> grupo, TrafficLight.State estado) {
        System.out.println("\n=== Definindo Grupo " + (grupo == grupo1 ? "1" : "2") + " para " + estado + " ===");
        Node<TrafficLight> atual = grupo.getHead();
        while (atual != null) {
            System.out.println("Semáforo " + atual.getData().getId() + " → " + estado);
            atual.getData().setState(estado);
            atual = atual.getNext();
        }
    }

    private boolean validarEstadoGrupo(LinkedList<TrafficLight> grupo, TrafficLight.State estado) {
        Node<TrafficLight> atual = grupo.getHead();
        while (atual != null) {
            if (atual.getData().getState() != estado) {
                System.err.println("ERRO: Semáforo " + atual.getData().getId()
                        + " está em estado inconsistente (" + estado + " esperado)");
                return false;
            }
            atual = atual.getNext();
        }
        return true;
    }


    private LinkedList<TrafficLight> getGrupoAtual() {
        return (fase == 0) ? grupo1 : grupo2;
    }


    private LinkedList<TrafficLight> getGrupoProximo() {
        return (fase == 0) ? grupo2 : grupo1;
    }

    public String getId() {
        return id;
    }

    public LinkedList<TrafficLight> getGrupo2() {
        return grupo2;
    }

    public LinkedList<TrafficLight> getGrupo1() {
        return grupo1;
    }

    public LinkedList<TrafficLight> getTodosSemaforos() {
        LinkedList<TrafficLight> todos = new LinkedList<>();
        todos.concat(grupo1);
        todos.concat(grupo2);
        return todos;
    }
}
