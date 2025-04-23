package org.icev.smarttrafficcontrol;

import org.icev.smarttrafficcontrol.controller.Simulator;
import org.icev.smarttrafficcontrol.datastructure.Deck;
import org.icev.smarttrafficcontrol.datastructure.Graph;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.model.City;
import org.icev.smarttrafficcontrol.model.Intersection;
import org.icev.smarttrafficcontrol.model.Road;

public class Main {
    public static void main(String[] args) {

        City city = new City("SimCity");

        Intersection a = new Intersection("A");
        Intersection b = new Intersection("B");
        Road road = new Road("A-B", a, b, 1.0);

        a.connectRoad(road);
        b.connectRoad(road);

        city.addIntersection(a);
        city.addIntersection(b);
        city.addRoad(road);

        // Inicializa o simulador
        Simulator simulator = new Simulator(city);

        // Inicia a simulação por 3 ciclos
        simulator.start(3);

        // Pausa a simulação
        simulator.pause();

        // Grava o estado atual da simulação
        simulator.save("simulacao.dat");

        // Carrega o simulador de um arquivo
        Simulator loadedSimulator = Simulator.load("simulacao.dat");

        // Continua a simulação carregada por mais 2 ciclos
        if (loadedSimulator != null) {
            loadedSimulator.start(2);
            loadedSimulator.stop();
        }

    }
}