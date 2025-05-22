package org.icev.smarttrafficcontrol.model;

import java.io.Serializable;

public class SimConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private int cicloVerde = 5;
    private int cicloAmarelo = 2;
    private int cicloVermelho = 5;

    private int veiculosPorCiclo = 3;

    private boolean modoPico = false;
    private int horarioAtual = 12;
    private int horaInicioPico = 7;
    private int horaFimPico = 9;

    private boolean modoEconomiaEnergia = false;

    public int getCicloVerde() {
        return cicloVerde;
    }

    public void setCicloVerde(int cicloVerde) {
        this.cicloVerde = cicloVerde;
    }

    public int getCicloAmarelo() {
        return cicloAmarelo;
    }

    public void setCicloAmarelo(int cicloAmarelo) {
        this.cicloAmarelo = cicloAmarelo;
    }

    public int getCicloVermelho() {
        return cicloVermelho;
    }

    public void setCicloVermelho(int cicloVermelho) {
        this.cicloVermelho = cicloVermelho;
    }

    public int getVeiculosPorCiclo() {
        return veiculosPorCiclo;
    }

    public void setVeiculosPorCiclo(int veiculosPorCiclo) {
        this.veiculosPorCiclo = veiculosPorCiclo;
    }

    public boolean isModoPico() {
        return modoPico;
    }

    public void setModoPico(boolean modoPico) {
        this.modoPico = modoPico;
    }

    public int getHorarioAtual() {
        return horarioAtual;
    }

    public void setHorarioAtual(int horarioAtual) {
        this.horarioAtual = horarioAtual;
    }

    public int getHoraInicioPico() {
        return horaInicioPico;
    }

    public void setHoraInicioPico(int horaInicioPico) {
        this.horaInicioPico = horaInicioPico;
    }

    public int getHoraFimPico() {
        return horaFimPico;
    }

    public void setHoraFimPico(int horaFimPico) {
        this.horaFimPico = horaFimPico;
    }

    public boolean isModoEconomiaEnergia() {
        return modoEconomiaEnergia;
    }

    public void setModoEconomiaEnergia(boolean modoEconomiaEnergia) {
        this.modoEconomiaEnergia = modoEconomiaEnergia;
    }

    public boolean isHorarioDePico() {
        return modoPico && horarioAtual >= horaInicioPico && horarioAtual <= horaFimPico;
    }
}
