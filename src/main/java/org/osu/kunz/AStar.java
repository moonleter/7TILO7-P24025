package org.osu.kunz;

import org.osu.kunz.exception.PathNotFoundException;

import java.util.*;

public class AStar {

    public static void findShortestPath(Graph graph, Node source, String endAirport) {
        source.setDistance(0D);

        Set<Node> visitedNodes = new HashSet<>();
        Set<Node> unvisitedNodes = new HashSet<>();

        unvisitedNodes.add(source);

        Map<String, Double> gScore = initializeScoreMap(graph.getNodes());
        Map<String, Double> fScore = initializeScoreMap(graph.getNodes());
        Map<String, String> previousNodeMap = new HashMap<>();

        gScore.put(source.getCode(), 0.0);
        fScore.put(source.getCode(), manhattanHeuristic(source, endAirport, graph.getNodes()));

        while (!unvisitedNodes.isEmpty()) {
            Node currentAirportNode = findNodeWithLowestFScore(unvisitedNodes, fScore);
            unvisitedNodes.remove(currentAirportNode);
            String currentAirport = currentAirportNode.getCode();

            if (currentAirport.equals(endAirport)) {
                reconstructPath(previousNodeMap, graph.getNodes(), source.getCode(), endAirport);
                return;
            }

            for (Map.Entry<String, Distance> adjacencyPair : currentAirportNode.getConnections().entrySet()) {
                Node adjacentNode = graph.getNodes().stream().filter(n -> Objects.equals(n.getCode(), adjacencyPair.getKey())).findFirst().orElseThrow(RuntimeException::new);
                double tentativeGScore = gScore.get(currentAirportNode.getCode()) + adjacencyPair.getValue().getValue();

                if (tentativeGScore < gScore.get(adjacentNode.getCode())) {
                    previousNodeMap.put(adjacentNode.getCode(), currentAirportNode.getCode());
                    gScore.put(adjacentNode.getCode(), tentativeGScore);
                    fScore.put(adjacentNode.getCode(), tentativeGScore + manhattanHeuristic(adjacentNode, endAirport, graph.getNodes()));

                    adjacentNode.setDistance(fScore.get(adjacentNode.getCode()));
                    unvisitedNodes.add(adjacentNode);
                }
            }
            visitedNodes.add(currentAirportNode);
        }
        throw new PathNotFoundException(String.format("Path from %s to %s not found", source.getCode(), endAirport));
    }

    private static Map<String, Double> initializeScoreMap(Set<Node> nodes) {
        Map<String, Double> scoreMap = new HashMap<>();
        for (Node node : nodes) {
            scoreMap.put(node.getCode(), Double.MAX_VALUE);
        }
        return scoreMap;
    }

    private static Node findNodeWithLowestFScore(Set<Node> unvisitedNodes, Map<String, Double> fScore) {
        Node lowestFScoreNode = null;
        double lowestFScore = Double.MAX_VALUE;
        for (Node node : unvisitedNodes) {
            double nodeFScore = fScore.get(node.getCode());
            if (nodeFScore < lowestFScore) {
                lowestFScore = nodeFScore;
                lowestFScoreNode = node;
            }
        }
        return lowestFScoreNode;
    }


    private static double manhattanHeuristic(Node fromNode, String toCode, Set<Node> nodes) {
        Node toNode = nodes.stream().filter(n -> n.getCode().equals(toCode)).findFirst().orElseThrow(RuntimeException::new);
        return Math.abs(fromNode.getConnections().size() - toNode.getConnections().size());
    }

    private static void reconstructPath(Map<String, String> cameFrom, Set<Node> nodes, String startAirport, String endAirport) {
        List<Node> totalPath = new ArrayList<>();
        String currentAirport = endAirport;
        while (cameFrom.containsKey(currentAirport)) {
            final String code = currentAirport;
            totalPath.add(nodes.stream().filter(n -> n.getCode().equals(code)).findFirst().orElseThrow(RuntimeException::new));
            currentAirport = cameFrom.get(currentAirport);
        }
        totalPath.add(nodes.stream().filter(n -> n.getCode().equals(startAirport)).findFirst().orElseThrow(RuntimeException::new));
        Collections.reverse(totalPath);
        Node.printFlight(totalPath);
    }
}