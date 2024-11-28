package org.osu.kunz;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.osu.kunz.Graph.calculateAndPrintAStarPath;
import static org.osu.kunz.Graph.calculateAndPrintShortestDijkstraPath;
import static org.osu.kunz.Node.initializeConnections;

public class Main {
    public static void main(String[] args) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<Node> nodes = mapper.readValue(new File("src/main/resources/airports.json"), new TypeReference<>() {
        });

        Graph graph = new Graph();
        for (Node node : nodes)
            graph.addNode(node);

        initializeConnections(graph.getNodes());

        calculateAndPrintShortestDijkstraPath(graph, nodes, "PRG", "DEL");

        calculateAndPrintShortestDijkstraPath(graph, nodes, "BRE", "FNJ");

        calculateAndPrintShortestDijkstraPath(graph, nodes, "JFK", "CAI");

        calculateAndPrintShortestDijkstraPath(graph, nodes, "DUB", "DME");

        calculateAndPrintShortestDijkstraPath(graph, nodes, "OKA", "EVX");


        System.out.println("_________________________________________________________________________________________");
        System.out.println("A* algorithm:");
        calculateAndPrintAStarPath(graph, nodes, "PRG", "DEL");
        calculateAndPrintAStarPath(graph, nodes, "BRE", "FNJ");
        calculateAndPrintAStarPath(graph, nodes, "JFK", "CAI");
        calculateAndPrintAStarPath(graph, nodes, "DUB", "DME");
        calculateAndPrintAStarPath(graph, nodes, "OKA", "EVX");

    }
}