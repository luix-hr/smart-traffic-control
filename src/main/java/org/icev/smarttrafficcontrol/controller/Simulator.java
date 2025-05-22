package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.SimulationStats;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;
import org.icev.smarttrafficcontrol.gui.SimulatorUI;

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
    private transient SimulatorUI ui;
    private LinkedList<IntersectionController> intersecoes;

    public Simulator(Graph grafo, SimConfig config, LinkedList<IntersectionController> intersecoes, String caminhoMapa) {
        this.grafo = grafo;
        this.config = config;
        this.intersecoes = intersecoes;
        this.geradorVeiculos = new VehicleGenerator(grafo);
        this.filaVeiculos = new Queue<>();
        this.controladorSemaforos = new TrafficLightController(intersecoes, config);
        this.stats = new SimulationStats();
        this.running = false;
        this.ui = new SimulatorUI(grafo, intersecoes, this);
    }

    public void setModeloSemaforo(int modelo) {
        this.controladorSemaforos.setModelo(modelo);
    }

    public void start(int cycles, int veiculosPorCiclo, int modelo) {
        running = true;
        geradorVeiculos.gerarMultiplosVeiculos(config.getVeiculosPorCiclo(), filaVeiculos);

        for (int i = 0; i < cycles && running; i++) {
            System.out.println("\nCiclo: " + (i + 1));

            controladorSemaforos.update(filaVeiculos);
            ui.repaintMapa();
            mostrarEstadoSemaforos();

            simularMovimentoVeiculos();
            stats.printCiclo(i + 1);

            LinkedList<Vehicle> listaVeiculos = new LinkedList<>();
            Queue<Vehicle> temp = new Queue<>();

            while (!filaVeiculos.isEmpty()) {
                Vehicle v = filaVeiculos.dequeue();
                listaVeiculos.insert(v);
                temp.enqueue(v);
            }
            filaVeiculos = temp;
            ui.atualizar(listaVeiculos, intersecoes);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        stats.imprimirResumo();
    }

    private void mostrarEstadoSemaforos() {
        LinkedList<TrafficLight> lista = controladorSemaforos.getTodosSemaforos();
        Node<TrafficLight> atual = lista.getHead();
        while (atual != null) {
            System.out.println(atual.getData());
            atual = atual.getNext();
        }
    }

    private void simularMovimentoVeiculos() {
        Queue<Vehicle> proximaFila = new Queue<>();

        while (!filaVeiculos.isEmpty()) {
            Vehicle veiculo = filaVeiculos.dequeue();
            Vertex destino = veiculo.getProximoDestino();

            if (destino != null) {
                if (podeAvancar(destino)) {
                    System.out.println("Veículo " + veiculo.getId() + " movendo-se para " + destino.getId());
                    veiculo.mover();
                } else {
                    System.out.println("Veículo " + veiculo.getId() + " aguardando sinal verde em " + destino.getId());
                    veiculo.incrementarEspera();
                    veiculo.incrementarTempo();
                }
                proximaFila.enqueue(veiculo);
            } else {
                System.out.println("Veículo " + veiculo.getId() + " chegou ao destino.");
                stats.registrarViagem(veiculo.getTempoTotal(), veiculo.getTempoParado());
            }
        }

        filaVeiculos = proximaFila;
    }


    private boolean podeAvancar(Vertex destino) {
        Node<IntersectionController> atual = intersecoes.getHead();

        while (atual != null) {
            IntersectionController intersec = atual.getData();
            LinkedList<TrafficLight> semaforos = intersec.getTodosSemaforos();
            Node<TrafficLight> node = semaforos.getHead();

            while (node != null) {
                TrafficLight tl = node.getData();
                if (tl.getVinculo().equals(destino)) {
                    return tl.getState() == TrafficLight.State.GREEN;
                }
                node = node.getNext();
            }
            atual = atual.getNext();
        }

        return true;
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

    public void setUI(SimulatorUI ui) {
        this.ui = ui;
    }
}
