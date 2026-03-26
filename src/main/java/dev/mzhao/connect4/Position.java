package dev.mzhao.connect4;

import java.util.stream.IntStream;

class Position {

    static final int COLUMNS = 7;
    static final int ROWS = 6;
    static final int TOTAL_SLOTS = COLUMNS * ROWS;

    static final int BITBOARD_ROWS = ROWS + 1;

    private static final long BITBOARD_EMPTY = 0;
    private static final long BITBOARD_BOTTOM_ROW =
        IntStream.range(0, COLUMNS).mapToLong(Position::BOTTOM).reduce(0, Long::sum);
    private static final long BITBOARD_FIRST_COLUMN = 2 * TOP(0) - 1;

    /**
     * Bitmask of the pieces placed by the next player to move
     */
    private long board = BITBOARD_EMPTY;
    /**
     * Bitmask of the bottom-most empty cell in each column
     */
    private long heights = BITBOARD_BOTTOM_ROW;
    private int numMoves = 0;

    /**
     * Whether the provided column is a valid next move
     *
     * @param col the column to move
     * @return true if the column is not full
     */
    boolean canPlayMove(int col) {

        return (heights & TOP(col)) == 0;
    }

    /**
     * Add a move to the position
     *
     * @param col the column played
     */
    void playMove(int col) {

        board ^= MASK();
        heights += HEIGHT(col);
        ++numMoves;
    }

    /**
     * "Undo" the last move in the specified column.
     *
     * @param col the most recent column to be played
     */
    void undoMove(int col) {

        heights -= HEIGHT(col) >>> 1;
        board ^= MASK();
        --numMoves;
    }


    /**
     * Return whether the current player can instantly win the game by playing in the specified
     * column.
     *
     * @param col the column to move
     * @return true, if the current player can form four in a row by playing in this column
     */
    boolean isWinningMove(int col) {

        long bitboard = board | HEIGHT(col);
        return checkFourInARow(bitboard, BITBOARD_ROWS)     //
            || checkFourInARow(bitboard, BITBOARD_ROWS - 1) //
            || checkFourInARow(bitboard, BITBOARD_ROWS + 1) //
            || checkFourInARow(bitboard, 1);
    }

    private static boolean checkFourInARow(long bitboard, int offset) {

        long x = bitboard & (bitboard >>> offset);
        return (x & (x >>> (2 * offset))) != BITBOARD_EMPTY;
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
    int getEmptySlots() {

        return Position.TOTAL_SLOTS - getNumMoves();
    }


    /**
     * Return unique key representing current position
     */
    long key() {

        return board + heights;
    }

    private long MASK() {

        return heights - BITBOARD_BOTTOM_ROW;
    }

    private static long TOP(int col) {

        return 1L << ((col + 1) * BITBOARD_ROWS - 1);
    }

    private static long BOTTOM(int col) {

        return 1L << (col * BITBOARD_ROWS);
    }

    private static long COLUMN(int col) {

        return BITBOARD_FIRST_COLUMN << (col * BITBOARD_ROWS);
    }

    private long HEIGHT(int col) {

        return COLUMN(col) & heights;
    }

    private static long SLOT(int col, int row) {

        return 1L << (col * BITBOARD_ROWS + row);
    }

    public static boolean isValidCol(int col) {

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

        long slot = SLOT(col, row);
        if ((slot & MASK()) == 0) {
            return '.';
        }
        return (slot & board) == 0 ? 'X' : 'O';
    }
}
