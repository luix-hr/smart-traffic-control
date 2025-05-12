package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;

import java.io.*;

public class Simulator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Graph grafo;
    private transient boolean running; // Não serializamos estado de execução

    public Simulator(Graph grafo) {
        this.grafo = grafo;
        this.running = false;
    }

    public void start(int cycles) {
        running = true;
        for (int i = 0; i < cycles && running; i++) {
            System.out.println("Cycle: " + (i + 1));

            // Lógica simulada de "atualizar semáforos"
            updateTrafficLights();

            // Simulação de movimentação de veículos
            simulateVehicles();

            try {
                Thread.sleep(1000); // Simula tempo real
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateTrafficLights() {
        // Aqui você pode incluir regras de alternância de semáforos
        System.out.println("Atualizando semáforos... (simulado)");
    }

    private void simulateVehicles() {
        System.out.println("Simulando movimentação de veículos:");
        for (Vertex origem : grafo.getVertices()) {
            for (Edge aresta : grafo.obterArestasDe(origem)) {
                System.out.println("De " + origem.getId() + " para " + aresta.getDestino().getId() +
                        " (Distância: " + aresta.getPeso() + ")");
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

