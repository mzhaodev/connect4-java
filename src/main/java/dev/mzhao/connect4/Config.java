package dev.mzhao.connect4;

final class Config {

    static final int TABLE_SIZE_MIB = 16;

    static final boolean STATISTICS_ENABLED = true;
    static final boolean STATISTICS_CALCULATE_HIT_RATE_OPTIMAL = false;

    static {

        if (Integer.bitCount(TABLE_SIZE_MIB) != 1) {

            throw new ExceptionInInitializerError("TABLE_SIZE_MIB must be a power of 2.");
        }

        if (!STATISTICS_ENABLED && STATISTICS_CALCULATE_HIT_RATE_OPTIMAL) {

            throw new ExceptionInInitializerError(
                "STATISTICS_CALCULATE_HIT_RATE_OPTIONAL requires STATISTICS_ENABLED.");
        }
    }
}
