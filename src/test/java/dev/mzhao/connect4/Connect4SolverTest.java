package dev.mzhao.connect4;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class Connect4SolverTest {

    private static Stream<Arguments> testSets() {
        return TestSets.TEST_SETS.entrySet().stream().map(
            testSet -> Arguments.of(testSet.getValue(), testSet.getKey()));
    }

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("testSets")
    void testSolver(String fileName, String ignoredTestSetName) {

        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> { runTestFile(fileName); });
    }

    void runTestFile(String fileName) throws IOException {

        try (InputStream stream = getClass().getResourceAsStream(fileName);
             Scanner scanner = new Scanner(Objects.requireNonNull(stream))) {

            Connect4Solver solver = new Connect4Solver();

            while (scanner.hasNext()) {

                String input = scanner.next();
                int expectedOutput = scanner.nextInt();

                /* We use a slightly different scoring function than the test set,
                   but it shouldn't matter because they result in the same move rankings */
                int output = solver.solve(input);
                int convertedOutput = output < 0 ? (output - 1) / 2 : (output + 1) / 2;
                Assertions.assertEquals(expectedOutput, convertedOutput);
            }
        }
    }
}
