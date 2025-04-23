package org.icev.smarttrafficcontrol.controller;

import org.icev.smarttrafficcontrol.model.City;

import java.io.*;

public class Simulator implements Serializable {
    private static final long serialVersionUID = 1L;

    private City city;
    private TrafficLightController trafficLightController;
    private VehicleGenerator vehicleGenerator;
    private boolean running;

    public Simulator(City city) {
        this.city = city;
        this.trafficLightController = new TrafficLightController(city.getIntersections());
        this.vehicleGenerator = new VehicleGenerator(city.getRoads());
        this.running = false;
    }

    public void start(int cycles) {
        running = true;
        for (int i = 0; i < cycles && running; i++) {
            System.out.println("Cycle: " + (i + 1));
            trafficLightController.update();
            vehicleGenerator.generate();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
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
        if (!dir.exists()) dir.mkdirs(); // Cria a pasta se não existir

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
}

