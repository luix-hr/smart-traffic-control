package org.icev.smarttrafficcontrol;

import org.icev.smarttrafficcontrol.controller.Simulator;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.service.MapLoader;
import org.icev.smarttrafficcontrol.ui.SimulatorUI;

import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulatorUI());
    }


    public static void main2() {

        SimConfig config = new SimConfig();
        config.setCicloVerde(5);
        config.setCicloAmarelo(2);
        config.setCicloVermelho(5);
        config.setModoPico(false);
        config.setHorarioAtual(14);

        Scanner scanner = new Scanner(System.in);
        Simulator simulator = null;
        boolean carregado = false;

        while (true) {
            System.out.println("\n===== MENU SIMULADOR DE TRÂNSITO =====");
            System.out.println("1. Carregar mapa e iniciar simulação");
            System.out.println("2. Carregar simulação salva");
            System.out.println("3. Pausar simulação");
            System.out.println("4. Parar simulação");
            System.out.println("5. Salvar simulação");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    String caminho = "saves/dados/MoradadoSolTeresinaPiauíBrazil.json";
                    Graph grafo = MapLoader.carregarJSON(caminho);
                    simulator = new Simulator(grafo, config);
                    carregado = true;

                    // Solicita heurística do semáforo
                    System.out.println("\nEscolha o modelo de controle de semáforo:");
                    System.out.println("1 - Ciclo fixo (Modelo 1)");
                    System.out.println("2 - Baseado em fila (Modelo 2)");
                    System.out.println("3 - Baseado em consumo/horário (Modelo 3)");
                    System.out.print("Modelo: ");
                    int modelo = scanner.nextInt();

                    System.out.print("Quantos ciclos? ");
                    int ciclos = scanner.nextInt();
                    System.out.print("Veículos por ciclo? ");
                    int veiculosPorCiclo = scanner.nextInt();
                    config.setVeiculosPorCiclo(veiculosPorCiclo);

                    simulator.start(ciclos, veiculosPorCiclo, modelo);
                    break;

                case 2:
                    System.out.print("Digite o nome do arquivo salvo: ");
                    String arquivo = scanner.nextLine();
                    simulator = Simulator.load(arquivo);
                    carregado = simulator != null;
                    break;

                case 3:
                    if (carregado) simulator.pause();
                    else System.out.println("Nenhuma simulação carregada.");
                    break;

                case 4:
                    if (carregado) simulator.stop();
                    else System.out.println("Nenhuma simulação carregada.");
                    break;

                case 5:
                    if (carregado) {
                        System.out.print("Digite o nome do arquivo para salvar: ");
                        String nomeArquivo = scanner.nextLine();
                        simulator.save(nomeArquivo);
                    } else {
                        System.out.println("Nenhuma simulação carregada.");
                    }
                    break;

                case 6:
                    System.out.println("Encerrando simulação...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
}
