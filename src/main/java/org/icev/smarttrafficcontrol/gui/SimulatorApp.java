package org.icev.smarttrafficcontrol.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.icev.smarttrafficcontrol.controller.VehicleGenerator;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.Queue;
import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.icev.smarttrafficcontrol.model.Vehicle;
import org.icev.smarttrafficcontrol.service.MapLoader;


import java.util.ArrayList;
import java.util.List;

public class SimulatorApp extends Application {

    private Graph grafo;
    private Canvas canvas;
    private Timeline timeline;
    private List<Vehicle> veiculos = new ArrayList<>();
    private VehicleGenerator generator;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulador de Trânsito");

        // Carrega o grafo do JSON
        grafo = MapLoader.carregarJSON("saves/dados/MoradadoSolTeresinaPiauíBrazil.json");
        generator = new VehicleGenerator(grafo);

        canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Button btnIniciar = new Button("Iniciar Simulação");
        btnIniciar.setOnAction(e -> iniciarSimulacao(gc));

        HBox controle = new HBox(10);
        controle.getChildren().addAll(btnIniciar);

        BorderPane root = new BorderPane();
        root.setTop(controle);
        root.setCenter(canvas);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void iniciarSimulacao(GraphicsContext gc) {
        // Gera 5 veículos
        Queue<Vehicle> fila = new Queue<>();
        generator.gerarMultiplosVeiculos( 5, fila);
        while (!fila.isEmpty()) {
            veiculos.add(fila.dequeue());
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> atualizarSimulacao(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void atualizarSimulacao(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double escalaLat = 5000;
        double escalaLon = 5000;
        double offsetX = 300;
        double offsetY = 300;

        // Desenha arestas
        Node<Edge> arestaAtual = grafo.getArestas().getHead();
        gc.setStroke(Color.DARKGRAY);
        while (arestaAtual != null) {
            Edge e = arestaAtual.getData();
            Vertex origem = e.getSource();
            Vertex destino = e.getTarget();

            double x1 = origem.getLongitude() * escalaLon + offsetX;
            double y1 = -origem.getLatitude() * escalaLat + offsetY;
            double x2 = destino.getLongitude() * escalaLon + offsetX;
            double y2 = -destino.getLatitude() * escalaLat + offsetY;

            gc.strokeLine(x1, y1, x2, y2);
            arestaAtual = arestaAtual.getNext();
        }

        // Desenha vértices (com cor de semáforo)
        Node<Vertex> verticeAtual = grafo.getVertices().getHead();
        while (verticeAtual != null) {
            Vertex v = verticeAtual.getData();
            double x = v.getLongitude() * escalaLon + offsetX;
            double y = -v.getLatitude() * escalaLat + offsetY;

            TrafficLight tf = v.getTrafficLight();
            if (tf != null) {
                switch (tf.getEstado()) {
                    case GREEN -> gc.setFill(Color.LIME);
                    case YELLOW -> gc.setFill(Color.GOLD);
                    case RED -> gc.setFill(Color.RED);
                }
            } else {
                gc.setFill(Color.GRAY);
            }

            gc.fillOval(x - 3, y - 3, 6, 6);
            verticeAtual = verticeAtual.getNext();
        }

        // Move veículos
        for (Vehicle v : veiculos) {
            v.mover();
        }

        // Desenha veículos
        gc.setFill(Color.BLUE);
        for (Vehicle v : veiculos) {
            Vertex atual = v.getProximoDestino();
            if (atual != null) {
                double x = atual.getLongitude() * escalaLon + offsetX;
                double y = -atual.getLatitude() * escalaLat + offsetY;
                gc.fillOval(x - 2, y - 2, 4, 4);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
