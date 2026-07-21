package io.github.himath2002.aeroroute;

import io.github.himath2002.aeroroute.service.AirRoutePlanner;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirRoutePlannerTest {
    @Test
    void keepsGraphAndCatalogueInSync() {
        AirRoutePlanner planner = new AirRoutePlanner();
        planner.addAirport("cmb", "Bandaranaike International Airport");
        planner.addAirport("sin", "Singapore Changi Airport");
        planner.connect("CMB", "SIN", 2_750);

        assertTrue(planner.containsAirport("cmb"));
        assertEquals("CMB", planner.airport("CMB").code());
        assertTrue(planner.areConnected("CMB", "SIN"));

        planner.removeAirport("CMB");
        assertFalse(planner.containsAirport("CMB"));
        assertEquals(0, planner.connectionCount());
        assertThrows(NoSuchElementException.class, () -> planner.airport("CMB"));
    }
}
