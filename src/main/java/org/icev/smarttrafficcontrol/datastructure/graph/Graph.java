package org.icev.smarttrafficcontrol.datastructure.graph;

import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;

import java.io.Serializable;

public class Graph<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private LinkedList<Vertex> vertices;
    private LinkedList<Edge> arestas;

    public Graph() {
        this.vertices = new LinkedList<>();
        this.arestas = new LinkedList<>();
    }

    public void adicionarVertice(Vertex vertice) {
        vertices.insert(vertice);
    }

    public void adicionarAresta(Edge aresta) {
        arestas.insert(aresta);
    }

    public LinkedList<Vertex> getVertices() {
        return vertices;
    }

    public LinkedList<Edge> getArestas() {
        return arestas;
    }

    public LinkedList<Edge> obterArestasDe(Vertex origem) {
        LinkedList<Edge> resultado = new LinkedList<>();
        Node<Edge> atual = this.arestas.getHead();

        while (atual != null) {
            Edge aresta = atual.getData();
            if (aresta.getSource().equals(origem)) {
                resultado.insert(aresta);
            }
            atual = atual.getNext();
        }

        return resultado;
    }


}
