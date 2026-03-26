package dev.mzhao.connect4;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
public class TranspositionTableTest {

    TranspositionTable tt = new TranspositionTable();

    @Test
    public void testGetAndSet() {

        tt.set(1, Solver.SCORE_MIN);
        tt.set(2, Solver.SCORE_MAX);
        tt.set(3, -5);
        tt.set(4, 5);
        tt.set(5, 0);

        Assertions.assertEquals(Solver.SCORE_MIN, tt.getUpperBoundOrDefault(1, Integer.MIN_VALUE));
        Assertions.assertEquals(Solver.SCORE_MAX, tt.getUpperBoundOrDefault(2, Integer.MIN_VALUE));
        Assertions.assertEquals(-5, tt.getUpperBoundOrDefault(3, Integer.MIN_VALUE));
        Assertions.assertEquals(5, tt.getUpperBoundOrDefault(4, Integer.MIN_VALUE));
        Assertions.assertEquals(0, tt.getUpperBoundOrDefault(5, Integer.MIN_VALUE));
        Assertions.assertEquals(Integer.MIN_VALUE, tt.getUpperBoundOrDefault(6, Integer.MIN_VALUE));
    }
}
