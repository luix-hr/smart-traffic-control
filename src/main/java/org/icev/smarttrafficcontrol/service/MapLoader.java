package org.icev.smarttrafficcontrol.service;
import org.icev.smarttrafficcontrol.datastructure.graph.Edge;
import org.icev.smarttrafficcontrol.datastructure.graph.Graph;
import org.icev.smarttrafficcontrol.datastructure.graph.Vertex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class MapLoader {

    public static Graph carregarJSON(String caminhoArquivo) {
        Graph grafo = new Graph();
        Map<String, Vertex> mapaVertices = new HashMap<>();

        try {
            String jsonContent = Files.readString(Paths.get(caminhoArquivo));
            org.json.JSONObject root = new JSONObject(jsonContent);

            JSONArray nodes = root.getJSONArray("nodes");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String id = node.getString("id");
                double lat = node.getDouble("latitude");
                double lon = node.getDouble("longitude");

                Vertex vertice = new Vertex(id, lat, lon);
                grafo.adicionarVertice(vertice);
                mapaVertices.put(id, vertice);
            }

            JSONArray edges = root.getJSONArray("edges");
            for (int i = 0; i < edges.length(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                String origemId = edge.getString("source");
                String destinoId = edge.getString("target");
                double custo = edge.getDouble("length");

                Vertex origem = mapaVertices.get(origemId);
                Vertex destino = mapaVertices.get(destinoId);

                if (origem != null && destino != null) {
                    Edge aresta = new Edge(origem, destino, custo);
                    grafo.adicionarAresta(aresta);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo JSON: " + e.getMessage());
        }

        return grafo;
    }
}

