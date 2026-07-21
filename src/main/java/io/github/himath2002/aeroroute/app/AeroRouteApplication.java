package io.github.himath2002.aeroroute.app;

import io.github.himath2002.aeroroute.algorithm.RouteSorter;
import io.github.himath2002.aeroroute.benchmark.SortingBenchmark;
import io.github.himath2002.aeroroute.domain.Airport;
import io.github.himath2002.aeroroute.domain.Route;
import io.github.himath2002.aeroroute.sample.SampleNetworks;
import io.github.himath2002.aeroroute.service.AirRoutePlanner;

import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Interactive console entry point for exploring the route network and algorithms. */
public final class AeroRouteApplication {
    private final AirRoutePlanner planner;
    private final ConsoleInput input;
    private final PrintStream output;

    private AeroRouteApplication(AirRoutePlanner planner, Scanner scanner, PrintStream output) {
        this.planner = planner;
        this.input = new ConsoleInput(scanner, output);
        this.output = output;
    }

    public static void main(String[] arguments) {
        int networkNumber;
        try {
            networkNumber = arguments.length == 0 ? 1 : Integer.parseInt(arguments[0]);
            AirRoutePlanner planner = SampleNetworks.load(networkNumber);
            new AeroRouteApplication(planner, new Scanner(System.in), System.out).run();
        } catch (NumberFormatException exception) {
            System.err.println("Network number must be 1, 2, or 3.");
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private void run() {
        printHeader();
        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.integer("Choose an action: ", value -> value >= 0 && value <= 11,
                    "Enter a number from 0 to 11.");
            try {
                running = dispatch(choice);
            } catch (IllegalArgumentException | NoSuchElementException exception) {
                output.println("\nUnable to complete that action: " + exception.getMessage());
            }
        }
        output.println("\nRoute session closed. Safe travels.");
    }

    private boolean dispatch(int choice) {
        switch (choice) {
            case 1 -> addAirport();
            case 2 -> addConnection();
            case 3 -> removeAirport();
            case 4 -> removeConnection();
            case 5 -> output.println("\n" + planner.adjacencyList());
            case 6 -> output.println("\n" + planner.adjacencyMatrix());
            case 7 -> discoverAndSortRoutes();
            case 8 -> findAirport();
            case 9 -> listAirports();
            case 10 -> runBenchmark();
            case 11 -> printSummary();
            case 0 -> {
                return false;
            }
            default -> throw new IllegalArgumentException("Unknown menu choice.");
        }
        return true;
    }

    private void addAirport() {
        String code = input.airportCode("Airport code: ");
        String name = input.text("Airport name: ", value -> !value.isBlank(), "Name cannot be blank.");
        planner.addAirport(code, name);
        output.println("Added " + code + " to the network and catalogue.");
    }

    private void addConnection() {
        String origin = input.airportCode("Origin code: ");
        String destination = input.airportCode("Destination code: ");
        int distance = input.integer("Distance in kilometres: ", value -> value > 0,
                "Distance must be greater than zero.");
        planner.connect(origin, destination, distance);
        output.printf("Connected %s and %s across %,d km.%n", origin, destination, distance);
    }

    private void removeAirport() {
        String code = input.airportCode("Airport code to remove: ");
        Airport removed = planner.removeAirport(code);
        output.println("Removed " + removed.code() + " and its connected routes.");
    }

    private void removeConnection() {
        String origin = input.airportCode("First airport code: ");
        String destination = input.airportCode("Second airport code: ");
        planner.disconnect(origin, destination);
        output.println("Removed the route between " + origin + " and " + destination + ".");
    }

    private void discoverAndSortRoutes() {
        String origin = input.airportCode("Origin code: ");
        String destination = input.airportCode("Destination code: ");
        int layovers = input.integer("Maximum layovers: ", value -> value >= 0,
                "Layovers cannot be negative.");
        List<Route> routes = planner.findRoutes(origin, destination, layovers);
        if (routes.isEmpty()) {
            output.println("No route matches the selected limit.");
            return;
        }

        int keyChoice = input.integer("Sort by 1) distance or 2) layovers: ", value -> value == 1 || value == 2,
                "Choose 1 or 2.");
        RouteSorter.SortKey key = keyChoice == 1
                ? RouteSorter.SortKey.DISTANCE : RouteSorter.SortKey.LAYOVERS;
        List<Route> sorted = RouteSorter.heapSort(routes, key);

        output.printf("%n%d candidate route%s:%n", sorted.size(), sorted.size() == 1 ? "" : "s");
        for (int index = 0; index < sorted.size(); index++) {
            output.printf("%2d. %s%n", index + 1, sorted.get(index));
        }
        output.println("Best match: " + sorted.get(0));
    }

    private void findAirport() {
        String code = input.airportCode("Airport code: ");
        Airport airport = planner.airport(code);
        output.println(airport.code() + " — " + airport.name());
    }

    private void listAirports() {
        output.println("\nAirport catalogue");
        for (Airport airport : planner.airports()) {
            output.printf("  %-3s  %s%n", airport.code(), airport.name());
        }
    }

    private void runBenchmark() {
        output.println("\nCorrectness-checked sorting benchmark");
        output.println("Times are indicative and depend on the current machine/JVM.");
        for (SortingBenchmark.Result result : SortingBenchmark.run(new int[]{100, 1_000, 10_000})) {
            output.println(result);
        }
    }

    private void printSummary() {
        output.printf("%nNetwork summary%n  %,d airports%n  %,d undirected routes%n  %.1f%% catalogue load%n",
                planner.airportCount(),
                planner.connectionCount(),
                planner.catalogueLoadFactor() * 100);
    }

    private void printHeader() {
        output.println("""

                 AEROROUTE ALGORITHMS
                 Explore routes. Inspect structures. Compare sorting strategies.
                """);
        printSummary();
    }

    private void printMenu() {
        output.println("""

                1  Add airport             7  Discover and rank routes
                2  Add route               8  Find airport information
                3  Remove airport          9  List airport catalogue
                4  Remove route           10  Compare sorting algorithms
                5  Show adjacency list    11  Show network summary
                6  Show adjacency matrix   0  Exit
                """);
    }
}
