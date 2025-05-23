package org.icev.smarttrafficcontrol.service;

import org.icev.smarttrafficcontrol.controller.IntersectionController;
import org.icev.smarttrafficcontrol.datastructure.LinkedList;
import org.icev.smarttrafficcontrol.datastructure.Node;
import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.model.SimConfig;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MapLoader {

    public static Graph carregarJSON(String caminhoArquivo) {
        Graph grafo = new Graph();
        Map<String, Vertex> mapaVertices = new HashMap<>();
        Set<String> verticesComSemaforo = new HashSet<>();

        try {
            String jsonContent = Files.readString(Paths.get(caminhoArquivo));
            JSONObject root = new JSONObject(jsonContent);

            JSONArray trafficLightsArray = root.getJSONArray("traffic_lights");
            for (int i = 0; i < trafficLightsArray.length(); i++) {
                String idSemaforo = trafficLightsArray.getJSONObject(i).getString("id");
                verticesComSemaforo.add(idSemaforo);
            }

            JSONArray nodes = root.getJSONArray("nodes");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String id = node.getString("id");
                double lat = node.getDouble("latitude");
                double lon = node.getDouble("longitude");

                Vertex vertice = new Vertex(id, lat, lon);

                if (verticesComSemaforo.contains(id)) {
                    TrafficLight semaforo = new TrafficLight(id);
                    semaforo.setVinculo(vertice);
                    vertice.setTrafficLight(semaforo);
                }

                grafo.adicionarVertice(vertice);
                mapaVertices.put(id, vertice);
            }

            JSONArray edges = root.getJSONArray("edges");
            for (int i = 0; i < edges.length(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                String id = edge.getString("id");
                String origemId = edge.getString("source");
                String destinoId = edge.getString("target");
                double length = edge.getDouble("length");
                double maxspeed = edge.has("maxspeed") ? edge.getDouble("maxspeed") : 30.0;

                Vertex origem = mapaVertices.get(origemId);
                Vertex destino = mapaVertices.get(destinoId);

                if (origem != null && destino != null) {
                    Edge aresta = new Edge(id, origem, destino, length, maxspeed);
                    grafo.adicionarAresta(aresta);
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Erro ao ler o arquivo JSON: " + e.getMessage());
        }

        return grafo;
    }

    public static LinkedList<IntersectionController> criarIntersecoesAutomaticas(Graph grafo, SimConfig config) {
        LinkedList<IntersectionController> intersecoes = new LinkedList<>();
        Set<String> verticesProcessados = new HashSet<>();

        Node<Vertex> atual = grafo.getVertices().getHead();

        while (atual != null) {
            Vertex centro = atual.getData();

            if (centro.getTrafficLight() != null && !verticesProcessados.contains(centro.getId())) {
                LinkedList<TrafficLight> grupo1 = new LinkedList<>();
                LinkedList<TrafficLight> grupo2 = new LinkedList<>();

                TrafficLight semaforoCentro = new TrafficLight("TL_" + centro.getId() + "_CENTRO");
                semaforoCentro.setVinculo(centro);
                grupo1.insert(semaforoCentro);

                Set<String> usados = new HashSet<>();
                usados.add(centro.getId());

                Node<Edge> arestaAtual = grafo.getArestas().getHead();
                while (arestaAtual != null) {
                    Edge e = arestaAtual.getData();
                    Vertex vizinho = null;
                    if (e.getSource().equals(centro)) vizinho = e.getTarget();
                    else if (e.getTarget().equals(centro)) vizinho = e.getSource();

                    if (vizinho != null && vizinho.getTrafficLight() != null && !usados.contains(vizinho.getId())) {
                        TrafficLight novoSemaforo = new TrafficLight("TL_" + centro.getId() + "_" + vizinho.getId());
                        novoSemaforo.setVinculo(vizinho);

                        double dx = vizinho.getLongitude() - centro.getLongitude();
                        double dy = vizinho.getLatitude() - centro.getLatitude();
                        double angle = Math.toDegrees(Math.atan2(dy, dx));
                        angle = (angle + 360) % 360;

                        if ((angle >= 45 && angle < 135) || (angle >= 225 && angle < 315)) {
                            grupo2.insert(novoSemaforo); // vias mais horizontais
                        } else {
                            grupo1.insert(novoSemaforo); // vias mais verticais
                        }

                        usados.add(vizinho.getId());
                    }

                    arestaAtual = arestaAtual.getNext();
                }

                while (Math.abs(grupo1.getSize() - grupo2.getSize()) > 1) {
                    if (grupo1.getSize() > grupo2.getSize()) {
                        grupo2.insert(grupo1.removeLast());
                    } else {
                        grupo1.insert(grupo2.removeLast());
                    }
                }

                if (grupo1.isEmpty()) {
                    TrafficLight semaforoExtra = new TrafficLight(centro.getId() + "_extraG1");
                    semaforoExtra.setVinculo(centro);
                    grupo1.insert(semaforoExtra);
                    System.out.println("⚠️ Adicionado semáforo extra no Grupo1 da interseção " + centro.getId());
                }

                if (grupo2.isEmpty()) {
                    TrafficLight semaforoExtra = new TrafficLight(centro.getId() + "_extraG2");
                    semaforoExtra.setVinculo(centro);
                    grupo2.insert(semaforoExtra);
                    System.out.println("⚠️ Adicionado semáforo extra no Grupo2 da interseção " + centro.getId());
                }

                System.out.println("✅ Interseção " + centro.getId() +
                        " - Grupo1: " + grupo1.getSize() +
                        " | Grupo2: " + grupo2.getSize());

                IntersectionController intersec = new IntersectionController(
                        "int_" + centro.getId(),
                        grupo1,
                        grupo2,
                        config
                );
                intersecoes.insert(intersec);
                verticesProcessados.add(centro.getId());
            }

            atual = atual.getNext();
        }

        return intersecoes;
    }

}
