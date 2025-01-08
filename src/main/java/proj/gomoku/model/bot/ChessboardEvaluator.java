package proj.gomoku.model.bot;

import proj.gomoku.app.Options;
import proj.gomoku.model.*;
import proj.gomoku.model.debug.DebugHandler;
import proj.gomoku.model.debug.OperationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessboardEvaluator {
    private static final int[] CONSECUTIVE_WEIGHT = weight(GomokuHelper.REQUIRED_LENGTH, 24, 2);
    private static final int[] OBSTRUCT_WEIGHT = weight(GomokuHelper.REQUIRED_LENGTH, 22, 3);

    private static final EvaluateResult ESSENTIAL_FACTOR = new EvaluateResult(100, 100, 100, 100);
    private final DroppingGomokuGame game;
    private final List<ImmutableIntegerPair> searching = new ArrayList<>(GomokuHelper.CHESSBOARD_HEIGHT);

    public ChessboardEvaluator(DroppingGomokuGame game) {
        this.game = game;

        System.out.println(Arrays.toString(CONSECUTIVE_WEIGHT));
        System.out.println(Arrays.toString(OBSTRUCT_WEIGHT));
    }

    public int evaluate(int col, int row, ChessState type) {
        if (this.game.getWinner() == type) {
            return 400;
        }

        float sum = 0;
        for (int i = 0; i < this.game.getColumns(); i++) {
           sum += this.game.getHeight(i);
        }

        int average = Math.round(sum / this.game.getColumns()) ;
        int[] evaluated = this.evaluate(col, row, average, type);
        if (Options.ENABLED_DEBUG.getBooleanValue()) {
            DebugHandler.displayWeight(col, evaluated);
        }
        return Arrays.stream(evaluated).sum();
    }

    public int fastEvaluate(int column, int row, ChessState type) {
        ChessState[][] chessboard = this.game.getChessboard();

        int total = 0;
        for (Direction direction : Direction.values()) {
            int score = this.evaluate(chessboard, column, row, direction, type);
            total += score;
            //this.clearSearching();
        }
        return total;
    }

    private int[] evaluate(int column, int row, int averageHeight, ChessState type) {
        ChessState[][] chessboard = this.game.getChessboard();
        int[] scores = new int[4];

        if (this.wouldWin(chessboard, column, row + 1, ChessState.getOpposite(type))) {
            return new int[] {-100, -100, -100, -100};
        }

        int diff = averageHeight - row;
        int adjust = diff < -1 ? 1 : 0;

        this.preview(column, row, type);
        for (Direction direction : Direction.values()) {
            int score = this.evaluate(chessboard, column, row, direction, type);

            scores[direction.ordinal()] = score - adjust;
            this.clearSearching();
        }

        this.preview(column, row, ChessState.NONE);

        return scores;
    }

    private boolean wouldWin(ChessState[][] chessboard, int column, int row, ChessState type) {
        if (!this.isValidPosition(column, row)) {
            return false;
        }

        boolean flag = false;
        this.preview(column, row, type);
        for (Direction direction : Direction.values()) {
            if (direction == Direction.VERTICAL) {
                continue;
            }
            this.addSearching(column, row);

            int count = 1;

            for (int x = column + direction.getX(), y = row + direction.getY(), i = 1;
                 this.isValidPosition(x, y) && chessboard[x][y] == type && i < GomokuHelper.REQUIRED_LENGTH;
                 x += direction.getX(), y += direction.getY(), i++
            ) {
                count ++;
                this.addSearching(x, y);
            }

            if (count >= GomokuHelper.REQUIRED_LENGTH) {
                this.poseSearching(OperationType.BLOCKED);
                flag = true;
                break;
            }

            for (int x = column - direction.getX(), y = row - direction.getY(), i = 1;
                 this.isValidPosition(x, y) && chessboard[x][y] == type && i < GomokuHelper.REQUIRED_LENGTH;
                 x -= direction.getX(), y -= direction.getY(), i++
            ) {
                count ++;
                this.addSearching(x, y);
            }

            if (count >= GomokuHelper.REQUIRED_LENGTH) {
                this.poseSearching(OperationType.BLOCKED);
                flag = true;
                break;
            }

            this.poseSearching(OperationType.SEARCHING);
            this.clearSearching();
        }
        this.preview(column, row, ChessState.NONE);

        return flag;
    }

    private int evaluate(
            ChessState[][] chessboard,
            int column, int row,
            Direction direction,
            ChessState type
    ) {
        this.addSearching(column, row);
        EvaluateResult forward = this.evaluate(chessboard, column, row, direction.getX(), direction.getY(), type);

        if (forward == ESSENTIAL_FACTOR) {
            return CONSECUTIVE_WEIGHT[GomokuHelper.REQUIRED_LENGTH];
        }

        EvaluateResult backward = this.evaluate(chessboard, column, row, -direction.getX(), -direction.getY(), type);

        if (backward == ESSENTIAL_FACTOR) {
            return CONSECUTIVE_WEIGHT[GomokuHelper.REQUIRED_LENGTH];
        }

        int alliances = forward.alliance() + backward.alliance();
        int available = forward.available() + backward.available();


        double weight = 0;
        if (alliances + available >= GomokuHelper.REQUIRED_LENGTH - 1) {
            weight += CONSECUTIVE_WEIGHT[alliances];
            this.poseSearching(OperationType.ACCEPTED);
        }


        if (alliances < 1) {
            int opponents = forward.opponent() + backward.opponent();

            if (opponents > 0
                    && forward.possible() + backward.possible() >= GomokuHelper.REQUIRED_LENGTH - 1
                    && (forward.opponent() > 0 || forward.possible() < GomokuHelper.REQUIRED_LENGTH)
                    && (backward.opponent() > 0 || backward.possible() < GomokuHelper.REQUIRED_LENGTH)) {
                weight += OBSTRUCT_WEIGHT[opponents];
                this.poseSearching(OperationType.BLOCKED);
            }

        }

        return (int) weight;
    }

    private EvaluateResult evaluate(
            ChessState[][] chessboard,
            int column, int row,
            int dx, int dy,
            ChessState type
    ) {
        int alliance = 0;
        int available = 0;

        int opponent = 0;
        int possible = 0;

        boolean hasAlliance = false;
        boolean hasOpponent = false;

        ChessState opposite = ChessState.getOpposite(type);
        for (int x = column + dx, y = row + dy, i = 1;
             this.isValidPosition(x, y) && i < GomokuHelper.REQUIRED_LENGTH;
             x += dx, y += dy, i++
        ) {
            this.addSearching(x, y);

            boolean same = chessboard[x][y] == type;
            boolean anti = chessboard[x][y] == opposite;

            if (same) {
                hasAlliance = true;
            }

            if (anti) {
                hasOpponent = true;
            }

            if (!hasOpponent) {
                if (same) {
                    alliance++;
                } else if (chessboard[x][y] == ChessState.NONE) {
                    available++;
                }
            }

            if (!same && !hasAlliance) {
                if (anti) {
                    opponent++;
                }
                possible++;
            }
        }

        if (alliance >= GomokuHelper.REQUIRED_LENGTH - 1 || opponent >= GomokuHelper.REQUIRED_LENGTH - 1) {
            return ESSENTIAL_FACTOR;
        } else {
            return new EvaluateResult(
                    alliance, available,
                    opponent, possible
            );
        }
    }

    private void clearSearching() {
        if (Options.STEP_DEBUG.getBooleanValue() && !this.searching.isEmpty()) {
            DebugHandler.fill(this.searching, OperationType.RESET, true);
        }
        this.searching.clear();
    }

    private void poseSearching(OperationType type) {
        if (Options.STEP_DEBUG.getBooleanValue() && !this.searching.isEmpty()) {
            DebugHandler.fill(this.searching, type, true);
        }
    }

    private void preview(int x, int y, ChessState state) {
        if (Options.STEP_DEBUG.getBooleanValue()) {
            DebugHandler.set(x, y, state, false);
        }
    }

    private void addSearching(int x, int y) {
        if (Options.STEP_DEBUG.getBooleanValue()) {
            this.searching.add(new ImmutableIntegerPair(x, y));
        }
    }

    public static int[] weight(int n, int max, double exp) {
        int[] weights = new int[n + 1];

        for (int i = 0; i < n; i++) {
            if (i == 0) {
                weights[i] = 0;
            } else {
                weights[i] = (int) Math.round(max * Math.pow((double) i / (n - 1), exp));
            }
        }
        weights[n] = weights[n - 1];
        return weights;
    }

    private boolean isValidPosition(int column, int row) {
        return row >= 0 && column >= 0 && row < GomokuHelper.CHESSBOARD_HEIGHT && column < GomokuHelper.CHESSBOARD_WIDTH;
    }

}
