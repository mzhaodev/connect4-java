package dev.mzhao.connect4;

import java.util.Arrays;

class TranspositionTable {

    private static final int TABLE_SIZE_MIB = 64;
    private static final int ENTRY_SIZE = Long.SIZE;
    private static final int NUM_ENTRIES = (TABLE_SIZE_MIB * 1024 * 1024) / Long.BYTES;

    private static final int INDEX_SIZE = Long.numberOfTrailingZeros(NUM_ENTRIES);

    private static final int KEY_SIZE = Position.COLUMNS * BitboardUtils.ROWS;
    private static final int UPPER_BOUND_SIZE = ENTRY_SIZE - KEY_SIZE;

    private static final long GOLDEN_GAMMA = 0x9e3779b97f4a7c15L;

    static {

        if (Long.bitCount(TABLE_SIZE_MIB) != 1) {

            throw new ExceptionInInitializerError("TABLE_SIZE_MIB must be a power of 2.");
        }

        if ((1L << UPPER_BOUND_SIZE) <= fromUpperBound(Solver.SCORE_MAX)) {

            throw new ExceptionInInitializerError("Max score does not fit in the transposition table.");
        }

        if ((1L << UPPER_BOUND_SIZE) <= fromUpperBound(Solver.SCORE_MIN)) {

            throw new ExceptionInInitializerError("Min score does not fit in the transposition table.");
        }
    }

    private final long[] table = new long[NUM_ENTRIES];

    private long tableHits = 0;
    private long tableMisses = 0;

    void set(long key, int upperBound) {

        int handle = idx(key);
        table[handle] = createEntry(key, upperBound);
    }

    int getUpperBoundOrDefault(long key, int defaultValue) {

        long entry = table[idx(key)];
        if (getKey(entry) == key) {

            ++tableHits;
            return getUpperBound(entry);
        }
        ++tableMisses;
        return defaultValue;
    }

    private int idx(long key) {

        return (int) ((key * GOLDEN_GAMMA) >>> (Long.SIZE - INDEX_SIZE));
    }

    private static long getKey(long entry) {

        return entry >>> UPPER_BOUND_SIZE;
    }

    private static int getUpperBound(long entry) {

        return toUpperBound(entry & ((1L << UPPER_BOUND_SIZE) - 1));
    }

    private static long createEntry(long key, int upperBound) {

        return (key << UPPER_BOUND_SIZE) + fromUpperBound(upperBound);
    }

    private static long fromUpperBound(int upperBound) {

        return upperBound - Solver.SCORE_MIN;
    }

    private static int toUpperBound(long bits) {

        return (int) bits + Solver.SCORE_MIN;
    }

    double getLoadFactor() {

        int count = 0;
        for (long entry : table) {
            if (entry != 0) {
                ++count;
            }
        }
        return (double) count / NUM_ENTRIES;
    }

    double getHitRate() {

        return (double) tableHits / (tableHits + tableMisses);
    }

    void reset() {

        Arrays.fill(table, 0);
    }
}
