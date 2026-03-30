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

public class SolverTest {

    private static Stream<Arguments> testSets() {

        return TestSets.TEST_SETS.entrySet().stream().mapMulti((testSet, consumer) -> {
            consumer.accept(Arguments.of(testSet.getValue(), testSet.getKey(), false));
            consumer.accept(Arguments.of(testSet.getValue(), testSet.getKey(), true));
        });
    }

    @ParameterizedTest(name = "{0} {1} strong={2}")
    @MethodSource("testSets")
    void testSolver(String fileName, String testSetName, boolean useStrongSolver) {

        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(60), () -> runTestFile(fileName, useStrongSolver));
    }

    void runTestFile(String fileName, boolean runStrongSolver) throws IOException {

        try (InputStream stream = getClass().getResourceAsStream(fileName);
             Scanner scanner = new Scanner(Objects.requireNonNull(stream))) {

            Solver solver = new Solver();

            while (scanner.hasNext()) {

                String input = scanner.next();
                int expectedOutput = scanner.nextInt();
                int output = solver.solve(input, runStrongSolver);

                checkOutput(expectedOutput, output, runStrongSolver);
                System.out.println("Test passed: " + input);
            }
        }
    }

    void checkOutput(int expectedOutput, int output, boolean useStrongSolver) {

        if (useStrongSolver) {

            /* We use a slightly different scoring function than the test set,
               but it shouldn't affect the search */
            int convertedOutput = output < 0 ? (output - 1) / 2 : (output + 1) / 2;
            Assertions.assertEquals(expectedOutput, convertedOutput);
        }
        else {

            Assertions.assertEquals(Math.signum(expectedOutput), Math.signum(output));
        }
    }
}
