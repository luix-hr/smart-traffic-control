package org.icev.smarttrafficcontrol.datastructure.graph;

import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.model.Vehicle;

import java.io.Serializable;

public class Vertex implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private double latitude;
    private double longitude;
    private transient Queue<Vehicle> filaEspera = new Queue<>();
    private TrafficLight trafficLight;

    public Vertex(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }

    public void setTrafficLight(TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
    }

    public Queue<Vehicle> getFilaEspera() {
        return filaEspera;
    }

    public void adicionarNaFila(Vehicle veiculo) {
        filaEspera.enqueue(veiculo);
    }

    public Vehicle removerDaFila() {
        return filaEspera.isEmpty() ? null : filaEspera.dequeue();
    }

    public int getTamanhoFilaEspera() {
        return filaEspera.getSize();
    }

    public boolean temFila() {
        return !filaEspera.isEmpty();
    }

    @Override
    public String toString() {
        return id + "(" + latitude + ", " + longitude + ")";
    }
}
