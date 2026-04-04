package dev.mzhao.connect4;

import java.util.stream.Stream;

/**
 * This class can be reused to score multiple positions. It is <i>not</i> thread-safe.
 */
public class Solver {

    public static final int SCORE_MIN = -Position.TOTAL_SLOTS + 7;
    public static final int SCORE_MAX = Position.TOTAL_SLOTS - 6;

    private final Statistics statistics = new Statistics();
    TranspositionTable tt = new TranspositionTable(statistics);

    MoveList[] moveLists = Stream.generate(MoveList::new).limit(Position.TOTAL_SLOTS).toArray(MoveList[] ::new);

    /**
     * @see Solver#solveStrongly(String)
     * @see Solver#solveWeakly(String)
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
            if (!Position.isValidCol(col) || !p.canPlayMove(col)) {

                throw new IllegalArgumentException("Invalid move sequence " + moves);
            }
            p.playMove(col);
        }
        return solve(p, min, max);
    }

    private int solve(Position p, int min, int max) {

        if (p.hasWinningMove()) {

            return p.getEmptySlotsCount();
        }

        int left = min;
        int right = max;

        while (left < right) {

            int mid = left + ((right - left) >>> 1);
            int pivot = mid <= 0 ? Math.min(mid, left / 2) : Math.max(mid, right / 2);

            int score = negamax(p, pivot, pivot + 1);
            if (score <= pivot) {

                right = score;
            }
            else {

                left = score;
            }
        }
        return Math.clamp(left, min, max);
    }

    /**
     * @param p a non-terminal position with no winning moves
     */
    private int negamax(Position p, int alpha, int beta) {

        statistics.incrementExploredNodes();

        if (p.getEmptySlotsCount() == 0) {

            return 0;
        }

        final int scoreIfAnyMoveLoses = -(p.getEmptySlotsCount() - 1);

        long possibleMoves = p.getPossibleMoves();
        long opponentThreats = p.getOpponentThreats();

        long forcedMoves = possibleMoves & opponentThreats;
        if (BitboardUtils.hasTwoOrMore(forcedMoves)) {

            return scoreIfAnyMoveLoses;
        }

        long losingSlots = (opponentThreats & BitboardUtils.PLAYABLE_SPACE) >>> 1;
        long nonLosingPossibleMoves = possibleMoves & ~losingSlots;
        if (forcedMoves != BitboardUtils.EMPTY) {

            nonLosingPossibleMoves &= forcedMoves;
        }

        if (nonLosingPossibleMoves == BitboardUtils.EMPTY) {

            return scoreIfAnyMoveLoses;
        }

        beta = Math.min(beta, Math.max(0, p.getEmptySlotsCount() - 2));
        beta = Math.min(beta, tt.getValueOrDefault(p.key(), SCORE_MAX));
        alpha = Math.max(alpha, Math.min(0, scoreIfAnyMoveLoses + 2));

        if (alpha >= beta) {
            return beta;
        }

        int bestScore = alpha;

        MoveList moveList = moveLists[p.getNumMoves()];
        moveList.calculateMoveOrder(p, nonLosingPossibleMoves);

        for (int i = 0; i < moveList.size(); ++i) {

            long candidateSlot = moveList.get(i);
            if (candidateSlot != BitboardUtils.EMPTY) {

                p.playMoveInSlot(candidateSlot);
                int candidateScore = -negamax(p, -beta, -bestScore);
                p.undoMoveInSlot(candidateSlot);

                if (candidateScore >= beta) {
                    return candidateScore;
                }

                bestScore = Math.max(bestScore, candidateScore);
            }
        }

        tt.set(p.key(), bestScore, p.getNumMoves());
        return bestScore;
    }

    /**
     * Get the total number of explored nodes
     */
    public long getTotalExploredNodes() {

        return statistics.getExploredNodes();
    }

    /**
     * Get the fraction of entries filled in the transposition table
     */
    public double getTTLoadFactor() {

        return tt.getLoadFactor();
    }

    public int getTTEntriesUsed() {

        return tt.getEntriesUsed();
    }

    public int getTTCapacity() {

        return tt.getCapacity();
    }

    Statistics getStatistics() {

        return statistics;
    }

    /**
     * Get the hit rate of transposition table
     */
    public double getTTHitRate() {

        return statistics.getTTHitRate();
    }

    public long getTTCacheEvictions() {

        return statistics.getTTCacheEvictions();
    }

    public void resetTT() {

        tt.reset();
    }
}
