package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;

import java.io.*;

public class Simulator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Graph grafo;
    private VehicleGenerator geradorVeiculos;
    private Queue<Vehicle> filaVeiculos;
    private TrafficLightController controladorSemaforos;
    private transient boolean running;

    public Simulator(Graph grafo) {
        this.grafo = grafo;
        this.geradorVeiculos = new VehicleGenerator(grafo);
        this.filaVeiculos = new Queue<>();
        this.controladorSemaforos = new TrafficLightController(coletarSemaforos());
        this.running = false;
    }

    private LinkedList<TrafficLight> coletarSemaforos() {
        LinkedList<TrafficLight> semaforos = new LinkedList<>();
        Node<Vertex> atual = grafo.getVertices().getHead();
        while (atual != null) {
            TrafficLight s = atual.getData().getTrafficLight();
            if (s != null) {
                semaforos.insert(s);
            }
            atual = atual.getNext();
        }
        return semaforos;
    }

    public void start(int cycles, int veiculosPorCiclo, int modeloSemaforo) {
        running = true;
        for (int i = 0; i < cycles && running; i++) {
            System.out.println("\nCiclo: " + (i + 1));

            controladorSemaforos.update(modeloSemaforo);
            mostrarEstadoSemaforos();
            geradorVeiculos.gerarMultiplosVeiculos(veiculosPorCiclo, filaVeiculos);
            simularMovimentoVeiculos();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void mostrarEstadoSemaforos() {
        LinkedList<TrafficLight> lista = controladorSemaforos.getTrafficLights();
        Node<TrafficLight> atual = lista.getHead();
        while (atual != null) {
            System.out.println(atual.getData());
            atual = atual.getNext();
        }
    }

    private void simularMovimentoVeiculos() {
        while (!filaVeiculos.isEmpty()) {
            Vehicle veiculo = filaVeiculos.dequeue();

            Vertex destino = veiculo.getProximoDestino();
            if (destino != null) {
                TrafficLight semaforo = destino.getTrafficLight();
                if (semaforo == null || semaforo.getState() == TrafficLight.State.GREEN) {
                    System.out.println("Veículo " + veiculo.getId() + " movendo-se para " + destino.getId());
                    veiculo.mover();
                    filaVeiculos.enqueue(veiculo);
                } else {
                    System.out.println("Veículo " + veiculo.getId() + " aguardando sinal verde em " + destino.getId());
                    filaVeiculos.enqueue(veiculo);
                }
            } else {
                System.out.println("Veículo " + veiculo.getId() + " chegou ao destino.");
            }
        }
    }

    public void pause() {
        running = false;
        System.out.println("Simulation paused.");
    }

    public void stop() {
        running = false;
        System.out.println("Simulation stopped.");
    }

    public void save(String filename) {
        File dir = new File("saves");
        if (!dir.exists()) dir.mkdirs();

        if (!filename.endsWith(".dat")) {
            filename += ".dat";
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("saves/" + filename))) {
            out.writeObject(this);
            System.out.println("Simulation saved to saves/" + filename);
        } catch (IOException e) {
            System.out.println("Error saving simulation: " + e.getMessage());
        }
    }

    public static Simulator load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("saves/" + filename))) {
            Simulator sim = (Simulator) in.readObject();
            System.out.println("Simulation loaded from saves/" + filename);
            return sim;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading simulation: " + e.getMessage());
            return null;
        }
    }

    public Graph getGrafo() {
        return grafo;
    }
}