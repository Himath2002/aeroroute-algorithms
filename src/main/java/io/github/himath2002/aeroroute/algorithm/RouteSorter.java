package io.github.himath2002.aeroroute.algorithm;

import io.github.himath2002.aeroroute.domain.Route;
import io.github.himath2002.aeroroute.structure.BinaryHeap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Heap, quick, and merge sort implementations specialized for candidate routes. */
public final class RouteSorter {
    private RouteSorter() {
    }

    public enum SortKey {
        DISTANCE(Comparator.comparingInt(Route::distanceKm)
                .thenComparingInt(Route::layovers)
                .thenComparing(Route::path)),
        LAYOVERS(Comparator.comparingInt(Route::layovers)
                .thenComparingInt(Route::distanceKm)
                .thenComparing(Route::path));

        private final Comparator<Route> comparator;

        SortKey(Comparator<Route> comparator) {
            this.comparator = comparator;
        }

        public Comparator<Route> comparator() {
            return comparator;
        }
    }

    public static Route bestRoute(List<Route> routes, SortKey key) {
        if (routes.isEmpty()) {
            throw new NoSuchElementException("No routes are available.");
        }
        return heapSort(routes, key).get(0);
    }

    public static List<Route> heapSort(List<Route> routes, SortKey key) {
        Objects.requireNonNull(routes, "routes");
        BinaryHeap<Route> heap = new BinaryHeap<>(key.comparator());
        routes.forEach(heap::offer);

        List<Route> sorted = new ArrayList<>(routes.size());
        while (!heap.isEmpty()) {
            sorted.add(heap.poll());
        }
        return List.copyOf(sorted);
    }

    public static List<Route> quickSort(List<Route> routes, SortKey key) {
        Objects.requireNonNull(routes, "routes");
        Route[] values = routes.toArray(Route[]::new);
        quickSort(values, 0, values.length - 1, key.comparator());
        return List.copyOf(Arrays.asList(values));
    }

    public static List<Route> mergeSort(List<Route> routes, SortKey key) {
        Objects.requireNonNull(routes, "routes");
        Route[] values = routes.toArray(Route[]::new);
        Route[] buffer = new Route[values.length];
        mergeSort(values, buffer, 0, values.length, key.comparator());
        return List.copyOf(Arrays.asList(values));
    }

    public static boolean isSorted(List<Route> routes, SortKey key) {
        for (int index = 1; index < routes.size(); index++) {
            if (key.comparator().compare(routes.get(index - 1), routes.get(index)) > 0) {
                return false;
            }
        }
        return true;
    }

    private static void quickSort(Route[] values, int low, int high, Comparator<Route> comparator) {
        int start = low;
        int end = high;
        while (start < end) {
            int pivot = partition(values, start, end, comparator);

            // Recurse into the smaller side and loop over the larger side. This
            // keeps stack usage logarithmic even for unfavourable input orders.
            if (pivot - start < end - pivot) {
                quickSort(values, start, pivot - 1, comparator);
                start = pivot + 1;
            } else {
                quickSort(values, pivot + 1, end, comparator);
                end = pivot - 1;
            }
        }
    }

    private static int partition(Route[] values, int low, int high, Comparator<Route> comparator) {
        int middle = low + (high - low) / 2;
        if (comparator.compare(values[low], values[middle]) > 0) {
            swap(values, low, middle);
        }
        if (comparator.compare(values[low], values[high]) > 0) {
            swap(values, low, high);
        }
        if (comparator.compare(values[middle], values[high]) > 0) {
            swap(values, middle, high);
        }
        swap(values, middle, high);

        Route pivot = values[high];
        int boundary = low;
        for (int index = low; index < high; index++) {
            if (comparator.compare(values[index], pivot) <= 0) {
                swap(values, boundary, index);
                boundary++;
            }
        }
        swap(values, boundary, high);
        return boundary;
    }

    private static void mergeSort(
            Route[] values,
            Route[] buffer,
            int start,
            int end,
            Comparator<Route> comparator
    ) {
        if (end - start < 2) {
            return;
        }
        int middle = (start + end) / 2;
        mergeSort(values, buffer, start, middle, comparator);
        mergeSort(values, buffer, middle, end, comparator);
        merge(values, buffer, start, middle, end, comparator);
    }

    private static void merge(
            Route[] values,
            Route[] buffer,
            int start,
            int middle,
            int end,
            Comparator<Route> comparator
    ) {
        int left = start;
        int right = middle;
        int destination = start;

        while (left < middle && right < end) {
            if (comparator.compare(values[left], values[right]) <= 0) {
                buffer[destination++] = values[left++];
            } else {
                buffer[destination++] = values[right++];
            }
        }
        while (left < middle) {
            buffer[destination++] = values[left++];
        }
        while (right < end) {
            buffer[destination++] = values[right++];
        }
        System.arraycopy(buffer, start, values, start, end - start);
    }

    private static void swap(Route[] values, int first, int second) {
        Route value = values[first];
        values[first] = values[second];
        values[second] = value;
    }
}
