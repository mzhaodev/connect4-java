package dev.mzhao.connect4;

import java.util.Arrays;

class Position {

    static final int COLUMNS = 7;
    static final int ROWS = 6;
    static final int TOTAL_SLOTS = COLUMNS * ROWS;

    private static final int SLOT_EMPTY = 0;
    private static final int SLOT_PLAYER = 1;
    private static final int SLOT_OPPONENT = 2;

    private final int[][] board = new int[COLUMNS][ROWS];
    private final int[] heights = new int[COLUMNS];
    private int moves = 0;

    /**
     * Whether the provided column is a valid next move
     *
     * @param col the column to move
     * @return true if the column is not full
     */
    boolean canPlayMove(int col) {

        return heights[col] < ROWS;
    }

    /**
     * Add a move to the position
     *
     * @param col the column played
     */
    void playMove(int col) {

        board[col][heights[col]++] = getWhoseMove();
        ++moves;
    }

    /**
     * "Undo" the last move in the specified column.
     *
     * @param col the most recent column to be played
     */
    void undoMove(int col) {

        --moves;
        board[col][--heights[col]] = SLOT_EMPTY;
    }


    /**
     * Return whether the current player can instantly win the game by playing in the specified
     * column.
     *
     * @param col the column to move
     * @return true, if the current player can form four in a row by playing in this column
     */
    boolean isWinningMove(int col) {

        return isWinningMove(col, heights[col], 0, 1) //
            || isWinningMove(col, heights[col], 1, 0) //
            || isWinningMove(col, heights[col], 1, 1) //
            || isWinningMove(col, heights[col], 1, -1);
    }

    /**
     * Check whether playing at a location would yield four in a row in a specific direction
     */
    private boolean isWinningMove(int col, int row, int dc, int dr) {

        return countPiecesInDirection(col, row, dc, dr) //
            + countPiecesInDirection(col, row, -dc, -dr)
            >= 3;
    }

    private int countPiecesInDirection(int col, int row, int dc, int dr) {

        int pieces = 0;
        while (true) {

            col += dc;
            row += dr;
            if (isValidOccupiedSlot(col, row) && board[col][row] == getWhoseMove()) {
                ++pieces;
            }
            else {
                return pieces;
            }
        }
    }

    private boolean isValidOccupiedSlot(int col, int row) {

        if (!isValidCol(col)) {
            return false;
        }
        return 0 <= row && row < heights[col];
    }

    /**
     * Check whether all slots are filled
     *
     * @return true if all slots are filled
     */
    boolean areAllSlotsFilled() {

        return moves >= TOTAL_SLOTS;
    }

    /**
     * @return the number of moves so far in this position
     */
    int getMoves() {

        return moves;
    }

    /**
     * Calculate whose turn it is based on the number of moves so far
     *
     * @return SLOT_PLAYER or SLOT_OPPONENT depending on whose turn it is
     */
    int getWhoseMove() {

        return (moves & 1) == 0 ? SLOT_PLAYER : SLOT_OPPONENT;
    }

    boolean isValidCol(int col) {

        return 0 <= col && col < COLUMNS;
    }

    /**
     * Reset the position to the beginning of the game
     */
    void reset() {

        for (int i = 0; i < COLUMNS; ++i) {
            Arrays.fill(board[i], SLOT_EMPTY);
        }
        Arrays.fill(heights, 0);
        moves = 0;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        for (int row = ROWS - 1; row >= 0; --row) {
            for (int col = 0; col < COLUMNS; ++col) {
                builder.append(board[col][row]);
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
