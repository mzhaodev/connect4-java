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

        runBenchmark("End-Easy", false);
        runBenchmark("Middle-Easy", false);
        runBenchmark("Middle-Medium", false);
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

        long startTime = System.nanoTime();
        for (String input : inputs) {

            solver.solve(input, useStrongSolver);
        }
        long endTime = System.nanoTime();
        long timeNanos = endTime - startTime;

        System.out.printf("%-22s %s\n",
                          "Model:",
                          useStrongSolver ? "Transposition Table (strong)" : "Transposition Table (weak)");
        System.out.printf("%-22s %s\n", "Test Set:", testSetName);
        System.out.printf("%-22s %,d ns\n", "Mean time:", timeNanos / inputs.size());
        System.out.printf("%-22s %,d\n", "Mean explored nodes:", solver.getTotalExploredNodes() / inputs.size());
        System.out.printf("%-22s %,.0f\n", "Positions/s:", solver.getTotalExploredNodes() * 1_000_000_000F / timeNanos);
        System.out.printf("%-22s %,.2f\n", "TT load factor:", solver.getTTLoadFactor());
        System.out.println();
    }
}
