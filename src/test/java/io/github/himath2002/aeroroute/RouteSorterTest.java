package io.github.himath2002.aeroroute;

import io.github.himath2002.aeroroute.algorithm.RouteSorter;
import io.github.himath2002.aeroroute.benchmark.SortingBenchmark;
import io.github.himath2002.aeroroute.domain.Route;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteSorterTest {
    @Test
    void allAlgorithmsProduceTheSameOrdering() {
        List<Route> routes = List.of(
                new Route(List.of("JFK", "LAX"), 3_983),
                new Route(List.of("JFK", "ORD", "LAX"), 3_997),
                new Route(List.of("JFK", "DFW", "LAX"), 4_468),
                new Route(List.of("JFK", "DEN", "LAX"), 3_983)
        );

        List<Route> heap = RouteSorter.heapSort(routes, RouteSorter.SortKey.DISTANCE);
        List<Route> quick = RouteSorter.quickSort(routes, RouteSorter.SortKey.DISTANCE);
        List<Route> merge = RouteSorter.mergeSort(routes, RouteSorter.SortKey.DISTANCE);

        assertEquals(heap, quick);
        assertEquals(heap, merge);
        assertEquals("JFK -> LAX", heap.get(0).path());
    }

    @Test
    void quickSortHandlesLargePreorderedInputWithoutDeepRecursion() {
        List<Route> routes = SortingBenchmark.generate(25_000, SortingBenchmark.InputOrder.ASCENDING);
        List<Route> sorted = RouteSorter.quickSort(routes, RouteSorter.SortKey.DISTANCE);

        assertTrue(RouteSorter.isSorted(sorted, RouteSorter.SortKey.DISTANCE));
    }

    @Test
    void generatedBenchmarkInputsAreDeterministic() {
        assertEquals(
                SortingBenchmark.generate(1_000, SortingBenchmark.InputOrder.RANDOM),
                SortingBenchmark.generate(1_000, SortingBenchmark.InputOrder.RANDOM)
        );
    }
}
