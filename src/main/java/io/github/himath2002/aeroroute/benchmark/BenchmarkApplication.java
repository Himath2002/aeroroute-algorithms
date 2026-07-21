package io.github.himath2002.aeroroute.benchmark;

/** Command-line entry point for reproducible local performance exploration. */
public final class BenchmarkApplication {
    private BenchmarkApplication() {
    }

    public static void main(String[] arguments) {
        System.out.println("Algorithm    Elements Input order       Elapsed");
        System.out.println("--------------------------------------------------");
        for (SortingBenchmark.Result result : SortingBenchmark.run(new int[]{100, 1_000, 10_000})) {
            System.out.println(result);
        }
        System.out.println();
        System.out.println("Indicative only: results depend on JVM warm-up and host hardware.");
    }
}
