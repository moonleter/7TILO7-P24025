package org.osu.kunz;

import java.util.*;


public class Dijkstra {

    public static void findShortestPath(Graph graph, Node source) {
        source.setDistance(0D);

        Set<Node> visitedNodes = new HashSet<>();
        Set<Node> unvisitedNodes = new HashSet<>();

        unvisitedNodes.add(source);

        while (unvisitedNodes.size() != 0) {
            Node currentNode = findNodeWithLowestDistance(unvisitedNodes);
            unvisitedNodes.remove(currentNode);
            for (Map.Entry<String, Distance> adjacencyPair :
                    currentNode.getConnections().entrySet()) {
                Node adjacentNode = graph.getNodes().stream().filter((n) -> Objects.equals(n.getCode(), adjacencyPair.getKey())).findFirst().orElseThrow(RuntimeException::new);
                Double edgeWeight = adjacencyPair.getValue().getDistance();
                if (!visitedNodes.contains(adjacentNode)) {
                    updateDistanceAndPath(adjacentNode, edgeWeight, currentNode);
                    unvisitedNodes.add(adjacentNode);
                }
            }
            visitedNodes.add(currentNode);
        }
    }

    private static Node findNodeWithLowestDistance(Set<Node> unvisitedNodes) {
        Node lowestDclosestNodeistanceNode = null;
        double lowestDistance = Double.MAX_VALUE;
        for (Node node : unvisitedNodes) {
            double nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDclosestNodeistanceNode = node;
            }
        }
        return lowestDclosestNodeistanceNode;
    }

    private static void updateDistanceAndPath(Node evaluationNode,
                                              Double edgeWeigh, Node sourceNode) {
        Double currentNodeDistance = sourceNode.getDistance();
        if (currentNodeDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(currentNodeDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
