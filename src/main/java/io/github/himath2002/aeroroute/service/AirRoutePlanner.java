package io.github.himath2002.aeroroute.service;

import io.github.himath2002.aeroroute.domain.Airport;
import io.github.himath2002.aeroroute.domain.Route;
import io.github.himath2002.aeroroute.graph.AirportGraph;
import io.github.himath2002.aeroroute.structure.OpenAddressHashTable;

import java.util.Comparator;
import java.util.List;

/** Keeps the airport catalogue and route graph synchronized behind one API. */
public final class AirRoutePlanner {
    private final AirportGraph graph = new AirportGraph();
    private final OpenAddressHashTable<Airport> catalogue = new OpenAddressHashTable<>();

    /** Adds one airport to both the graph and catalogue. */
    public void addAirport(String code, String name) {
        Airport airport = new Airport(code, name);
        graph.addAirport(airport);
        catalogue.put(airport.code(), airport);
    }

    /** Removes one airport from both state stores. */
    public Airport removeAirport(String code) {
        Airport removed = graph.removeAirport(code);
        catalogue.remove(removed.code());
        return removed;
    }

    /** Looks up an airport by code. */
    public Airport airport(String code) {
        return catalogue.get(code);
    }

    /** Returns whether the catalogue contains an airport code. */
    public boolean containsAirport(String code) {
        return catalogue.containsKey(code);
    }

    /** Returns an immutable, code-sorted catalogue snapshot. */
    public List<Airport> airports() {
        return catalogue.entries().stream()
                .map(OpenAddressHashTable.Entry::value)
                .sorted(Comparator.comparing(Airport::code))
                .toList();
    }

    /** Creates a weighted connection between two known airports. */
    public void connect(String origin, String destination, int distanceKm) {
        graph.connect(origin, destination, distanceKm);
    }

    /** Removes the direct connection between two known airports. */
    public void disconnect(String origin, String destination) {
        graph.disconnect(origin, destination);
    }

    /** Returns whether two airports have a direct route. */
    public boolean areConnected(String origin, String destination) {
        return graph.areConnected(origin, destination);
    }

    /** Returns candidate routes that satisfy the selected layover limit. */
    public List<Route> findRoutes(String origin, String destination, int maximumLayovers) {
        return graph.findRoutes(origin, destination, maximumLayovers);
    }

    /** Returns the graph as a readable adjacency list. */
    public String adjacencyList() {
        return graph.adjacencyList();
    }

    /** Returns the graph as a distance matrix. */
    public String adjacencyMatrix() {
        return graph.adjacencyMatrix();
    }

    /** Returns the number of synchronized airports. */
    public int airportCount() {
        return graph.airportCount();
    }

    /** Returns the number of undirected route connections. */
    public int connectionCount() {
        return graph.connectionCount();
    }

    /** Returns the current custom hash-table load factor. */
    public double catalogueLoadFactor() {
        return catalogue.loadFactor();
    }
}
