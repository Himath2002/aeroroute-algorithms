package io.github.himath2002.aeroroute.domain;

import java.util.List;
import java.util.Objects;

/** A complete candidate route, including its ordered stops and total distance. */
public record Route(List<String> stops, int distanceKm) {
    /** Creates an immutable route from at least two ordered stops. */
    public Route {
        Objects.requireNonNull(stops, "stops");
        stops = List.copyOf(stops);

        if (stops.size() < 2) {
            throw new IllegalArgumentException("A route requires an origin and destination.");
        }
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Route distance cannot be negative.");
        }
    }

    /** Returns the number of intermediate airports on this route. */
    public int layovers() {
        return Math.max(0, stops.size() - 2);
    }

    /** Returns the ordered airport codes as a human-readable path. */
    public String path() {
        return String.join(" -> ", stops);
    }

    @Override
    public String toString() {
        return "%s | %d km | %d layover%s".formatted(
                path(), distanceKm, layovers(), layovers() == 1 ? "" : "s");
    }
}
