package org.icev.smarttrafficcontrol.gui;

import org.icev.smarttrafficcontrol.controller.IntersectionController;
import org.icev.smarttrafficcontrol.controller.Simulator;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;
import org.icev.smarttrafficcontrol.service.MapLoader;

import javax.swing.*;
import java.awt.*;

public class SimulatorUI extends JFrame {
    private Graph grafo;
    private LinkedList<IntersectionController> intersecoes;
    private Simulator simulator;

    private JPanel painelMapa;
    private JTextArea areaVeiculos;
    private JComboBox<String> modeloCombo;
    private JTextField campoVeiculos;
    private JTextField campoCiclo;
    private JButton iniciarBtn, pausarBtn, pararBtn, salvarBtn, carregarBtn;

    private LinkedList<Vehicle> listaVeiculos = new LinkedList<>();

    private double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
    private double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

    public SimulatorUI(Graph grafo, LinkedList<IntersectionController> intersecoes, Simulator simulator) {
        this.simulator = simulator;
        this.grafo = grafo;
        this.intersecoes = intersecoes;
        calcularLimitesMapa();
        construirUI();
    }

    private void construirUI() {
        setTitle("Simulador de Trânsito Visual");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setVisible(true);

        painelMapa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharMapa((Graphics2D) g);
            }
        };
        painelMapa.setPreferredSize(new Dimension(700, 700));
        add(painelMapa, BorderLayout.CENTER);

        JPanel painelDireito = new JPanel(new BorderLayout());

        JPanel painelControles = new JPanel(new FlowLayout());
        painelControles.add(new JLabel("Modelo:"));
        modeloCombo = new JComboBox<>(new String[]{"1 - Fixo", "2 - Fila", "3 - Pico"});
        painelControles.add(modeloCombo);

        painelControles.add(new JLabel("Ciclos:"));
        campoCiclo = new JTextField("10",4);
        painelControles.add(campoCiclo);

        painelControles.add(new JLabel("Veículos/ciclo:"));
        campoVeiculos = new JTextField("10", 4);
        painelControles.add(campoVeiculos);

        iniciarBtn = new JButton("Iniciar");
        pausarBtn = new JButton("Pausar");
        pararBtn = new JButton("Parar");
        salvarBtn = new JButton("Salvar");
        carregarBtn = new JButton("Carregar");

        painelControles.add(iniciarBtn);
        painelControles.add(pausarBtn);
        painelControles.add(pararBtn);
        painelControles.add(salvarBtn);
        painelControles.add(carregarBtn);

        painelDireito.add(painelControles, BorderLayout.NORTH);

        areaVeiculos = new JTextArea();
        areaVeiculos.setEditable(false);
        JScrollPane scrollVeiculos = new JScrollPane(areaVeiculos);
        scrollVeiculos.setPreferredSize(new Dimension(300, 700));
        painelDireito.add(scrollVeiculos, BorderLayout.CENTER);

        add(painelDireito, BorderLayout.EAST);

        iniciarBtn.addActionListener(e -> iniciarSimulacao());
        pausarBtn.addActionListener(e -> simulator.pause());
        pararBtn.addActionListener(e -> simulator.stop());

        salvarBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("saves");
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                simulator.save(chooser.getSelectedFile().getName());
            }
        });

        carregarBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("saves");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                Simulator sim = Simulator.load(chooser.getSelectedFile().getName());
                if (sim != null) {
                    this.simulator = sim;
                    JOptionPane.showMessageDialog(this, "Simulação carregada!");
                }
            }
        });
    }

    private void calcularLimitesMapa() {
        Node<Vertex> v = grafo.getVertices().getHead();
        while (v != null) {
            Vertex vertice = v.getData();
            double lat = vertice.getLatitude();
            double lon = vertice.getLongitude();
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;
            v = v.getNext();
        }
    }

    private Point mapear(Vertex v) {
        int largura = painelMapa.getWidth() - 100;
        int altura = painelMapa.getHeight() - 100;

        double lon = v.getLongitude();
        double lat = v.getLatitude();

        double x = (lon - minLon) / (maxLon - minLon) * largura + 50;
        double y = (maxLat - lat) / (maxLat - minLat) * altura + 50;

        return new Point((int) x, (int) y);
    }

    private void desenharMapa(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);
        Node<Edge> e = grafo.getArestas().getHead();
        while (e != null) {
            Vertex v1 = e.getData().getSource();
            Vertex v2 = e.getData().getTarget();
            Point p1 = mapear(v1);
            Point p2 = mapear(v2);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            e = e.getNext();
        }

        Node<Vertex> v = grafo.getVertices().getHead();
        while (v != null) {
            Vertex vertex = v.getData();
            Point p = mapear(vertex);
            TrafficLight tl = vertex.getTrafficLight();
            if (tl != null) {
                switch (tl.getState()) {
                    case GREEN -> g.setColor(Color.GREEN);
                    case YELLOW -> g.setColor(Color.YELLOW);
                    case RED -> g.setColor(Color.RED);
                }
            } else {
                g.setColor(Color.GRAY);
            }
            g.fillOval(p.x - 4, p.y - 4, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(vertex.getId(), p.x + 6, p.y);
            v = v.getNext();
        }

        g.setColor(Color.BLUE);
        Node<Vehicle> veiculoNode = listaVeiculos.getHead();
        while (veiculoNode != null) {
            Vehicle veiculo = veiculoNode.getData();
            Vertex destino = veiculo.getProximoDestino();
            if (destino != null) {
                Point p = mapear(destino);
                g.fillOval(p.x - 6, p.y - 6, 12, 12);
            }
            veiculoNode = veiculoNode.getNext();
        }
    }

    private void iniciarSimulacao() {
        int modelo = modeloCombo.getSelectedIndex() + 1;
        simulator.setModeloSemaforo(modelo);
        int veiculosPorCiclo = Integer.parseInt(campoVeiculos.getText());
        simulator.getConfig().setVeiculosPorCiclo(veiculosPorCiclo);
        new Thread(() -> simulator.start(9999, veiculosPorCiclo, modelo)).start();
    }

    public void atualizar(LinkedList<Vehicle> veiculos, LinkedList<IntersectionController> intersecoes) {
        this.listaVeiculos = veiculos;
        atualizarPainelVeiculos();
        painelMapa.repaint();
    }

    private void atualizarPainelVeiculos() {
        StringBuilder sb = new StringBuilder();
        Node<Vehicle> v = listaVeiculos.getHead();
        while (v != null) {
            Vehicle veiculo = v.getData();
            sb.append(veiculo.getId()).append(" -> destino atual: ");
            Vertex d = veiculo.getProximoDestino();
            sb.append(d != null ? d.getId() : "chegou ao destino").append("\n");
            v = v.getNext();
        }
        areaVeiculos.setText(sb.toString());
    }

    public void repaintMapa() {
        painelMapa.repaint();
    }
}
