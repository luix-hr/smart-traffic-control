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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
                JSONObject semaforoNode = trafficLightsArray.getJSONObject(i);
                String idSemaforo = semaforoNode.getString("id");
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
            System.err.println("Erro ao ler o arquivo JSON: " + e.getMessage());
        }

        return grafo;
    }

    public static LinkedList<IntersectionController> criarIntersecoesAutomaticas(Graph grafo, SimConfig config) {
        LinkedList<IntersectionController> intersecoes = new LinkedList<>();
        Node<Vertex> atual = grafo.getVertices().getHead();

        while (atual != null) {
            Vertex centro = atual.getData();
            TrafficLight semaforoCentro = centro.getTrafficLight();
            if (semaforoCentro != null) {
                LinkedList<TrafficLight> grupo1 = new LinkedList<>();
                LinkedList<TrafficLight> grupo2 = new LinkedList<>();

                grupo1.insert(semaforoCentro); // adiciona o próprio

                Node<Edge> arestaAtual = grafo.getArestas().getHead();
                while (arestaAtual != null) {
                    Edge e = arestaAtual.getData();
                    Vertex vizinho = null;
                    if (e.getSource().equals(centro)) vizinho = e.getTarget();
                    else if (e.getTarget().equals(centro)) vizinho = e.getSource();

                    if (vizinho != null && vizinho.getTrafficLight() != null) {
                        double diffLat = Math.abs(centro.getLatitude() - vizinho.getLatitude());
                        double diffLon = Math.abs(centro.getLongitude() - vizinho.getLongitude());

                        if (diffLat > diffLon) {
                            grupo1.insert(vizinho.getTrafficLight()); // vertical
                        } else {
                            grupo2.insert(vizinho.getTrafficLight()); // horizontal
                        }
                    }
                    arestaAtual = arestaAtual.getNext();
                }

                if (!grupo1.isEmpty() || !grupo2.isEmpty()) {
                    IntersectionController intersec = new IntersectionController("int_" + centro.getId(), grupo1, grupo2, config);
                    intersecoes.insert(intersec);
                }
            }
            atual = atual.getNext();
        }
        return intersecoes;
    }

    private static double distancia(Vertex v1, Vertex v2) {
        double dLat = v1.getLatitude() - v2.getLatitude();
        double dLon = v1.getLongitude() - v2.getLongitude();
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }
}
