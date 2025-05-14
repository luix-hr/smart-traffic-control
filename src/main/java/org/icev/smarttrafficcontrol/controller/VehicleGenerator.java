package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.*;
import org.icev.smarttrafficcontrol.model.Vehicle;
import org.icev.smarttrafficcontrol.service.Dijkstra;
import java.io.Serializable;
import java.util.Random;

public class VehicleGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Graph grafo;
    private Random random;
    private int contador;

    public VehicleGenerator(Graph grafo) {
        this.grafo = grafo;
        this.random = new Random();
        this.contador = 0;
    }

    public Vehicle gerarVeiculoComRota() {
        Vertex origem = sortearVertice();
        Vertex destino = sortearVerticeDiferente(origem);

        Queue<Vertex> rota = Dijkstra.encontrarMenorCaminho(grafo, origem, destino);
        Vehicle veiculo = new Vehicle("V" + contador++, rota);

        System.out.println("Veículo " + veiculo.getId() + " criado com rota de " + origem.getId() + " até " + destino.getId());
        return veiculo;
    }

    public void gerarMultiplosVeiculos(int quantidade, Queue<Vehicle> filaVeiculos) {
        for (int i = 0; i < quantidade; i++) {
            Vehicle veiculo = gerarVeiculoComRota();
            filaVeiculos.enqueue(veiculo);
        }
    }

    private Vertex sortearVertice() {
        int tamanho = grafo.getVertices().getSize();
        int indice = random.nextInt(tamanho);
        Node<Vertex> atual = grafo.getVertices().getHead();
        for (int i = 0; i < indice; i++) {
            atual = atual.getNext();
        }
        return atual.getData();
    }

    private Vertex sortearVerticeDiferente(Vertex diferenteDe) {
        Vertex candidato;
        do {
            candidato = sortearVertice();
        } while (candidato.equals(diferenteDe));
        return candidato;
    }
}
