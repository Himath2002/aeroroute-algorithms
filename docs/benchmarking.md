# Benchmark methodology

The benchmark compares AeroRoute's heap, quick, and merge sorting implementations without presenting a local timing as a universal performance claim.

## Reproduce it

```bash
./gradlew benchmark
```

For each input size, the harness creates routes from a fixed random seed and prepares four orderings:

- ascending;
- descending;
- random;
- nearly sorted, with approximately five percent of positions swapped.

Each algorithm receives equivalent input. The resulting route list must pass `RouteSorter.isSorted(...)` before the elapsed time is reported.

## Reference run

This single reference run used Java 17 on an Apple Silicon development machine. Values are milliseconds and should be interpreted as a reproducibility example, not a stable ranking across machines.

| Elements | Input order | Heap | Quick | Merge |
| ---: | --- | ---: | ---: | ---: |
| 10,000 | Ascending | 5.131 | 4.135 | 2.493 |
| 10,000 | Descending | 7.168 | 5.725 | 2.348 |
| 10,000 | Random | 6.181 | 5.071 | 2.792 |
| 10,000 | Nearly sorted | 4.997 | 4.034 | 1.406 |

## Interpretation boundaries

- JVM class loading and just-in-time compilation affect short runs.
- Operating-system scheduling and background work introduce noise.
- The harness measures complete implementation paths, including heap construction where applicable.
- The generated routes have simple two-stop paths so comparison cost is dominated by numeric keys.
- Use JMH, warm-up forks, multiple measured iterations, and confidence intervals for formal microbenchmarking.
