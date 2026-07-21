package io.github.himath2002.aeroroute.graph;

import io.github.himath2002.aeroroute.domain.Airport;
import io.github.himath2002.aeroroute.domain.Route;
import io.github.himath2002.aeroroute.structure.ArrayQueue;
import io.github.himath2002.aeroroute.structure.DoublyLinkedList;
import io.github.himath2002.aeroroute.structure.OpenAddressHashTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * An undirected, weighted airport graph backed by custom data structures.
 * Route discovery performs a breadth-first exploration of simple paths within
 * the requested layover limit.
 */
public final class AirportGraph {
    private final DoublyLinkedList<Vertex> vertices = new DoublyLinkedList<>();
    private final OpenAddressHashTable<Vertex> verticesByCode = new OpenAddressHashTable<>();
    private int connectionCount;

    /** Returns the number of vertices in the graph. */
    public int airportCount() {
        return vertices.size();
    }

    /** Returns the number of undirected connections in the graph. */
    public int connectionCount() {
        return connectionCount;
    }

    /** Returns whether an airport code exists in the graph. */
    public boolean containsAirport(String code) {
        return verticesByCode.containsKey(normalizeCode(code));
    }

    /** Adds a new airport vertex. */
    public void addAirport(Airport airport) {
        Objects.requireNonNull(airport, "airport");
        if (verticesByCode.containsKey(airport.code())) {
            throw new IllegalArgumentException("Airport " + airport.code() + " already exists.");
        }

        Vertex vertex = new Vertex(airport);
        vertices.addLast(vertex);
        verticesByCode.put(airport.code(), vertex);
    }

    /** Returns the airport for a code or fails when it is unknown. */
    public Airport getAirport(String code) {
        return requireVertex(code).airport;
    }

    /** Returns an immutable, code-sorted airport snapshot. */
    public List<Airport> airports() {
        List<Airport> result = new ArrayList<>(vertices.size());
        for (Vertex vertex : vertices) {
            result.add(vertex.airport);
        }
        result.sort(Comparator.comparing(Airport::code));
        return List.copyOf(result);
    }

    /** Removes an airport and every connection incident to it. */
    public Airport removeAirport(String code) {
        Vertex removed = requireVertex(code);
        List<Edge> connections = removed.edges.toList();
        for (Edge edge : connections) {
            edge.destination.edges.removeIf(candidate -> candidate.destination == removed);
            connectionCount--;
        }

        vertices.remove(removed);
        verticesByCode.remove(removed.airport.code());
        return removed.airport;
    }

    /** Creates one weighted, undirected connection between distinct airports. */
    public void connect(String firstCode, String secondCode, int distanceKm) {
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero.");
        }

        Vertex first = requireVertex(firstCode);
        Vertex second = requireVertex(secondCode);
        if (first == second) {
            throw new IllegalArgumentException("An airport cannot connect to itself.");
        }
        if (edgeTo(first, second) != null) {
            throw new IllegalArgumentException("Route already exists between the selected airports.");
        }

        first.edges.addLast(new Edge(second, distanceKm));
        second.edges.addLast(new Edge(first, distanceKm));
        connectionCount++;
    }

    /** Removes the connection between two airports. */
    public void disconnect(String firstCode, String secondCode) {
        Vertex first = requireVertex(firstCode);
        Vertex second = requireVertex(secondCode);
        if (edgeTo(first, second) == null) {
            throw new NoSuchElementException("No route exists between the selected airports.");
        }

        first.edges.removeIf(edge -> edge.destination == second);
        second.edges.removeIf(edge -> edge.destination == first);
        connectionCount--;
    }

    /** Returns whether two airports share a direct connection. */
    public boolean areConnected(String firstCode, String secondCode) {
        Vertex first = requireVertex(firstCode);
        Vertex second = requireVertex(secondCode);
        return edgeTo(first, second) != null;
    }

    /**
     * Enumerates simple routes between two airports within a layover limit.
     *
     * @return an immutable candidate list in breadth-first discovery order
     */
    public List<Route> findRoutes(String originCode, String destinationCode, int maximumLayovers) {
        if (maximumLayovers < 0) {
            throw new IllegalArgumentException("Maximum layovers cannot be negative.");
        }

        Vertex origin = requireVertex(originCode);
        Vertex destination = requireVertex(destinationCode);
        if (origin == destination) {
            throw new IllegalArgumentException("Origin and destination must be different.");
        }

        int maximumEdges = maximumLayovers + 1;
        ArrayQueue<SearchState> frontier = new ArrayQueue<>();
        frontier.offer(new SearchState(origin, List.of(origin.airport.code()), 0));
        List<Route> matches = new ArrayList<>();

        while (!frontier.isEmpty()) {
            SearchState state = frontier.poll();
            int usedEdges = state.stops.size() - 1;
            if (usedEdges >= maximumEdges) {
                continue;
            }

            for (Edge edge : state.vertex.edges) {
                String nextCode = edge.destination.airport.code();
                if (state.stops.contains(nextCode)) {
                    continue;
                }

                List<String> nextStops = append(state.stops, nextCode);
                int nextDistance = state.distanceKm + edge.distanceKm;
                if (edge.destination == destination) {
                    matches.add(new Route(nextStops, nextDistance));
                } else {
                    frontier.offer(new SearchState(edge.destination, nextStops, nextDistance));
                }
            }
        }

        return List.copyOf(matches);
    }

    /** Returns a deterministic, human-readable adjacency list. */
    public String adjacencyList() {
        StringBuilder output = new StringBuilder();
        for (Airport airport : airports()) {
            Vertex vertex = requireVertex(airport.code());
            List<Edge> edges = vertex.edges.toList().stream()
                    .sorted(Comparator.comparing(edge -> edge.destination.airport.code()))
                    .toList();

            output.append(airport.code()).append(" -> ");
            if (edges.isEmpty()) {
                output.append("(no routes)");
            } else {
                for (int index = 0; index < edges.size(); index++) {
                    if (index > 0) {
                        output.append(", ");
                    }
                    Edge edge = edges.get(index);
                    output.append(edge.destination.airport.code())
                            .append(" (").append(edge.distanceKm).append(" km)");
                }
            }
            output.append(System.lineSeparator());
        }
        return output.toString().stripTrailing();
    }

    /** Returns a deterministic distance matrix using a middle dot for no edge. */
    public String adjacencyMatrix() {
        List<Airport> ordered = airports();
        StringBuilder output = new StringBuilder("     ");
        for (Airport airport : ordered) {
            output.append("%6s".formatted(airport.code()));
        }
        output.append(System.lineSeparator());

        for (Airport row : ordered) {
            output.append("%-5s".formatted(row.code()));
            for (Airport column : ordered) {
                int distance = distanceBetween(row.code(), column.code());
                output.append("%6s".formatted(distance == 0 ? "·" : distance));
            }
            output.append(System.lineSeparator());
        }
        return output.toString().stripTrailing();
    }

    private int distanceBetween(String firstCode, String secondCode) {
        if (firstCode.equals(secondCode)) {
            return 0;
        }
        Edge edge = edgeTo(requireVertex(firstCode), requireVertex(secondCode));
        return edge == null ? 0 : edge.distanceKm;
    }

    private Vertex requireVertex(String code) {
        String normalized = normalizeCode(code);
        Vertex vertex = verticesByCode.getOrNull(normalized);
        if (vertex == null) {
            throw new NoSuchElementException("Airport " + normalized + " does not exist.");
        }
        return vertex;
    }

    private static Edge edgeTo(Vertex source, Vertex destination) {
        for (Edge edge : source.edges) {
            if (edge.destination == destination) {
                return edge;
            }
        }
        return null;
    }

    private static List<String> append(List<String> values, String value) {
        List<String> result = new ArrayList<>(values.size() + 1);
        result.addAll(values);
        result.add(value);
        return List.copyOf(result);
    }

    private static String normalizeCode(String code) {
        Objects.requireNonNull(code, "code");
        String normalized = code.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Airport code must contain exactly three letters.");
        }
        return normalized;
    }

    private static final class Vertex {
        private final Airport airport;
        private final DoublyLinkedList<Edge> edges = new DoublyLinkedList<>();

        private Vertex(Airport airport) {
            this.airport = airport;
        }
    }

    private record Edge(Vertex destination, int distanceKm) {
    }

    private record SearchState(Vertex vertex, List<String> stops, int distanceKm) {
    }
}
