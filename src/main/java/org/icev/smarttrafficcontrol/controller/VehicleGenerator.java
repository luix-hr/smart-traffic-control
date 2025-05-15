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

        if (rota == null || rota.isEmpty()) {
            System.out.println("⚠️ Caminho invalido de " + origem.getId() + " ate " + destino.getId() + ". Veiculo nao criado.");
            return null;
        }

        if (rota.peek().equals(origem)) {
            rota.dequeue();
        }

        Vehicle veiculo = new Vehicle("V" + contador++, rota);
        System.out.println("✅ Veiculo " + veiculo.getId() + " criado com rota de " + origem.getId() + " ate " + destino.getId());
        return veiculo;
    }

    public void gerarMultiplosVeiculos(int quantidade, Queue<Vehicle> filaVeiculos) {
        int tentativas = 0;
        while (quantidade > 0 && tentativas < 10 * quantidade) {
            Vehicle veiculo = gerarVeiculoComRota();
            if (veiculo != null) {
                filaVeiculos.enqueue(veiculo);
                quantidade--;
            }
            tentativas++;
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
