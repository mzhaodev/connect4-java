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
     * */
    static void main() throws IOException {

        runBenchmark("End-Easy");
    }

    private static void runBenchmark(String testSetName) throws IOException {

        String resourcePath = TEST_SETS.get(testSetName);

        ArrayList<String> inputs = new ArrayList<>();
        ArrayList<Integer> expectedOutputs = new ArrayList<>();

        try (InputStream stream = BenchmarkMain.class.getResourceAsStream(resourcePath);
             Scanner scanner = new Scanner(Objects.requireNonNull(stream))) {

            while (scanner.hasNext()) {

                inputs.add(scanner.next());
                expectedOutputs.add(scanner.nextInt());
            }
        }

        Connect4Solver solver = new Connect4Solver();
        ArrayList<Integer> outputs = new ArrayList<>(expectedOutputs.size());

        long startTime = System.nanoTime();
        for (String input : inputs) {
            outputs.add(solver.solve(input));
        }
        long endTime = System.nanoTime();
        long timeNanos = endTime - startTime;

        for (int i = 0; i < outputs.size(); ++i) {
            /* We use a slightly different scoring function than the test set,
               but it shouldn't matter because they result in the same move rankings */
            assert Math.signum(outputs.get(i)) == Math.signum(expectedOutputs.get(i));
        }

        System.out.printf("Test Set: %s\n", testSetName);
        System.out.printf("Mean time: %,d ns\n", timeNanos / inputs.size());
        System.out.printf("Mean explored nodes: %,d\n", solver.getTotalExploredNodes() / inputs.size());
        System.out.printf("Positions/s: %,d\n", solver.getTotalExploredNodes() * 1_000_000_000L / timeNanos);
        System.out.println();
    }
}
