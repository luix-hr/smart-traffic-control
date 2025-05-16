package org.icev.smarttrafficcontrol.ui;

import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {

    private Graph grafo;
    private Queue<Vehicle> veiculos;

    public MapPanel(Graph grafo, Queue<Vehicle> veiculos) {
        this.grafo = grafo;
        this.veiculos = veiculos;
    }

    public void setGrafo(Graph grafo) {
        this.grafo = grafo;
    }

    public void setVeiculos(Queue<Vehicle> veiculos) {
        this.veiculos = veiculos;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (grafo == null) return;

        g.setColor(Color.LIGHT_GRAY);
        Node<Edge> edgeNode = grafo.getArestas().getHead();
        while (edgeNode != null) {
            Edge edge = edgeNode.getData();
            Point p1 = mapear(edge.getSource());
            Point p2 = mapear(edge.getTarget());
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            edgeNode = edgeNode.getNext();
        }

        Node<Vertex> vertexNode = grafo.getVertices().getHead();
        while (vertexNode != null) {
            Vertex v = vertexNode.getData();
            Point p = mapear(v);

            TrafficLight semaforo = v.getTrafficLight();
            if (semaforo != null) {
                switch (semaforo.getState()) {
                    case RED -> g.setColor(Color.RED);
                    case YELLOW -> g.setColor(Color.YELLOW);
                    case GREEN -> g.setColor(Color.GREEN);
                }
                g.fillOval(p.x - 4, p.y - 4, 8, 8);
            } else {
                g.setColor(Color.BLACK);
                g.fillOval(p.x - 2, p.y - 2, 5, 5);
            }
            vertexNode = vertexNode.getNext();
        }

        if (veiculos != null) {
            g.setColor(Color.BLUE);
            Node<Vehicle> vNode = veiculos.getHead();
            while (vNode != null) {
                Vehicle v = vNode.getData();
                Vertex destino = v.getProximoDestino();
                if (destino != null) {
                    Point p = mapear(destino);
                    g.fillOval(p.x - 4, p.y - 4, 8, 8);
                }
                vNode = vNode.getNext();
            }
        }
    }

    private Point mapear(Vertex v) {
        double lat = v.getLatitude();
        double lon = v.getLongitude();

        int width = getWidth();
        int height = getHeight();

        double minLat = -5.12, maxLat = -5.08;
        double minLon = -42.82, maxLon = -42.79;

        int x = (int) ((lon - minLon) / (maxLon - minLon) * width);
        int y = (int) ((1 - (lat - minLat) / (maxLat - minLat)) * height);

        return new Point(x, y);
    }
}