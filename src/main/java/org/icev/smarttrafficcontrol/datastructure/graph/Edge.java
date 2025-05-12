package org.icev.smarttrafficcontrol.datastructure.graph;

public class Edge<T> {
    private Vertex origem;
    private Vertex destino;
    private double custo;

    public Edge(Vertex origem, Vertex destino, double custo) {
        this.origem = origem;
        this.destino = destino;
        this.custo = custo;
    }

    public Vertex getOrigem() {
        return origem;
    }

    public Vertex getDestino() {
        return destino;
    }

    public double getCusto() {
        return custo;
    }
}
