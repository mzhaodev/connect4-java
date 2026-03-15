package dev.mzhao.connect4;

public class Connect4Solver {

    private long totalExploredNodes = 0;

    public long getTotalExploredNodes() {

        return totalExploredNodes;
    }

    /**
     * Solve for the final score of the game if played perfectly from the given position
     * from the perspective of the next player to move.
     * @param moves the sequence of moves played so far.<br>e.g. "1123" means the first player
     *     played in the first column, the second player played in the first column, the first
     *     player played in the second column, and the second player played in the third column
     * @return the score of the position:
     * <ul>
     *   <li>0 if the game ends in a draw</li>
     *   <li>(x+1) if the player will win with x empty slots remaining</li>
     *   <li>-(x+1) if the player will lose with x empty slots remaining</li>
     * </ul>
     */
    public int solve(String moves) {

        Position p = new Position();
        for (int i = 0; i < moves.length(); ++i) {

            int col = moves.charAt(i) - '1';
            if (!p.isValidCol(col) || !p.canPlayMove(col)) {

                throw new IllegalArgumentException("Invalid move sequence " + moves);
            }
            p.playMove(col);
        }
        return solve(p);
    }

    private int solve(Position p) {

        ++totalExploredNodes;

        if (p.areAllSlotsFilled()) {
            return 0;
        }

        for (int i = 0; i < Position.COLUMNS; ++i) {

            if (p.canPlayMove(i) && p.isWinningMove(i)) {
                return Position.TOTAL_SLOTS - p.getMoves();
            }
        }

        int maxScore = Integer.MIN_VALUE;
        for (int i = 0; i < Position.COLUMNS; ++i) {

            if (p.canPlayMove(i)) {

                p.playMove(i);
                maxScore = Math.max(maxScore, -solve(p));
                p.undoMove(i);
            }
        }

        return maxScore;
    }
}
