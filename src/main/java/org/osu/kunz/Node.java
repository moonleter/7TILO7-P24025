package org.osu.kunz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("connections")
    private Connection[] tempConnections;

    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;

    @JsonIgnore
    Map<String, Distance> connections = new HashMap<>();

    @JsonIgnore
    private List<Node> shortestPath = new LinkedList<>();
    @JsonIgnore
    private Double distance = Double.MAX_VALUE;
    @JsonIgnore
    Map<Node, Double> adjacentNodes = new HashMap<>();

    public Node(String code, String name) {
        this.code = code;
        this.name = name;
    }

    void addConnection(String key, Distance value) {
        connections.put(key, value);
    }

    public void addDestination(Node destination, double distance) {
        adjacentNodes.put(destination, distance);
    }

    public static void initializeConnections(Set<Node> nodes) {
        for (Node n : nodes) {
            for (Connection c : n.getTempConnections()) {
                if (Objects.equals(c.getDistance().getUnit(), "mi")) {
                    c.getDistance().setValue(
                            BigDecimal.valueOf(c.getDistance().getValue() * 1.609344)     // convert to km
                                    .setScale(2, RoundingMode.HALF_UP)          // round up to 2 decimal places
                                    .doubleValue()
                    );
                    c.getDistance().setUnit("km");
                }
                n.addConnection(c.getCode(), c.getDistance());
            }
        }
    }

    public static void printFlight(List<Node> paths) {
        StringBuilder output = new StringBuilder();
        output.append(paths.get(0).getCode());

        double totalDistance = 0;

        for (int i = 1; i < paths.size(); i++) {
            double distanceBetween = BigDecimal.valueOf(paths.get(i).getDistance() - paths.get(i - 1).getDistance())
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            totalDistance = BigDecimal.valueOf(totalDistance + distanceBetween)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            output.append(" -> ")
                    .append(paths.get(i).getCode())
                    .append("(")
                    .append(totalDistance)
                    .append(" km, ")
                    .append(distanceBetween)
                    .append(" km)");
        }

        output.append("\nTotal Distance: ").append(totalDistance).append(" km");
        System.out.println(output.toString());
        System.out.println("");
    }
}