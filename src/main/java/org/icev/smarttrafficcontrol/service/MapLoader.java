package org.icev.smarttrafficcontrol.service;

import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
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

            // Lê IDs dos vértices que possuem semáforo
            JSONArray trafficLightsArray = root.getJSONArray("traffic_lights");
            for (int i = 0; i < trafficLightsArray.length(); i++) {
                JSONObject semaforoNode = trafficLightsArray.getJSONObject(i);
                String idSemaforo = semaforoNode.getString("id");
                verticesComSemaforo.add(idSemaforo);
            }

            // Carrega os vértices
            JSONArray nodes = root.getJSONArray("nodes");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String id = node.getString("id");
                double lat = node.getDouble("latitude");
                double lon = node.getDouble("longitude");

                Vertex vertice = new Vertex(id, lat, lon);

                // Se vértice tem semáforo, cria e vincula
                if (verticesComSemaforo.contains(id)) {
                    TrafficLight semaforo = new TrafficLight(id);
                    semaforo.setVinculo(vertice);
                    vertice.setTrafficLight(semaforo);
                }

                grafo.adicionarVertice(vertice);
                mapaVertices.put(id, vertice);
            }

            // Carrega as arestas (edges)
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
}
