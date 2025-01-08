package proj.gomoku.model;

import lombok.Getter;
import net.quepierts.papyri.event.OptionUpdateEvent;
import net.quepierts.papyri.event.PapyriEventBus;
import net.quepierts.papyri.model.option.BooleanOption;
import proj.gomoku.app.Options;
import proj.gomoku.model.event.GameFinishedEvent;
import proj.gomoku.model.event.GameResetEvent;
import proj.gomoku.model.event.PlacedChessEvent;
import proj.gomoku.model.event.UndoChessEvent;

import java.util.Arrays;
import java.util.Stack;

public class DroppingGomokuGame {
    @Getter
    private final ChessState[][] chessboard = new ChessState[GomokuHelper.CHESSBOARD_WIDTH][GomokuHelper.CHESSBOARD_HEIGHT];
    private final int[] height = new int[GomokuHelper.CHESSBOARD_WIDTH];

    private final Stack<ImmutableIntegerPair> steps = new Stack<>();
    private final boolean silent;

    @Getter
    private ChessState current = ChessState.BLUE;
    @Getter
    private ChessState winner = ChessState.NONE;
    @Getter
    private Direction winDirection = null;

    private boolean dirty = false;

    public DroppingGomokuGame(boolean silent) {
        this.silent = silent;
        for (ChessState[] states : this.chessboard) {
            Arrays.fill(states, ChessState.NONE);
        }
    }

    public boolean add(int column) {
        if (this.isGameFinished()) {
            return false;
        }

        if (!this.canAdd(column)) {
            return false;
        }

        this.dirty = true;
        int row = this.height[column];

        this.chessboard[column][row] = this.current;
        this.steps.push(new ImmutableIntegerPair(column, row));
        this.height[column]++;

        if (this.steps.size() > ((GomokuHelper.REQUIRED_LENGTH - 1) * 2) && isWin(column, row)) {
            this.winner = this.current;

            if (!this.silent) {
                PapyriEventBus.post(new GameFinishedEvent(this));
            }
        } else if (this.steps.size() == GomokuHelper.CHESSBOARD_WIDTH * GomokuHelper.CHESSBOARD_HEIGHT) {
            if (!this.silent) {
                PapyriEventBus.post(new GameFinishedEvent(this));
            }
        }

        ChessState last = this.current;
        this.current = ChessState.getOpposite(this.current);
        if (!this.silent) {
            PapyriEventBus.post(new PlacedChessEvent(this, column, row, last));
        }
        return true;
    }

    public boolean undo() {
        if (this.steps.isEmpty()) {
            return false;
        }

        ImmutableIntegerPair packed = this.steps.pop();
        final int column = packed.x();
        final int row = packed.y();
        this.current = ChessState.getOpposite(this.current);
        this.winner = ChessState.NONE;
        this.winDirection = null;
        this.chessboard[column][row] = ChessState.NONE;
        this.height[column] --;

        if (!this.silent) {
            PapyriEventBus.post(new UndoChessEvent(this, column, row, this.current));
        }
        return true;
    }

    public boolean isGameFinished() {
        return this.winner != ChessState.NONE || this.steps.size() == GomokuHelper.CHESSBOARD_WIDTH * GomokuHelper.CHESSBOARD_HEIGHT;
    }

    public boolean isPlayerRound() {
        return this.current == GomokuHelper.PLAYER_STATE;
    }

    public boolean isAIRound() {
        return this.current == GomokuHelper.AI_STATE;
    }

    public boolean isGameStarted() {
        return !this.steps.isEmpty();
    }

    public void reset() {
        if (!this.dirty) {
            return;
        }

        this.dirty = false;
        for (ChessState[] states : this.chessboard) {
            Arrays.fill(states, ChessState.NONE);
        }
        Arrays.fill(this.height, 0);

        this.steps.clear();
        this.current = Options.BLUE_RED.getBooleanValue() ? ChessState.RED : ChessState.BLUE;
        this.winner = ChessState.NONE;
        this.winDirection = null;

        if (!this.silent) {
            PapyriEventBus.post(new GameResetEvent(this));
        }
    }

    public ImmutableIntegerPair getLastStep() {
        if (this.steps.isEmpty()) {
            return null;
        }
        return this.steps.peek();
    }

    public ChessState getLastState() {
        return ChessState.getOpposite(this.current);
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
                this.winDirection = direction;
                return true;
            }

            count += GomokuHelper.countDirection(column, row, -direction.getX(), -direction.getY(), this.chessboard, type);

            if (count >= GomokuHelper.REQUIRED_LENGTH) {
                this.winDirection = direction;
                return true;
            }
        }

        return false;
    }

    public boolean canAdd(int column) {
        return !isGameFinished() && isValidColumn(column) && this.height[column] < GomokuHelper.CHESSBOARD_HEIGHT;
    }

    public boolean isValidColumn(int column) {
        return column >= 0 && column < GomokuHelper.CHESSBOARD_WIDTH;
    }

    public void getChessboard(ChessState[][] out) {
        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            System.arraycopy(this.chessboard[i], 0, out[i], 0, GomokuHelper.CHESSBOARD_HEIGHT);
        }
    }

    public int getHeight(int column) {
        return this.height[column];
    }

    public int getColumns() {
        return this.height.length;
    }

    public ChessState getState(int column, int row) {
        if (column < 0 || column >= GomokuHelper.CHESSBOARD_WIDTH
                || row < 0 || row >= GomokuHelper.CHESSBOARD_HEIGHT) {
            return ChessState.NONE;
        }
        return this.chessboard[column][row];
    }

    public void onOptionUpdate(OptionUpdateEvent<BooleanOption> event) {
        if (event.getOption() == Options.BLUE_RED && !this.isGameFinished()) {
            this.current = event.getOption().getBooleanValue() ? ChessState.RED : ChessState.BLUE;
        }
    }
}
