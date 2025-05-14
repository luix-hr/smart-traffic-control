package org.icev.smarttrafficcontrol.service;

import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.icev.smarttrafficcontrol.model.TrafficLight;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MapLoader implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Graph carregarJSON(String caminhoArquivo) {
        Graph grafo = new Graph();
        Map<String, Vertex> mapaVertices = new HashMap<>();

        try {
            String jsonContent = Files.readString(Paths.get(caminhoArquivo));
            JSONObject root = new JSONObject(jsonContent);

            // Lê os vértices (nodes)
            JSONArray nodes = root.getJSONArray("nodes");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String id = node.getString("id");
                double latitude = node.getDouble("latitude");
                double longitude = node.getDouble("longitude");

                Vertex vertice = new Vertex(id, latitude, longitude);

                // Se houver semáforo marcado no nó, adiciona
                if (node.has("traffic_light") && node.getBoolean("traffic_light")) {
                    TrafficLight semaforo = new TrafficLight("TL-" + id);
                    vertice.setTrafficLight(semaforo);
                }

                grafo.adicionarVertice(vertice);
                mapaVertices.put(id, vertice);
            }

            // Lê as arestas (edges)
            JSONArray edges = root.getJSONArray("edges");
            for (int i = 0; i < edges.length(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                String id = edge.getString("id");
                String sourceId = edge.getString("source");
                String targetId = edge.getString("target");
                double length = edge.optDouble("length", 1.0);
                double maxspeed = edge.optDouble("maxspeed", 0.0);

                Vertex origem = mapaVertices.get(sourceId);
                Vertex destino = mapaVertices.get(targetId);

                if (origem != null && destino != null) {
                    Edge aresta = new Edge(id, origem, destino, length, maxspeed);
                    grafo.adicionarAresta(aresta);

                    boolean oneway = edge.optBoolean("oneway", false);
                    if (!oneway) {
                        Edge reversa = new Edge(id + "_rev", destino, origem, length, maxspeed);
                        grafo.adicionarAresta(reversa);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo JSON: " + e.getMessage());
        }

        return grafo;
    }
}