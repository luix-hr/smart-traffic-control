package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.SimulationStats;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;

import java.io.*;

public class Simulator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Graph grafo;
    private VehicleGenerator geradorVeiculos;
    private Queue<Vehicle> filaVeiculos;
    private TrafficLightController controladorSemaforos;
    private SimulationStats stats;
    private SimConfig config;
    private transient boolean running;

    public Simulator(Graph grafo, SimConfig config) {
        this.grafo = grafo;
        this.config = config;
        this.geradorVeiculos = new VehicleGenerator(grafo);
        this.filaVeiculos = new Queue<>();
        this.controladorSemaforos = new TrafficLightController(coletarSemaforos(), config);
        this.stats = new SimulationStats();
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
            geradorVeiculos.gerarMultiplosVeiculos(config.getVeiculosPorCiclo(), filaVeiculos);
            simularMovimentoVeiculos();

            stats.printCiclo(i + 1);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        stats.imprimirResumo();
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
        Queue<Vehicle> proximaFila = new Queue<>();

        System.out.println("=== Simulação de Movimento de Veículos ===");
        while (!filaVeiculos.isEmpty()) {
            Vehicle veiculo = filaVeiculos.dequeue();
            veiculo.incrementarTempo();

            Vertex destino = veiculo.getProximoDestino();

            if (destino != null) {
                TrafficLight semaforo = destino.getTrafficLight();

                if (semaforo == null) {
                    System.out.println("Veículo " + veiculo.getId() + " movendo-se para " + destino.getId() + " (sem semáforo)");
                    veiculo.mover();
                    proximaFila.enqueue(veiculo);
                } else {
                    System.out.println("Veículo " + veiculo.getId() + " enfrentando semáforo em " + destino.getId() + " - Estado: " + semaforo.getState());

                    if (semaforo.getState() == TrafficLight.State.GREEN) {
                        System.out.println("Veículo " + veiculo.getId() + " movendo-se para " + destino.getId());
                        veiculo.mover();
                        proximaFila.enqueue(veiculo);
                    } else {
                        System.out.println("Veículo " + veiculo.getId() + " parado em semáforo " + destino.getId());
                        veiculo.incrementarEspera();
                        destino.adicionarNaFila(veiculo);
                    }
                }
            } else {
                System.out.println("Veículo " + veiculo.getId() + " chegou ao destino após " + veiculo.getTempoTotal() + " ciclos, com " + veiculo.getTempoParado() + " ciclos parado.");
                stats.registrarViagem(veiculo.getTempoTotal(), veiculo.getTempoParado());
            }
        }

        System.out.println("=== Liberando veículos das filas nos semáforos verdes ===");
        Node<Vertex> verticeNode = grafo.getVertices().getHead();
        while (verticeNode != null) {
            Vertex v = verticeNode.getData();
            TrafficLight tf = v.getTrafficLight();

            if (tf != null && tf.getState() == TrafficLight.State.GREEN && v.temFila()) {
                Vehicle liberado = v.removerDaFila();
                if (liberado != null) {
                    System.out.println("Veículo " + liberado.getId() + " liberado do semáforo em " + v.getId());
                    proximaFila.enqueue(liberado);
                }
            }
            verticeNode = verticeNode.getNext();
        }

        filaVeiculos = proximaFila;
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

    public SimulationStats getEstatisticas() {
        return stats;
    }

    public SimConfig getConfig() {
        return config;
    }

    public Queue<Vehicle> getFilaVeiculos() {
        return filaVeiculos;
    }

    public TrafficLightController getControladorSemaforos() {
        return controladorSemaforos;
    }
}
