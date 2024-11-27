package org.osu.kunz;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.osu.kunz.Graph.calculateAndPrintShortestGraphPath;
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

        // Prague - Ruzyne International Airport (PRG) -> Indira Gandhi International Airport (DEL)
        calculateAndPrintShortestGraphPath(graph, nodes, "PRG", "DEL");

        // Bremen Airport -> Sunan International Airport
        calculateAndPrintShortestGraphPath(graph, nodes, "BRE", "FNJ");

        // John F Kennedy International Airport -> Cairo International Airport
        calculateAndPrintShortestGraphPath(graph, nodes, "JFK", "CAI");

        // Dublin Airport -> Domodedovo Airport
        calculateAndPrintShortestGraphPath(graph, nodes, "DUB", "DME");

        // Naha Airport -> Evreux Airport
        calculateAndPrintShortestGraphPath(graph, nodes, "OKA", "EVX");

        System.out.println("A* algorithm:");
        AStar.calculateAndPrintAStarRoute(graph, nodes, "PRG", "DEL");
        AStar.calculateAndPrintAStarRoute(graph, nodes, "BRE", "FNJ");
        AStar.calculateAndPrintAStarRoute(graph, nodes, "JFK", "CAI");
        AStar.calculateAndPrintAStarRoute(graph, nodes, "DUB", "DME");
        AStar.calculateAndPrintAStarRoute(graph, nodes, "OKA", "EVX");

    }
}