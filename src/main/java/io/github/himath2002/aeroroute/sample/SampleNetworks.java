package io.github.himath2002.aeroroute.sample;

import io.github.himath2002.aeroroute.service.AirRoutePlanner;

/** Reproducible demonstration networks for the interactive console. */
public final class SampleNetworks {
    private SampleNetworks() {
    }

    /** Loads demonstration network 1, 2, or 3 into a new planner. */
    public static AirRoutePlanner load(int networkNumber) {
        AirRoutePlanner planner = new AirRoutePlanner();
        switch (networkNumber) {
            case 1 -> loadTransatlanticNetwork(planner);
            case 2 -> loadAsiaPacificNetwork(planner);
            case 3 -> loadEuropeanNetwork(planner);
            default -> throw new IllegalArgumentException("Network number must be 1, 2, or 3.");
        }
        return planner;
    }

    private static void loadTransatlanticNetwork(AirRoutePlanner planner) {
        addAirports(planner, new String[][]{
                {"JFK", "John F. Kennedy International Airport"},
                {"LAX", "Los Angeles International Airport"},
                {"ORD", "O'Hare International Airport"},
                {"DFW", "Dallas/Fort Worth International Airport"},
                {"DEN", "Denver International Airport"},
                {"SFO", "San Francisco International Airport"},
                {"SJC", "Norman Y. Mineta San Jose International Airport"},
                {"SNA", "John Wayne Airport"},
                {"MEX", "Mexico City International Airport"},
                {"YYZ", "Toronto Pearson International Airport"},
                {"LHR", "London Heathrow Airport"},
                {"CDG", "Charles de Gaulle Airport"},
                {"FRA", "Frankfurt Airport"},
                {"HND", "Tokyo Haneda Airport"},
                {"SYD", "Sydney Kingsford Smith Airport"}
        });
        addConnections(planner, new Connection[]{
                new Connection("JFK", "LAX", 3_983),
                new Connection("JFK", "ORD", 1_190),
                new Connection("ORD", "LAX", 2_807),
                new Connection("ORD", "DFW", 1_291),
                new Connection("DFW", "LAX", 1_987),
                new Connection("DFW", "DEN", 1_032),
                new Connection("DEN", "LAX", 1_387),
                new Connection("DEN", "SFO", 1_556),
                new Connection("SFO", "LAX", 543),
                new Connection("JFK", "LHR", 5_554),
                new Connection("LHR", "CDG", 344),
                new Connection("CDG", "FRA", 448),
                new Connection("FRA", "HND", 9_595),
                new Connection("HND", "SYD", 7_795),
                new Connection("SFO", "MEX", 3_770),
                new Connection("MEX", "YYZ", 3_952),
                new Connection("ORD", "CDG", 6_658),
                new Connection("FRA", "LHR", 654),
                new Connection("JFK", "SJC", 4_110),
                new Connection("SJC", "SNA", 550)
        });
    }

    private static void loadAsiaPacificNetwork(AirRoutePlanner planner) {
        addAirports(planner, new String[][]{
                {"ATL", "Hartsfield-Jackson Atlanta International Airport"},
                {"SEA", "Seattle-Tacoma International Airport"},
                {"MIA", "Miami International Airport"},
                {"BOS", "Logan International Airport"},
                {"PHX", "Phoenix Sky Harbor International Airport"},
                {"LAS", "Harry Reid International Airport"},
                {"GRU", "São Paulo/Guarulhos International Airport"},
                {"EZE", "Ministro Pistarini International Airport"},
                {"JNB", "O. R. Tambo International Airport"},
                {"DXB", "Dubai International Airport"},
                {"SIN", "Singapore Changi Airport"},
                {"BKK", "Suvarnabhumi Airport"},
                {"CMB", "Bandaranaike International Airport"},
                {"KUL", "Kuala Lumpur International Airport"},
                {"BOM", "Chhatrapati Shivaji Maharaj International Airport"}
        });
        addConnections(planner, new Connection[]{
                new Connection("ATL", "SEA", 3_511),
                new Connection("ATL", "MIA", 973),
                new Connection("MIA", "BOS", 2_024),
                new Connection("BOS", "PHX", 3_701),
                new Connection("PHX", "LAS", 411),
                new Connection("SEA", "BOM", 12_489),
                new Connection("ATL", "BOM", 13_708),
                new Connection("BOM", "CMB", 1_542),
                new Connection("CMB", "BKK", 2_395),
                new Connection("BKK", "SIN", 1_417),
                new Connection("SIN", "KUL", 296),
                new Connection("KUL", "DXB", 5_548),
                new Connection("DXB", "JNB", 6_416),
                new Connection("JNB", "GRU", 7_439),
                new Connection("GRU", "EZE", 1_673),
                new Connection("EZE", "MIA", 7_122),
                new Connection("LAS", "SEA", 1_401)
        });
    }

    private static void loadEuropeanNetwork(AirRoutePlanner planner) {
        addAirports(planner, new String[][]{
                {"IAH", "George Bush Intercontinental Airport"},
                {"MSP", "Minneapolis-Saint Paul International Airport"},
                {"DTW", "Detroit Metropolitan Airport"},
                {"CLT", "Charlotte Douglas International Airport"},
                {"PDX", "Portland International Airport"},
                {"PHL", "Philadelphia International Airport"},
                {"MAD", "Adolfo Suárez Madrid-Barajas Airport"},
                {"BCN", "Barcelona-El Prat Airport"},
                {"FCO", "Leonardo da Vinci-Fiumicino Airport"},
                {"MUC", "Munich Airport"},
                {"VIE", "Vienna International Airport"},
                {"ZRH", "Zurich Airport"},
                {"BRU", "Brussels Airport"},
                {"OSL", "Oslo Airport"},
                {"ARN", "Stockholm Arlanda Airport"}
        });
        addConnections(planner, new Connection[]{
                new Connection("IAH", "MSP", 1_657),
                new Connection("MSP", "DTW", 851),
                new Connection("DTW", "CLT", 806),
                new Connection("CLT", "PDX", 3_428),
                new Connection("PDX", "PHL", 3_782),
                new Connection("PHL", "MAD", 5_884),
                new Connection("MAD", "BCN", 483),
                new Connection("BCN", "FCO", 859),
                new Connection("FCO", "MUC", 697),
                new Connection("MUC", "ZRH", 261),
                new Connection("VIE", "ZRH", 602),
                new Connection("ZRH", "BRU", 493),
                new Connection("ZRH", "OSL", 1_428),
                new Connection("OSL", "ARN", 386),
                new Connection("ARN", "BRU", 1_281),
                new Connection("ARN", "MAD", 2_598),
                new Connection("ZRH", "FCO", 694),
                new Connection("ZRH", "CLT", 7_178)
        });
    }

    private static void addAirports(AirRoutePlanner planner, String[][] airports) {
        for (String[] airport : airports) {
            planner.addAirport(airport[0], airport[1]);
        }
    }

    private static void addConnections(AirRoutePlanner planner, Connection[] connections) {
        for (Connection connection : connections) {
            planner.connect(connection.origin, connection.destination, connection.distanceKm);
        }
    }

    private record Connection(String origin, String destination, int distanceKm) {
    }
}
