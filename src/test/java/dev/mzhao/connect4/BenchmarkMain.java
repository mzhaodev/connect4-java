package dev.mzhao.connect4;

import static dev.mzhao.connect4.TestSets.TEST_SETS;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class BenchmarkMain {

    /**
     * These benchmarks are unreliable and serve as a rough estimate of performance.
     */
    public static void main(String[] args) throws IOException {

        runBenchmark("End-Easy", true);
        runBenchmark("Middle-Easy", true);
        runBenchmark("Middle-Medium", true);
        runBenchmark("Begin-Easy", true);
        runBenchmark("Begin-Medium", true);
        runBenchmark("Begin-Hard", true);

        runBenchmark("End-Easy", false);
        runBenchmark("Middle-Easy", false);
        runBenchmark("Middle-Medium", false);
        runBenchmark("Begin-Easy", false);
        runBenchmark("Begin-Medium", false);
        runBenchmark("Begin-Hard", false);
    }

    private static void runBenchmark(String testSetName, boolean useStrongSolver) throws IOException {

        String resourcePath = TEST_SETS.get(testSetName);

        ArrayList<String> inputs = new ArrayList<>();

        try (InputStream stream = BenchmarkMain.class.getResourceAsStream(resourcePath);
             Scanner scanner = new Scanner(Objects.requireNonNull(stream))) {

            while (scanner.hasNext()) {

                inputs.add(scanner.next());
                scanner.nextInt();
            }
        }

        Solver solver = new Solver();
        Statistics statistics = solver.getStatistics();
        long totalExploredNodes = 0;
        long totalTTEntriesUsed = 0;
        long totalTTCacheEvictions = 0;
        long totalTTHits = 0;
        long totalTTMisses = 0;
        long totalTTSets = 0;
        long totalTTGets = 0;
        long totalTTOptimalHits = 0;

        long timeNanos = 0;
        for (String input : inputs) {

            solver.resetTT();
            statistics.reset();

            long startTime = System.nanoTime();
            solver.solve(input, useStrongSolver);
            long endTime = System.nanoTime();
            timeNanos += endTime - startTime;

            totalExploredNodes += statistics.getExploredNodes();
            totalTTEntriesUsed += solver.getTTEntriesUsed();
            totalTTCacheEvictions += statistics.getTTCacheEvictions();
            totalTTHits += statistics.getTTHits();
            totalTTMisses += statistics.getTTMisses();
            totalTTSets += statistics.getTTSets();
            totalTTGets += statistics.getTTGets();
            totalTTOptimalHits += statistics.getTTOptimalHits();
        }

        double meanTTEntriesUsed = (double) totalTTEntriesUsed / inputs.size();
        double ttHitRate = totalTTHits + totalTTMisses == 0 ? 0 : (double) totalTTHits / (totalTTHits + totalTTMisses);
        double ttOptimalHitRate = totalTTGets == 0 ? 0 : (double) totalTTOptimalHits / totalTTGets;

        System.out
            .printf("%-22s %s%s\n", "Model:", "Two-tier transposition table ", useStrongSolver ? "(strong)" : "(weak)");
        System.out.printf("%-22s %s\n", "Test Set:", testSetName);
        System.out.printf("%-22s %,d ns\n", "Mean time:", timeNanos / inputs.size());
        if (Config.STATISTICS_ENABLED) {

            System.out.printf("%-22s %,d\n", "Mean explored nodes:", totalExploredNodes / inputs.size());
            System.out.printf("%-22s %,.0f\n", "Positions/s:", totalExploredNodes * 1_000_000_000F / timeNanos);
            System.out.printf("%-22s %,.1f/%,d (%.2f%%)\n",
                              "Mean TT load factor:",
                              meanTTEntriesUsed,
                              solver.getTTCapacity(),
                              100 * meanTTEntriesUsed / solver.getTTCapacity());
            System.out.printf("%-22s %,d\n", "Mean cache evictions:", totalTTCacheEvictions / inputs.size());
            System.out.printf("%-22s %,d\n", "Mean TT sets:", totalTTSets / inputs.size());
            System.out.printf("%-22s %,d\n", "Mean TT gets:", totalTTGets / inputs.size());
            System.out.printf("%-22s %,.5f\n", "TT hit rate:", ttHitRate);
            if (Config.STATISTICS_CALCULATE_HIT_RATE_OPTIMAL) {

                System.out.printf("%-22s %,.5f\n", "TT optimal hit rate:", ttOptimalHitRate);
            }
        }
        System.out.println();
    }
}
