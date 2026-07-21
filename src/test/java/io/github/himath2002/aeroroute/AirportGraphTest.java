package io.github.himath2002.aeroroute;

import io.github.himath2002.aeroroute.domain.Airport;
import io.github.himath2002.aeroroute.domain.Route;
import io.github.himath2002.aeroroute.graph.AirportGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportGraphTest {
    private AirportGraph graph;

    @BeforeEach
    void setUp() {
        graph = new AirportGraph();
        graph.addAirport(new Airport("JFK", "John F. Kennedy International Airport"));
        graph.addAirport(new Airport("ORD", "O'Hare International Airport"));
        graph.addAirport(new Airport("DFW", "Dallas/Fort Worth International Airport"));
        graph.addAirport(new Airport("LAX", "Los Angeles International Airport"));
        graph.connect("JFK", "LAX", 3_983);
        graph.connect("JFK", "ORD", 1_190);
        graph.connect("ORD", "LAX", 2_807);
        graph.connect("ORD", "DFW", 1_291);
        graph.connect("DFW", "LAX", 1_987);
    }

    @Test
    void discoversEverySimpleRouteWithinTheLayoverLimit() {
        List<Route> direct = graph.findRoutes("JFK", "LAX", 0);
        List<Route> oneLayover = graph.findRoutes("JFK", "LAX", 1);
        List<Route> twoLayovers = graph.findRoutes("JFK", "LAX", 2);

        assertEquals(List.of(new Route(List.of("JFK", "LAX"), 3_983)), direct);
        assertEquals(2, oneLayover.size());
        assertEquals(3, twoLayovers.size());
        assertTrue(twoLayovers.contains(new Route(List.of("JFK", "ORD", "DFW", "LAX"), 4_468)));
    }

    @Test
    void removingAnAirportAlsoRemovesItsConnections() {
        Airport removed = graph.removeAirport("ORD");

        assertEquals("ORD", removed.code());
        assertEquals(3, graph.airportCount());
        assertEquals(2, graph.connectionCount());
        assertFalse(graph.containsAirport("ORD"));
        assertThrows(NoSuchElementException.class, () -> graph.findRoutes("JFK", "ORD", 1));
    }

    @Test
    void rejectsInvalidOrDuplicateConnections() {
        assertThrows(IllegalArgumentException.class, () -> graph.connect("JFK", "JFK", 10));
        assertThrows(IllegalArgumentException.class, () -> graph.connect("JFK", "LAX", 10));
        assertThrows(IllegalArgumentException.class, () -> graph.connect("JFK", "ORD", 0));
    }

    @Test
    void producesDeterministicGraphViews() {
        assertTrue(graph.adjacencyList().startsWith("DFW -> LAX (1987 km), ORD (1291 km)"));
        assertTrue(graph.adjacencyMatrix().contains("  3983"));
    }
}
