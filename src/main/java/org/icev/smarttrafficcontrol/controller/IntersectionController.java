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
        LinkedList<TrafficLight> grupoAtual = getGrupoAtual();
        LinkedList<TrafficLight> grupoProximo = getGrupoProximo();

        if (!amareloAtivo) {
            if (timer >= config.getCicloVerde()) {
                setGrupoState(grupoAtual, TrafficLight.State.YELLOW);
                amareloAtivo = true;
                timer = 0;
                System.out.println(id + ": Verde -> Amarelo");
            }
        } else {
            if (timer >= config.getCicloAmarelo()) {
                setGrupoState(grupoAtual, TrafficLight.State.RED);
                setGrupoState(grupoProximo, TrafficLight.State.GREEN);
                fase = (fase == 0) ? 1 : 0;
                amareloAtivo = false;
                timer = 0;
                System.out.println(id + ": Amarelo -> Vermelho/Verde");
            }
        }
        timer++;
    }

    private void updateBaseadoEmFila(Queue<Vehicle> filaVeiculos) {
        int filaGrupo1 = contarVeiculos(grupo1, filaVeiculos);
        int filaGrupo2 = contarVeiculos(grupo2, filaVeiculos);

        int tempoVerde = config.getCicloVerde();
        if (fase == 0 && filaGrupo1 > filaGrupo2) tempoVerde += 2;
        if (fase == 1 && filaGrupo2 > filaGrupo1) tempoVerde += 2;

        if (!amareloAtivo) {
            if (timer >= tempoVerde) {
                setGrupoState(getGrupoAtual(), TrafficLight.State.YELLOW);
                amareloAtivo = true;
                timer = 0;
            }
        } else {
            if (timer >= config.getCicloAmarelo()) {
                setGrupoState(getGrupoAtual(), TrafficLight.State.RED);
                setGrupoState(getGrupoProximo(), TrafficLight.State.GREEN);
                fase = (fase == 0) ? 1 : 0;
                amareloAtivo = false;
                timer = 0;
            }
        }
        timer++;
    }

    private void updateEconomico() {
        int tempoVerde = config.getCicloVerde();
        if (config.isModoPico()) tempoVerde += 3;

        if (!amareloAtivo) {
            if (timer >= tempoVerde) {
                setGrupoState(getGrupoAtual(), TrafficLight.State.YELLOW);
                amareloAtivo = true;
                timer = 0;
            }
        } else {
            if (timer >= config.getCicloAmarelo()) {
                setGrupoState(getGrupoAtual(), TrafficLight.State.RED);
                setGrupoState(getGrupoProximo(), TrafficLight.State.GREEN);
                fase = (fase == 0) ? 1 : 0;
                amareloAtivo = false;
                timer = 0;
            }
        }
        timer++;
    }

    private void setGrupoState(LinkedList<TrafficLight> grupo, TrafficLight.State estado) {
        Node<TrafficLight> atual = grupo.getHead();
        System.out.println("Atualizando grupo " + (grupo == grupo1 ? "1" : "2") + " para " + estado);
        while (atual != null) {
            atual.getData().setState(estado);
            System.out.println("Semáforo " + atual.getData().getId() + " definido para " + estado);
            atual = atual.getNext();
        }
    }

    private LinkedList<TrafficLight> getGrupoAtual() {
        return (fase == 0) ? grupo1 : grupo2;
    }

    private boolean grupoEmEstado(LinkedList<TrafficLight> grupo, TrafficLight.State estado) {
        Node<TrafficLight> node = grupo.getHead();
        while (node != null) {
            if (node.getData().getState() != estado) {
                return false;
            }
            node = node.getNext();
        }
        return true;
    }

    private LinkedList<TrafficLight> getGrupoProximo() {
        return (fase == 0) ? grupo2 : grupo1;
    }

    private int contarVeiculos(LinkedList<TrafficLight> grupo, Queue<Vehicle> veiculos) {
        int count = 0;
        Node<TrafficLight> node = grupo.getHead();
        while (node != null) {
            TrafficLight semaforo = node.getData();
            Node<Vehicle> vNode = veiculos.getHead();
            while (vNode != null) {
                Vehicle v = vNode.getData();
                if (v.getProximoDestino() == semaforo.getVinculo()
                        && semaforo.getState() == TrafficLight.State.RED) {
                    count++;
                }
                vNode = vNode.getNext();
            }
            node = node.getNext();
        }
        return count;
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
}
