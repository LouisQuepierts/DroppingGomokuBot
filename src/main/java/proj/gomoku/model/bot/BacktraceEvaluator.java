package proj.gomoku.model.bot;

import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.GomokuHelper;
import proj.gomoku.model.ImmutableIntegerPair;

public class BacktraceEvaluator {
    public static final int MAX_DEPTH = 9;

    private final DroppingGomokuGame game;
    private final ChessState team;
    private final GomokuGameEvaluator evaluator;
    
    public BacktraceEvaluator(DroppingGomokuGame game, ChessState team, GomokuGameEvaluator evaluator) {
        this.game = game;
        this.team = team;
        this.evaluator = evaluator;
    }
    
    public int evaluate() {
        int bestScore = Integer.MIN_VALUE;
        int bestCol = -1;

        int[] scores = new int[GomokuHelper.CHESSBOARD_WIDTH];
        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            if (!this.game.canAdd(i)) {
                continue;
            }

            scores[i] = this.evaluator.fastEvaluate(i, this.game.getHeight(i), this.team);

            if (scores[i] > bestScore) {
                bestScore = scores[i];
                bestCol = i;
            }
        }

        if (bestScore > 12) {
            return bestCol;
        }

        bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            if (!this.game.add(i)) {
                continue;
            }

            boolean win = this.game.getWinner() == this.team;
            int score = scores[i];
            score += minimax(MAX_DEPTH, ChessState.BLUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            this.game.undo();

            if (win) {
                return i;
            }

            if (score > bestScore) {
                bestScore = score;
                bestCol = i;
            }
        }

        return bestCol;
    }

    private int minimax(int depth, ChessState team, int alpha, int beta) {
        if (depth == 0 || this.game.isGameFinished()) {
            if (this.game.getWinner() != this.team) {
                return this.team == team ? -24 : 24;
            }
            ImmutableIntegerPair lastStep = this.game.getLastStep();
            return this.evaluator.fastEvaluate(lastStep.x(), lastStep.y(), this.team);
        }

        int bestScore = team == this.team ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            if (!this.game.add(i)) {
                continue;
            }

            int score = this.evaluator.fastEvaluate(i, this.game.getHeight(i) - 1, team);
            score += this.minimax(depth - 1, ChessState.getOpposite(team), alpha, beta);
            this.game.undo();

            if (team == this.team) {
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, score);
            } else {
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, score);
            }

            if (beta <= alpha) {
                break;
            }
        }

        return bestScore;
    }
}
