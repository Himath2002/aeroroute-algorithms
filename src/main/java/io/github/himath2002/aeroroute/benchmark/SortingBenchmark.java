package io.github.himath2002.aeroroute.benchmark;

import io.github.himath2002.aeroroute.algorithm.RouteSorter;
import io.github.himath2002.aeroroute.domain.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

/** A deterministic, correctness-checked benchmark for the three route sort implementations. */
public final class SortingBenchmark {
    private static final long SEED = 2_162_162_8L;

    private SortingBenchmark() {
    }

    public enum InputOrder {
        ASCENDING,
        DESCENDING,
        RANDOM,
        NEARLY_SORTED
    }

    public record Result(String algorithm, int elements, InputOrder order, double milliseconds) {
        @Override
        public String toString() {
            return "%-10s %,7d %-14s %9.3f ms".formatted(
                    algorithm, elements, order.name().toLowerCase(Locale.ROOT), milliseconds);
        }
    }

    public static List<Result> run(int[] sizes) {
        List<Result> results = new ArrayList<>();
        for (int size : sizes) {
            for (InputOrder order : InputOrder.values()) {
                List<Route> input = generate(size, order);
                results.add(measure("heap", size, order, input,
                        routes -> RouteSorter.heapSort(routes, RouteSorter.SortKey.DISTANCE)));
                results.add(measure("quick", size, order, input,
                        routes -> RouteSorter.quickSort(routes, RouteSorter.SortKey.DISTANCE)));
                results.add(measure("merge", size, order, input,
                        routes -> RouteSorter.mergeSort(routes, RouteSorter.SortKey.DISTANCE)));
            }
        }
        return List.copyOf(results);
    }

    public static List<Route> generate(int size, InputOrder order) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        Random random = new Random(SEED + size);
        List<Route> routes = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            routes.add(new Route(List.of("SRC", "DST"), 100 + random.nextInt(15_000)));
        }

        Comparator<Route> comparator = RouteSorter.SortKey.DISTANCE.comparator();
        switch (order) {
            case ASCENDING -> routes.sort(comparator);
            case DESCENDING -> routes.sort(comparator.reversed());
            case RANDOM -> Collections.shuffle(routes, random);
            case NEARLY_SORTED -> {
                routes.sort(comparator);
                int swaps = Math.max(1, size / 20);
                for (int index = 0; index < swaps && size > 1; index++) {
                    int first = random.nextInt(size);
                    int second = random.nextInt(size);
                    Collections.swap(routes, first, second);
                }
            }
        }
        return List.copyOf(routes);
    }

    private static Result measure(
            String algorithm,
            int size,
            InputOrder order,
            List<Route> input,
            Function<List<Route>, List<Route>> sort
    ) {
        long started = System.nanoTime();
        List<Route> sorted = sort.apply(input);
        long elapsed = System.nanoTime() - started;
        if (!RouteSorter.isSorted(sorted, RouteSorter.SortKey.DISTANCE)) {
            throw new IllegalStateException(algorithm + " sort returned an invalid ordering.");
        }
        return new Result(algorithm, size, order, elapsed / 1_000_000.0);
    }
}
