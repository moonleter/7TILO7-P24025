package org.osu.kunz;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static org.osu.kunz.Node.printFlight;


@Getter
@Setter
public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node node) {
        nodes.add(node);
    }

    public static void calculateAndPrintShortestGraphPath(Graph graph, List<Node> nodes, String sourceCode, String destinationCode) {
        nodes = resetNodeAttributes(nodes);
        Node source = Objects.requireNonNull(nodes.stream().filter(n -> Objects.equals(n.getCode(), sourceCode)).findFirst().orElseThrow(RuntimeException::new));
        Node destinationNode = Objects.requireNonNull(nodes.stream().filter(n -> Objects.equals(n.getCode(), destinationCode)).findFirst().orElseThrow(RuntimeException::new));

        Dijkstra.findShortestPath(graph, source);

        List<Node> paths = destinationNode.getShortestPath();
        paths.add(destinationNode);

        printFlight(paths);
    }

    static List<Node> resetNodeAttributes(List<Node> nodes) {
        List<Node> ret = new ArrayList<>();
        for (Node n : nodes) {
            n.setShortestPath(new LinkedList<>());
            n.setDistance(Double.MAX_VALUE);
            n.setAdjacentNodes(new HashMap<>());
            ret.add(n);
        }
        return ret;
    }
}