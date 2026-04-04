package dev.mzhao.connect4;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TranspositionTableTest {

    TranspositionTable tt = new TranspositionTable(new Statistics());

    @Test
    public void testGetAndSet() {

        tt.set(1, Solver.SCORE_MIN, 0);
        tt.set(2, Solver.SCORE_MAX, 1);
        tt.set(3, -5, 2);
        tt.set(4, 5, 3);
        tt.set(4, 6, 3);
        tt.set(5, 1, 4);
        tt.set(5, 0, 4);

        Assertions.assertEquals(Solver.SCORE_MIN, tt.getValueOrDefault(1, Integer.MIN_VALUE));
        Assertions.assertEquals(Solver.SCORE_MAX, tt.getValueOrDefault(2, Integer.MIN_VALUE));
        Assertions.assertEquals(-5, tt.getValueOrDefault(3, Integer.MIN_VALUE));
        Assertions.assertEquals(5, tt.getValueOrDefault(4, Integer.MIN_VALUE));
        Assertions.assertEquals(0, tt.getValueOrDefault(5, Integer.MIN_VALUE));
        Assertions.assertEquals(Integer.MIN_VALUE, tt.getValueOrDefault(6, Integer.MIN_VALUE));
    }
}
