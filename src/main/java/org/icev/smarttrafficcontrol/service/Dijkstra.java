package org.icev.smarttrafficcontrol.service;

import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.datastructure.*;

public class Dijkstra<T> {

    public static Queue<Vertex> encontrarMenorCaminho(Graph grafo, Vertex origem, Vertex destino) {
        LinkedList<Vertex> vertices = grafo.getVertices();
        LinkedList<EntradaDistancia> distancias = new LinkedList<>();
        LinkedList<EntradaAnterior> anteriores = new LinkedList<>();

        Node<Vertex> atual = vertices.getHead();
        while (atual != null) {
            Vertex v = atual.getData();
            double distInicial = v.equals(origem) ? 0 : Double.POSITIVE_INFINITY;
            distancias.insert(new EntradaDistancia(v, distInicial));
            anteriores.insert(new EntradaAnterior(v, null));
            atual = atual.getNext();
        }

        while (!todosVisitados(distancias)) {
            EntradaDistancia min = encontrarMinimo(distancias);
            if (min == null || min.vertice.equals(destino)) break;
            min.visitado = true;

            LinkedList<Edge> arestas = grafo.obterArestasDe(min.vertice);
            Node<Edge> noAresta = arestas.getHead();

            while (noAresta != null) {
                Edge aresta = noAresta.getData();
                Vertex vizinho = aresta.getDestino();
                EntradaDistancia entradaVizinho = buscar(distancias, vizinho);

                double novaDist = min.distancia + aresta.getCusto();
                if (!entradaVizinho.visitado && novaDist < entradaVizinho.distancia) {
                    entradaVizinho.distancia = novaDist;
                    atualizarAnterior(anteriores, vizinho, min.vertice);
                }
                noAresta = noAresta.getNext();
            }
        }

        return construirCaminho(anteriores, origem, destino);
    }

    private static boolean todosVisitados(LinkedList<EntradaDistancia> lista) {
        Node<EntradaDistancia> atual = lista.getHead();
        while (atual != null) {
            if (!atual.getData().visitado) return false;
            atual = atual.getNext();
        }
        return true;
    }

    private static EntradaDistancia encontrarMinimo(LinkedList<EntradaDistancia> lista) {
        Node<EntradaDistancia> atual = lista.getHead();
        EntradaDistancia min = null;

        while (atual != null) {
            EntradaDistancia ed = atual.getData();
            if (!ed.visitado && (min == null || ed.distancia < min.distancia)) {
                min = ed;
            }
            atual = atual.getNext();
        }
        return min;
    }

    private static EntradaDistancia buscar(LinkedList<EntradaDistancia> lista, Vertex vertice) {
        Node<EntradaDistancia> atual = lista.getHead();
        while (atual != null) {
            if (atual.getData().vertice.equals(vertice)) {
                return atual.getData();
            }
            atual = atual.getNext();
        }
        return null;
    }

    private static void atualizarAnterior(LinkedList<EntradaAnterior> lista, Vertex vertice, Vertex anterior) {
        Node<EntradaAnterior> atual = lista.getHead();
        while (atual != null) {
            if (atual.getData().vertice.equals(vertice)) {
                atual.getData().anterior = anterior;
                return;
            }
            atual = atual.getNext();
        }
    }

    private static Queue<Vertex> construirCaminho(LinkedList<EntradaAnterior> anteriores, Vertex origem, Vertex destino) {
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

    private static Vertex buscarAnterior(LinkedList<EntradaAnterior> lista, Vertex vertice) {
        Node<EntradaAnterior> atual = lista.getHead();
        while (atual != null) {
            if (atual.getData().vertice.equals(vertice)) {
                return atual.getData().anterior;
            }
            atual = atual.getNext();
        }
        return null;
    }

    // Classes auxiliares
    private static class EntradaDistancia {
        Vertex vertice;
        double distancia;
        boolean visitado;

        EntradaDistancia(Vertex vertice, double distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
            this.visitado = false;
        }
    }

    private static class EntradaAnterior {
        Vertex vertice;
        Vertex anterior;

        EntradaAnterior(Vertex vertice, Vertex anterior) {
            this.vertice = vertice;
            this.anterior = anterior;
        }
    }
}
