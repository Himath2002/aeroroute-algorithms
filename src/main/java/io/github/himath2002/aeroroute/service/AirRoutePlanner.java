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

    public void addAirport(String code, String name) {
        Airport airport = new Airport(code, name);
        graph.addAirport(airport);
        catalogue.put(airport.code(), airport);
    }

    public Airport removeAirport(String code) {
        Airport removed = graph.removeAirport(code);
        catalogue.remove(removed.code());
        return removed;
    }

    public Airport airport(String code) {
        return catalogue.get(code);
    }

    public boolean containsAirport(String code) {
        return catalogue.containsKey(code);
    }

    public List<Airport> airports() {
        return catalogue.entries().stream()
                .map(OpenAddressHashTable.Entry::value)
                .sorted(Comparator.comparing(Airport::code))
                .toList();
    }

    public void connect(String origin, String destination, int distanceKm) {
        graph.connect(origin, destination, distanceKm);
    }

    public void disconnect(String origin, String destination) {
        graph.disconnect(origin, destination);
    }

    public boolean areConnected(String origin, String destination) {
        return graph.areConnected(origin, destination);
    }

    public List<Route> findRoutes(String origin, String destination, int maximumLayovers) {
        return graph.findRoutes(origin, destination, maximumLayovers);
    }

    public String adjacencyList() {
        return graph.adjacencyList();
    }

    public String adjacencyMatrix() {
        return graph.adjacencyMatrix();
    }

    public int airportCount() {
        return graph.airportCount();
    }

    public int connectionCount() {
        return graph.connectionCount();
    }

    public double catalogueLoadFactor() {
        return catalogue.loadFactor();
    }
}
