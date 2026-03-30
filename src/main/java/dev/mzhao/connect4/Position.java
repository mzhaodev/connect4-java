package dev.mzhao.connect4;

class Position {

    static final int COLUMNS = 7;
    static final int ROWS = 6;
    static final int TOTAL_SLOTS = COLUMNS * ROWS;

    /**
     * Bitmask of the pieces placed by the next player to move
     */
    private long board = BitboardUtils.EMPTY;
    /**
     * Bitmask of the bottom-most empty cell in each column
     */
    private long heights = BitboardUtils.BOTTOM_ROW;
    private int numMoves = 0;

    /**
     * Whether the provided column is a valid next move
     *
     * @param col the column to move
     * @return true if the column is not full
     */
    boolean canPlayMove(int col) {

        return (heights & BitboardUtils.top(col)) == 0;
    }

    /**
     * Add a move to the position
     *
     * @param col the column played
     */
    void playMove(int col) {

        board ^= mask();
        heights += heightOfColumn(col);
        ++numMoves;
    }

    /**
     * Add a move to the position in the specified slot
     */
    void playMoveInSlot(long slot) {

        board ^= mask();
        heights += slot;
        ++numMoves;
    }

    /**
     * "Undo" the last move in the specified slot.
     *
     * @param slot the most recent slot that was played
     */
    void undoMoveInSlot(long slot) {

        heights -= slot;
        board ^= mask();
        --numMoves;
    }

    /**
     * Return whether the current player can instantly win the game on the next move
     * @return true, if the current player can form four in a row on the next move
     */
    boolean hasWinningMove() {

        return BitboardUtils.winningMoves(board, heights) != BitboardUtils.EMPTY;
    }

    long getPossibleMoves() {

        return BitboardUtils.possibleMoves(heights);
    }

    /**
     * May include sentinel slots or slots already played
     */
    long getOpponentThreats() {

        return BitboardUtils.winningSlots(mask() & ~board);
    }

    /**
     * May include sentinel slots, doesn't include slots already played
     */
    long getRealThreatsIfPlaySlot(long slot) {

        return BitboardUtils.winningSlots(board | slot) & ~mask() & BitboardUtils.PLAYABLE_SPACE;
    }

    /**
     * @return the number of moves so far in this position
     */
    int getNumMoves() {

        return numMoves;
    }

    /**
     * @return The number of empty slots in the position
     */
    int getEmptySlotsCount() {

        return Position.TOTAL_SLOTS - getNumMoves();
    }

    /**
     * Return unique key representing current position
     */
    long key() {

        return board + heights;
    }

    private long mask() {

        return BitboardUtils.mask(heights);
    }

    private long heightOfColumn(int col) {

        return BitboardUtils.heightOfColumn(heights, col);
    }

    static boolean isValidCol(int col) {

        return 0 <= col && col < COLUMNS;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        for (int row = ROWS - 1; row >= 0; --row) {
            for (int col = 0; col < COLUMNS; ++col) {
                builder.append(toChar(row, col));
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    private char toChar(int row, int col) {

        long slot = BitboardUtils.slot(col, row);
        if ((slot & mask()) == 0) {
            return '.';
        }
        return (slot & board) == 0 ? 'O' : 'X';
    }
}
