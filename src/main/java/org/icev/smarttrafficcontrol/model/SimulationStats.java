package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;
import org.icev.smarttrafficcontrol.datastructure.*;

public class SimulationStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalVeiculos;
    private double tempoTotalViagem;
    private double tempoTotalEspera;
    private double consumoEnergetico;
    private int veiculosComEsperaAlta;

    private LinkedList<Double> viagens;
    private LinkedList<Double> esperas;

    public SimulationStats() {
        viagens = new LinkedList<>();
        esperas = new LinkedList<>();
    }

    public void registrarViagem(double tempoViagem, double tempoEspera) {
        totalVeiculos++;
        tempoTotalViagem += tempoViagem;
        tempoTotalEspera += tempoEspera;
        consumoEnergetico += tempoEspera * 0.5;

        viagens.insert(tempoViagem);
        esperas.insert(tempoEspera);
        if (tempoEspera > 3) veiculosComEsperaAlta++;
    }

    public double getIndiceCongestionamento() {
        if (totalVeiculos == 0) return 0;
        return (double) veiculosComEsperaAlta / totalVeiculos;
    }

    public int getTotalVeiculos() {
        return totalVeiculos;
    }

    public double getTempoMedioViagem() {
        if (viagens.isEmpty()) return 0;
        double soma = 0;
        Node<Double> atual = viagens.getHead();
        while (atual != null) {
            soma += atual.getData();
            atual = atual.getNext();
        }
        return soma / viagens.getSize();
    }

    public double getTempoMedioEspera() {
        if (esperas.isEmpty()) return 0;
        double soma = 0;
        Node<Double> atual = esperas.getHead();
        while (atual != null) {
            soma += atual.getData();
            atual = atual.getNext();
        }
        return soma / esperas.getSize();
    }

    public double getConsumoEnergeticoTotal() {
        return consumoEnergetico;
    }

    public void imprimirResumo() {
        System.out.println("======== ESTATÍSTICAS DA SIMULAÇÃO ========");
        System.out.println("Total de veículos: " + totalVeiculos);
        System.out.printf("Tempo médio de viagem: %.2f ciclos\n", getTempoMedioViagem());
        System.out.printf("Tempo médio de espera: %.2f ciclos\n", getTempoMedioEspera());
        System.out.printf("Consumo energético total: %.2f unidades\n", consumoEnergetico);
        System.out.printf("Índice de congestionamento: %.2f%%\n", getIndiceCongestionamento() * 100);
        System.out.println("===========================================");
    }

    public void resetar() {
        totalVeiculos = 0;
        tempoTotalViagem = 0;
        tempoTotalEspera = 0;
        consumoEnergetico = 0;
        viagens = new LinkedList<>();
        esperas = new LinkedList<>();
    }

    public void printCiclo(int ciclo) {
        System.out.printf("[Ciclo %d] Média viagem: %.2f | Média espera: %.2f | Energia: %.2f\n",
                ciclo, getTempoMedioViagem(), getTempoMedioEspera(), consumoEnergetico);
    }
}
