package proj.gomoku.model;

import lombok.Getter;
import proj.gomoku.model.bot.ChessboardCache;
import proj.gomoku.model.bot.Direction;

import java.util.Arrays;
import java.util.Stack;

public class DroppingGomokuGame {
    @Getter
    private final ChessState[][] chessboard = new ChessState[GomokuHelper.CHESSBOARD_SIZE][GomokuHelper.CHESSBOARD_SIZE];
    private final int[] height = new int[GomokuHelper.CHESSBOARD_SIZE];

    private final Stack<Integer> steps = new Stack<>();
    private final ChessboardCache cache = new ChessboardCache();

    private ChessState current = ChessState.RED;
    private ChessState winner = ChessState.NONE;

    public DroppingGomokuGame() {
        for (ChessState[] states : chessboard) {
            Arrays.fill(states, ChessState.NONE);
        }
    }

    public boolean add(int column) {
        if (!this.canGameContinue()) {
            return false;
        }

        if (!this.canAdd(column)) {
            return false;
        }

        int row = this.height[column];

        this.chessboard[column][row] = this.current;
        this.cache.update(column, row, this.chessboard, this.current);
        this.steps.push(GomokuHelper.getPackedIndex(column, row));

        this.height[column]++;

        if (this.steps.size() > 9 && isWin(column, row)) {
            this.winner = this.current;
        }

        this.current = this.current == ChessState.RED ? ChessState.BLUE : ChessState.RED;
        return true;
    }

    public boolean undo() {
        if (this.steps.isEmpty()) {
            return false;
        }

        System.out.println("Undo");

        int packed = this.steps.pop();
        int x = packed / GomokuHelper.CHESSBOARD_SIZE;
        int y = packed % GomokuHelper.CHESSBOARD_SIZE;
        this.current = this.current == ChessState.RED ? ChessState.BLUE : ChessState.RED;

        this.chessboard[x][y] = ChessState.NONE;
        this.height[x] --;
        this.cache.update(x, y, this.chessboard, this.current);
        return true;
    }

    public boolean canGameContinue() {
        return this.winner == ChessState.NONE && this.steps.size() < GomokuHelper.CHESSBOARD_SIZE * GomokuHelper.CHESSBOARD_SIZE;
    }

    public int getLastStep() {
        if (this.steps.isEmpty()) {
            return -1;
        }
        return this.steps.peek();
    }

    public ChessState getLastState() {
        return this.current == ChessState.RED ? ChessState.BLUE : ChessState.RED;
    }

    public ChessState getCurrentState() {
        return this.current;
    }

    private boolean isWin(int column, int row) {
        ChessState type = this.chessboard[column][row];

        if (type == ChessState.NONE) {  // no possible, for case insure
            return false;
        }

        for (Direction direction : Direction.values()) {
            int count = GomokuHelper.countDirection(column, row, direction.getX(), direction.getY(), this.chessboard, type) + 1;

            if (count >= GomokuHelper.REQUIRED_LENGTH) {
                return true;
            }

            count += GomokuHelper.countDirection(column, row, -direction.getX(), -direction.getY(), this.chessboard, type);

            if (count >= GomokuHelper.REQUIRED_LENGTH) {
                return true;
            }
        }

        return false;
    }

    public boolean canAdd(int column) {
        return isValidColumn(column) && this.height[column] < GomokuHelper.CHESSBOARD_SIZE;
    }

    public boolean isValidColumn(int column) {
        return column >= 0 && column < GomokuHelper.CHESSBOARD_SIZE;
    }

    public void getChessboard(ChessState[][] out) {
        for (int i = 0; i < GomokuHelper.CHESSBOARD_SIZE; i++) {
            System.arraycopy(this.chessboard[i], 0, out[i], 0, GomokuHelper.CHESSBOARD_SIZE);
        }
    }

    public int getHeight(int column) {
        return this.height[column];
    }
}
