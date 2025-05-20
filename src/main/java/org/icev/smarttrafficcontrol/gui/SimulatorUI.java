package org.icev.smarttrafficcontrol.gui;

import org.icev.smarttrafficcontrol.controller.IntersectionController;
import org.icev.smarttrafficcontrol.controller.Simulator;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;
import org.icev.smarttrafficcontrol.service.MapLoader;

import javax.swing.*;
import java.awt.*;


public class SimulatorUI extends JFrame {
    private Graph grafo;
    private LinkedList<IntersectionController> intersecoes;
    private Simulator simulator;
    private SimConfig config;

    private JPanel painelMapa;
    private JTextArea areaVeiculos;
    private JComboBox<String> modeloCombo;
    private JTextField campoVeiculos;
    private JButton iniciarBtn, pausarBtn, pararBtn, salvarBtn, carregarBtn;

    private Queue<Vehicle> listaVeiculos = new Queue<>();

    public SimulatorUI(String caminhoMapa) {
        config = new SimConfig();
        config.setCicloVerde(5);
        config.setCicloAmarelo(2);
        config.setCicloVermelho(5);
        config.setVeiculosPorCiclo(10);
        config.setModoPico(false);
        config.setHorarioAtual(14);

        this.grafo = MapLoader.carregarJSON(caminhoMapa);
        this.intersecoes = MapLoader.criarIntersecoesAutomaticas(grafo, config);
        this.simulator = new Simulator(grafo, config, intersecoes,caminhoMapa);

        construirUI();
    }

    private void construirUI() {
        setTitle("Simulador de Trânsito Visual");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel mapa
        painelMapa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharMapa((Graphics2D) g);
            }
        };
        painelMapa.setPreferredSize(new Dimension(800, 700));
        add(painelMapa, BorderLayout.CENTER);

        // Painel lateral com controles e status
        JPanel painelDireito = new JPanel(new BorderLayout());

        // Controles
        JPanel painelControles = new JPanel(new FlowLayout());
        painelControles.add(new JLabel("Modelo:"));
        modeloCombo = new JComboBox<>(new String[]{"1 - Fixo", "2 - Fila", "3 - Pico"});
        painelControles.add(modeloCombo);

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

        // Área veículos
        areaVeiculos = new JTextArea();
        areaVeiculos.setEditable(false);
        JScrollPane scrollVeiculos = new JScrollPane(areaVeiculos);
        painelDireito.add(scrollVeiculos, BorderLayout.CENTER);

        add(painelDireito, BorderLayout.EAST);

        // Ações dos botões
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

        setLocationRelativeTo(null);
        setVisible(true);
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
        for (Vehicle veiculo : listaVeiculos) {
            Vertex destino = veiculo.getProximoDestino();
            if (destino != null) {
                Point p = mapear(destino);
                g.fillOval(p.x - 6, p.y - 6, 12, 12);
            }
        }
    }

    private Point mapear(Vertex v) {
        double x = (v.getLongitude() + 1) * 300;
        double y = (v.getLatitude() + 1) * 300;
        return new Point((int) x, (int) y);
    }

    private void iniciarSimulacao() {
        int modelo = modeloCombo.getSelectedIndex() + 1;
        simulator.setModeloSemaforo(modelo);
        int veiculosPorCiclo = Integer.parseInt(campoVeiculos.getText());
        config.setVeiculosPorCiclo(veiculosPorCiclo);

        new Thread(() -> simulator.start(9999, veiculosPorCiclo, modelo)).start();
    }

    public void atualizar(Queue<Vehicle> veiculos, LinkedList<IntersectionController> intersecoes){
        this.listaVeiculos = veiculos;
        atualizarPainelVeiculos();
        painelMapa.repaint();
    }

    private void atualizarPainelVeiculos() {
        StringBuilder sb = new StringBuilder();
        for (Vehicle v : listaVeiculos) {
            sb.append(v.getId()).append(" -> destino atual: ");
            Vertex d = v.getProximoDestino();
            sb.append(d != null ? d.getId() : "chegou ao destino").append("\n");
        }
        areaVeiculos.setText(sb.toString());
    }
}
