package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;

import java.io.Serializable;

public class TrafficLightController implements Serializable {
    private static final long serialVersionUID = 1L;

    private LinkedList<IntersectionController> intersecoes;
    private SimConfig config;

    private int timer;
    private boolean amareloAtivo;
    private int modelo = 1;

    public TrafficLightController(LinkedList<IntersectionController> intersecoes, SimConfig config) {
        this.intersecoes = intersecoes;
        this.config = config;
        this.timer = 0;
        this.amareloAtivo = false;
    }

    public void setModelo(int modelo) {
        this.modelo = modelo;
    }

    public void update(Queue<Vehicle> filaVeiculos) {
        switch (modelo) {
            case 1 -> updateFixo();
            case 2 -> updateBaseadoEmFila(filaVeiculos);
            case 3 -> updateEconomico(filaVeiculos);
            default -> updateFixo();
        }
    }

    private void updateFixo() {
        LinkedList<TrafficLight> grupoVerde = obterSemaforosNoVerde();
        LinkedList<TrafficLight> grupoAmarelo = obterSemaforosNoAmarelo(intersecoes);
        LinkedList<TrafficLight> grupoVermelho = obterSemaforosNoVermelho();

        if (!amareloAtivo) {
            if (timer >= config.getCicloVerde()) {
                setGrupoState(grupoVerde, TrafficLight.State.YELLOW);
                amareloAtivo = true;
                timer = 0;
            }
        } else {
            if (timer >= config.getCicloAmarelo()) {
                setGrupoState(grupoVermelho, TrafficLight.State.GREEN);
                setGrupoState(grupoAmarelo, TrafficLight.State.RED);
                amareloAtivo = false;
                timer = 0;
            }
        }

        timer++;
    }

    private void updateBaseadoEmFila(Queue<Vehicle> filaVeiculos) {
        int tamanhoFila = filaVeiculos.getSize();
        int extraVerde = 0;

        if (tamanhoFila > 3) {
            extraVerde = 2 + (tamanhoFila - 3);
            if (extraVerde > 6) extraVerde = 6;
        }

        int cicloVerde = config.getCicloVerde() + extraVerde;
        int cicloAmarelo = config.getCicloAmarelo();

        LinkedList<TrafficLight> grupoVerde = obterSemaforosNoVerde();
        LinkedList<TrafficLight> grupoAmarelo = obterSemaforosNoAmarelo(intersecoes);
        LinkedList<TrafficLight> grupoVermelho = obterSemaforosNoVermelho();

        if (!amareloAtivo) {
            if (timer >= cicloVerde) {
                setGrupoState(grupoVerde, TrafficLight.State.YELLOW);
                amareloAtivo = true;
                timer = 0;
            }
        } else {
            if (timer >= cicloAmarelo) {
                setGrupoState(grupoVermelho, TrafficLight.State.GREEN);
                setGrupoState(grupoAmarelo, TrafficLight.State.RED);
                amareloAtivo = false;
                timer = 0;
            }
        }

        timer++;
    }


    private void updateEconomico(Queue<Vehicle> filaVeiculos) {
        int tamanhoFila = filaVeiculos.getSize();
        int reduzirVermelho = (tamanhoFila > 5) ? 1 : 0;

        int cicloVerde = config.getCicloVerde();
        int cicloAmarelo = config.getCicloAmarelo();
        int cicloVermelho = config.getCicloVerde() - reduzirVermelho;

        LinkedList<TrafficLight> grupoVerde = obterSemaforosNoVerde();
        LinkedList<TrafficLight> grupoAmarelo = obterSemaforosNoAmarelo(intersecoes);
        LinkedList<TrafficLight> grupoVermelho = obterSemaforosNoVermelho();

        if (!amareloAtivo) {
            if (timer >= cicloVerde) {
                setGrupoState(grupoVerde, TrafficLight.State.YELLOW);
                amareloAtivo = true;
                timer = 0;
            }
        } else {
            if (timer >= cicloAmarelo) {
                setGrupoState(grupoVermelho, TrafficLight.State.GREEN);
                setGrupoState(grupoAmarelo, TrafficLight.State.RED);
                amareloAtivo = false;
                timer = 0;
            }
        }

        timer++;
    }



    public LinkedList<TrafficLight> obterSemaforosNoVerde() {
        LinkedList<TrafficLight> grupoVerde = new LinkedList<>();
        Node<IntersectionController> atual = intersecoes.getHead();

        while (atual != null) {
            IntersectionController intersec = atual.getData();
            Node<TrafficLight> node = intersec.getTodosSemaforos().getHead();
            while (node != null) {
                if (node.getData().getState() == TrafficLight.State.GREEN) {
                    grupoVerde.insert(node.getData());
                }
                node = node.getNext();
            }
            atual = atual.getNext();
        }
        return grupoVerde;
    }

    public LinkedList<TrafficLight> obterSemaforosNoAmarelo(LinkedList<IntersectionController> intersecoes) {
        LinkedList<TrafficLight> grupoVerde = new LinkedList<>();

        Node<IntersectionController> atual = intersecoes.getHead();
        while (atual != null) {
            LinkedList<TrafficLight> semaforos = atual.getData().getTodosSemaforos();
            Node<TrafficLight> s = semaforos.getHead();
            while (s != null) {
                if (s.getData().getState() == TrafficLight.State.YELLOW) {
                    grupoVerde.insert(s.getData());
                }
                s = s.getNext();
            }
            atual = atual.getNext();
        }

        return grupoVerde;
    }

    public LinkedList<TrafficLight> obterSemaforosNoVermelho() {
        LinkedList<TrafficLight> grupoVermelho = new LinkedList<>();
        Node<IntersectionController> atual = intersecoes.getHead();

        while (atual != null) {
            IntersectionController intersec = atual.getData();
            Node<TrafficLight> node = intersec.getTodosSemaforos().getHead();
            while (node != null) {
                if (node.getData().getState() == TrafficLight.State.RED) {
                    grupoVermelho.insert(node.getData());
                }
                node = node.getNext();
            }
            atual = atual.getNext();
        }
        return grupoVermelho;
    }

    private void setGrupoState(LinkedList<TrafficLight> grupo, TrafficLight.State estado) {
        Node<TrafficLight> atual = grupo.getHead();
        while (atual != null) {
            atual.getData().setState(estado);
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
}
