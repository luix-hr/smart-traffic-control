package org.icev.smarttrafficcontrol.service;

import org.icev.smarttrafficcontrol.datastructure.graph.*;
import org.icev.smarttrafficcontrol.datastructure.*;

import java.io.Serializable;

public class Dijkstra implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Queue<Vertex> encontrarMenorCaminho(Graph grafo, Vertex origem, Vertex destino) {
        LinkedList<Distancia> distancias = new LinkedList<>();
        LinkedList<Anterior> anteriores = new LinkedList<>();

        // Inicializa distâncias
        Node<Vertex> atual = grafo.getVertices().getHead();
        while (atual != null) {
            Vertex v = atual.getData();
            double distanciaInicial = v.equals(origem) ? 0.0 : Double.POSITIVE_INFINITY;
            distancias.insert(new Distancia(v, distanciaInicial));
            anteriores.insert(new Anterior(v, null));
            atual = atual.getNext();
        }

        // Algoritmo principal
        while (!todosVisitados(distancias)) {
            Distancia menor = encontrarMenor(distancias);
            if (menor == null || menor.vertice.equals(destino)) break;
            menor.visitado = true;

            LinkedList<Edge> arestas = grafo.obterArestasDe(menor.vertice);
            Node<Edge> noAresta = arestas.getHead();
            while (noAresta != null) {
                Edge aresta = noAresta.getData();
                Vertex vizinho = aresta.getTarget();

                Distancia entradaVizinho = buscar(distancias, vizinho);
                double novaDist = menor.distancia + aresta.getTravelTime();

                if (!entradaVizinho.visitado && novaDist < entradaVizinho.distancia) {
                    entradaVizinho.distancia = novaDist;
                    atualizarAnterior(anteriores, vizinho, menor.vertice);
                }

                noAresta = noAresta.getNext();
            }
        }

        return construirCaminho(anteriores, origem, destino);
    }

    // Métodos auxiliares

    private static boolean todosVisitados(LinkedList<Distancia> lista) {
        Node<Distancia> atual = lista.getHead();
        while (atual != null) {
            if (!atual.getData().visitado) return false;
            atual = atual.getNext();
        }
        return true;
    }

    private static Distancia encontrarMenor(LinkedList<Distancia> lista) {
        Node<Distancia> atual = lista.getHead();
        Distancia menor = null;
        while (atual != null) {
            Distancia d = atual.getData();
            if (!d.visitado && (menor == null || d.distancia < menor.distancia)) {
                menor = d;
            }
            atual = atual.getNext();
        }
        return menor;
    }

    private static Distancia buscar(LinkedList<Distancia> lista, Vertex v) {
        Node<Distancia> atual = lista.getHead();
        while (atual != null) {
            if (atual.getData().vertice.equals(v)) return atual.getData();
            atual = atual.getNext();
        }
        return null;
    }

    private static void atualizarAnterior(LinkedList<Anterior> lista, Vertex v, Vertex anterior) {
        Node<Anterior> atual = lista.getHead();
        while (atual != null) {
            if (atual.getData().vertice.equals(v)) {
                atual.getData().anterior = anterior;
                return;
            }
            atual = atual.getNext();
        }
    }

    private static Vertex buscarAnterior(LinkedList<Anterior> lista, Vertex v) {
        Node<Anterior> atual = lista.getHead();
        while (atual != null) {
            if (atual.getData().vertice.equals(v)) return atual.getData().anterior;
            atual = atual.getNext();
        }
        return null;
    }

    private static Queue<Vertex> construirCaminho(LinkedList<Anterior> anteriores, Vertex origem, Vertex destino) {
        Stack<Vertex> pilha = new Stack<>();
        Queue<Vertex> caminho = new Queue<>();

        Vertex atual = destino;
        while (atual != null) {
            pilha.push(atual);
            atual = buscarAnterior(anteriores, atual);
        }

        while (!pilha.isEmpty()) {
            caminho.enqueue(pilha.pop());
        }

        return caminho;
    }

    // Classes auxiliares internas
    private static class Distancia {
        Vertex vertice;
        double distancia;
        boolean visitado;

        Distancia(Vertex vertice, double distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
            this.visitado = false;
        }
    }

    private static class Anterior {
        Vertex vertice;
        Vertex anterior;

        Anterior(Vertex vertice, Vertex anterior) {
            this.vertice = vertice;
            this.anterior = anterior;
        }
    }
}
