package dev.mzhao.connect4;

class MoveList {

    private static final long[] DEFAULT_COLUMN_ORDERING = new long[Position.COLUMNS];
    static {
        int right = Position.COLUMNS / 2;
        int left = right - 1;
        for (int i = 0; i < Position.COLUMNS; ++i) {
            DEFAULT_COLUMN_ORDERING[i] = BitboardUtils.column(i % 2 == 0 ? right++ : left--);
        }
    }

    private final long[] moves = new long[2 * Position.COLUMNS];
    private int size;

    void calculateMoveOrder(Position p, long candidateMoves) {

        size = 0;
        for (int i = 0; i < Position.COLUMNS; ++i) {

            long move = DEFAULT_COLUMN_ORDERING[i] & candidateMoves;
            if (move == BitboardUtils.EMPTY) {

                continue;
            }

            int numThreats = Long.bitCount(p.getRealThreatsIfPlaySlot(move));
            int idx = 2 * size;
            while (idx > 0 && numThreats > moves[idx - 1]) {

                moves[idx] = moves[idx - 2];
                moves[idx + 1] = moves[idx - 1];
                idx -= 2;
            }
            moves[idx] = move;
            moves[idx + 1] = numThreats;
            ++size;
        }
    }

    int size() {

        return size;
    }

    long get(int idx) {

        return moves[2 * idx];
    }
}
