package org.osu.kunz;

import java.util.*;

public class AStar {
    //TODO fix:

    public static void calculateShortestPathFromSource(Graph graph, Node source, Node destination) {
        source.setDistance(0D);

        Set<Node> closedSet = new HashSet<>();
        Set<Node> openSet = new HashSet<>();
        openSet.add(source);

        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Double> gScore = new HashMap<>();
        gScore.put(source, 0D);

        Map<Node, Double> fScore = new HashMap<>();
        fScore.put(source, heuristic(source, destination));

        while (!openSet.isEmpty()) {
            Node current = getLowestFScoreNode(openSet, fScore);
            if (current.equals(destination)) {
                reconstructPath(cameFrom, current);
                return;
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Map.Entry<String, Distance> adjacencyPair : current.getConnections().entrySet()) {
                Node neighbor = graph.getNodes().stream().filter((n) -> Objects.equals(n.getCode(), adjacencyPair.getKey())).findFirst().orElseThrow(RuntimeException::new);
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + adjacencyPair.getValue().getDistance();
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else if (tentativeGScore >= gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    continue;
                }

                cameFrom.put(neighbor, current);
                gScore.put(neighbor, tentativeGScore);
                fScore.put(neighbor, tentativeGScore + heuristic(neighbor, destination));
            }
        }
    }

    private static Node getLowestFScoreNode(Set<Node> openSet, Map<Node, Double> fScore) {
        Node lowestFScoreNode = null;
        double lowestFScore = Double.MAX_VALUE;
        for (Node node : openSet) {
            double score = fScore.getOrDefault(node, Double.MAX_VALUE);
            if (score < lowestFScore) {
                lowestFScore = score;
                lowestFScoreNode = node;
            }
        }
        return lowestFScoreNode;
    }

    private static double heuristic(Node node, Node destination) {
        // Euclidean distance as the heuristic
        double dx = node.getX() - destination.getX();
        double dy = node.getY() - destination.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static void reconstructPath(Map<Node, Node> cameFrom, Node current) {
        LinkedList<Node> totalPath = new LinkedList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.addFirst(current);
        }
        Node.printFlight(totalPath);
    }

    public static void calculateAndPrintAStarRoute(Graph graph, List<Node> nodes, String sourceCode, String destinationCode) {
        nodes = Graph.resetNodeAttributes(nodes);
        Node source = Objects.requireNonNull(nodes.stream().filter(n -> Objects.equals(n.getCode(), sourceCode)).findFirst().orElseThrow(RuntimeException::new));
        Node destination = Objects.requireNonNull(nodes.stream().filter(n -> Objects.equals(n.getCode(), destinationCode)).findFirst().orElseThrow(RuntimeException::new));

        calculateShortestPathFromSource(graph, source, destination);
    }
}