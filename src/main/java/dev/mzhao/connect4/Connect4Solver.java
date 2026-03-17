package dev.mzhao.connect4;

/**
 * This class can be reused to score multiple positions. It is <i>not</i> thread-safe.
 */
public class Connect4Solver {

    public static final int SCORE_MIN = -Position.TOTAL_SLOTS;
    public static final int SCORE_MAX = Position.TOTAL_SLOTS;

    private static final int[] MOVE_ORDER = new int[Position.COLUMNS];
    static {
        int right = Position.COLUMNS / 2;
        int left = right - 1;
        for (int i = 0; i < Position.COLUMNS; ++i) {
            MOVE_ORDER[i] = i % 2 == 0 ? right++ : left--;
        }
    }

    private long totalExploredNodes = 0;

    public long getTotalExploredNodes() {

        return totalExploredNodes;
    }

    /**
     * @see Connect4Solver#solveStrongly(String)
     * @see Connect4Solver#solveWeakly(String)
     */
    public int solve(String moves, boolean strong) {

        if (strong) {

            return solveStrongly(moves);
        }
        return solveWeakly(moves);
    }

    /**
     * Solve for the score of the position
     * @return a number between {@value SCORE_MIN} and {@value SCORE_MAX}
     */
    public int solveStrongly(String moves) {

        return solve(moves, SCORE_MIN, SCORE_MAX);
    }

    /**
     * Return -1, 0, or 1 for losing, drawn, or winning
     */
    public int solveWeakly(String moves) {

        return solve(moves, -1, 1);
    }

    /**
     * Solve for the score of a position, clamped to a provided range.
     * @param moves the sequence of moves played so far.<br>e.g. "1123" means the first player
     *     played in the first column, the second player played in the first column, the first
     *     player played in the second column, and the second player played in the third column
     * @param min the minimum number to return, must be <= max
     * @param max the maximum number to return, must be >= min
     * @return the score of the position, clamped to the inclusive range [min, max]
     * @throws IllegalArgumentException if moves are invalid
     */
    public int solve(String moves, int min, int max) {

        Position p = new Position();
        for (int i = 0; i < moves.length(); ++i) {

            int col = moves.charAt(i) - '1';
            if (!p.isValidCol(col) || !p.canPlayMove(col)) {

                throw new IllegalArgumentException("Invalid move sequence " + moves);
            }
            p.playMove(col);
        }

        int score = solve(p, min, max);
        return Math.clamp(score, min, max);
    }

    private int solve(Position p, int alpha, int beta) {

        ++totalExploredNodes;

        if (p.getEmptySlots() == 0) {
            return 0;
        }

        for (int i = 0; i < Position.COLUMNS; ++i) {

            if (p.canPlayMove(i) && p.isWinningMove(i)) {
                return p.getEmptySlots();
            }
        }

        beta = Math.min(beta, p.getEmptySlots() == 1 ? 0 : p.getEmptySlots() - 2);
        alpha = Math.max(alpha, -(p.getEmptySlots() - 1));

        if (alpha >= beta) {
            return beta;
        }

        int bestScore = alpha;
        for (int candidateMove : MOVE_ORDER) {

            if (p.canPlayMove(candidateMove)) {

                p.playMove(candidateMove);
                int candidateScore = -solve(p, -beta, -bestScore);
                p.undoMove(candidateMove);

                if (candidateScore >= beta) {
                    return beta;
                }

                bestScore = Math.max(bestScore, candidateScore);
            }
        }
        return bestScore;
    }
}
