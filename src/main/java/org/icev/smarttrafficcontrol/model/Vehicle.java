package org.icev.smarttrafficcontrol.model;

public class Vehicle {
    private String placa;
    private double speed;

    public Vehicle(String placa, double speed) {
        this.placa = placa;
        this.speed = speed;
    }

    public String getPlaca() {
        return placa;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "plate='" + placa + '\'' +
                ", speed=" + speed +
                '}';
    }
}
