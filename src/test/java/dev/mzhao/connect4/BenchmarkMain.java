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
    static void main() throws IOException {

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
        double totalLoadFactor = 0;

        long timeNanos = 0;
        for (String input : inputs) {

            solver.resetTT();

            long startTime = System.nanoTime();
            solver.solve(input, useStrongSolver);
            long endTime = System.nanoTime();
            timeNanos += endTime - startTime;

            totalLoadFactor += solver.getTTLoadFactor();
        }

        System.out.printf("%-22s %s%s\n", "Model:", "Better move ordering ", useStrongSolver ? "(strong)" : "(weak)");
        System.out.printf("%-22s %s\n", "Test Set:", testSetName);
        System.out.printf("%-22s %,d ns\n", "Mean time:", timeNanos / inputs.size());
        System.out.printf("%-22s %,d\n", "Mean explored nodes:", solver.getTotalExploredNodes() / inputs.size());
        System.out.printf("%-22s %,.0f\n", "Positions/s:", solver.getTotalExploredNodes() * 1_000_000_000F / timeNanos);
        System.out.printf("%-22s %,.5f\n", "Mean TT load factor:", totalLoadFactor / inputs.size());
        System.out.printf("%-22s %,.2f\n", "TT hit rate:", solver.getTTHitRate());
        System.out.println();
    }
}
