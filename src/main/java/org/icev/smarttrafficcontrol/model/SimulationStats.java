package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;
import org.icev.smarttrafficcontrol.datastructure.*;
import org.icev.smarttrafficcontrol.gui.SimulatorUI;

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
        if (tempoEspera > 20) veiculosComEsperaAlta++;
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

    public void imprimirResumo(SimulatorUI ui) {
        String resumo = "\n======== ESTATISTICAS DA SIMULACAO ========\n" +
                "Total de viagens concluídas: " + totalVeiculos + "\n" +
                "Tempo medio de viagem: " + String.format("%.2f", getTempoMedioViagem()) + "\n" +
                "Tempo medio de espera: " + String.format("%.2f", getTempoMedioEspera()) + "\n" +
                "Indice de congestionamento: " + String.format("%.2f", getIndiceCongestionamento() * 100) + "\n" +
                "Consumo energetico total: " + String.format("%.2f", consumoEnergetico) + "\n";

        System.out.println(resumo);
            if (ui != null) {
                ui.log(resumo);
            }


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
        System.out.printf("[Ciclo %d] Media viagem: %.2f | Media espera: %.2f | Energia: %.2f\n",
                ciclo, getTempoMedioViagem(), getTempoMedioEspera(), consumoEnergetico);
    }
}
