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
        fScore.put(source.getCode(), haversineHeuristic(source.getCode(), endAirport, graph.getNodes()));

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
                    fScore.put(adjacentNode.getCode(), tentativeGScore + haversineHeuristic(adjacentNode.getCode(), endAirport, graph.getNodes()));

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

   private static double haversineHeuristic(String fromCode, String toCode, Set<Node> nodes) {
    Node fromNode = nodes.stream().filter(n -> n.getCode().equals(fromCode)).findFirst().orElseThrow(RuntimeException::new);
    Node toNode = nodes.stream().filter(n -> n.getCode().equals(toCode)).findFirst().orElseThrow(RuntimeException::new);

    final int R = 6371; // Radius of the Earth in kilometers
    double lat1 = fromNode.getX();
    double lon1 = fromNode.getY();
    double lat2 = toNode.getX();
    double lon2 = toNode.getY();
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c; // Distance in kilometers
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