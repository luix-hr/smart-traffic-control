package org.icev.smarttrafficcontrol.ui;

import org.icev.smarttrafficcontrol.controller.Simulator;
import org.icev.smarttrafficcontrol.service.MapLoader;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.model.SimulationStats;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

public class SimulatorUI extends JFrame {

    private JTextArea logArea;
    private Simulator simulator;
    private JComboBox<String> modeloSemaforo;
    private JTextField ciclosField, veiculosField;
    private MapPanel mapa;

    public SimulatorUI() {
        setTitle("Simulador de Tráfego - Teresina");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de controle
        JPanel controlePanel = new JPanel(new FlowLayout());
        JButton iniciarBtn = new JButton("Iniciar");
        JButton pausarBtn = new JButton("Pausar");
        JButton pararBtn = new JButton("Parar");
        JButton salvarBtn = new JButton("Salvar");

        modeloSemaforo = new JComboBox<>(new String[]{"Modelo 1 (Fixo)", "Modelo 2 (Fila)", "Modelo 3 (Economico)"});
        ciclosField = new JTextField("5", 5);
        veiculosField = new JTextField("3", 5);

        controlePanel.add(new JLabel("Ciclos:"));
        controlePanel.add(ciclosField);
        controlePanel.add(new JLabel("Veiculos/ciclo:"));
        controlePanel.add(veiculosField);
        controlePanel.add(modeloSemaforo);
        controlePanel.add(iniciarBtn);
        controlePanel.add(pausarBtn);
        controlePanel.add(pararBtn);
        controlePanel.add(salvarBtn);

        // Área de log
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(logArea);

        // Painel visual
        mapa = new MapPanel(null, null);
        mapa.setPreferredSize(new Dimension(1000, 400));

        add(controlePanel, BorderLayout.NORTH);
        add(mapa, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        // Ações
        iniciarBtn.addActionListener(e -> iniciarSimulacao());
        pausarBtn.addActionListener(e -> {
            if (simulator != null) simulator.pause();
        });
        pararBtn.addActionListener(e -> {
            if (simulator != null) simulator.stop();
        });
        salvarBtn.addActionListener(e -> {
            if (simulator != null) simulator.save("simulacao_ui.dat");
        });

        setVisible(true);
    }

    private void iniciarSimulacao() {
        try {
            int ciclos = Integer.parseInt(ciclosField.getText());
            int veiculos = Integer.parseInt(veiculosField.getText());

            Graph grafo = MapLoader.carregarJSON("saves/dados/MoradadoSolTeresinaPiauíBrazil.json");
            simulator = new Simulator(grafo, new org.icev.smarttrafficcontrol.model.SimConfig());

            int modelo = modeloSemaforo.getSelectedIndex() + 1;
            simulator.getConfig().setVeiculosPorCiclo(veiculos);
            simulator.getControladorSemaforos().setModelo(modelo);

            mapa.setGrafo(grafo);
            mapa.setVeiculos(simulator.getFilaVeiculos());

            PrintStream consoleOut = System.out;
            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                    logArea.append(String.valueOf((char) b));
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                }
            }));

            new Thread(() -> {
                simulator.start(ciclos, veiculos, modelo);
                SwingUtilities.invokeLater(() -> {
                    mapa.repaint();
                    exibirResumo(simulator.getEstatisticas());
                });
                System.setOut(consoleOut);
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void exibirResumo(SimulationStats stats) {
        String resumo = "=== Estatisticas Finais ===\n" +
                "Total de veiculos: " + stats.getTotalVeiculos() + "\n" +
                String.format("Tempo medio de viagem: %.2f ciclos\n", stats.getTempoMedioViagem()) +
                String.format("Tempo medio de espera: %.2f ciclos\n", stats.getTempoMedioEspera()) +
                String.format("Consumo energetico total: %.2f unidades\n", stats.getConsumoEnergeticoTotal());

        logArea.append("\n" + resumo);
    }
}
