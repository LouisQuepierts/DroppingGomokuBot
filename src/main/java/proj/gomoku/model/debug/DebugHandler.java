package proj.gomoku.model.debug;

import javafx.application.Platform;
import proj.gomoku.app.Options;
import proj.gomoku.app.view.debug.DebugPane;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.GomokuHelper;
import proj.gomoku.model.ImmutableIntegerPair;

import java.util.Arrays;
import java.util.List;

public class DebugHandler {
    private static final ChessState[][] cached = new ChessState[GomokuHelper.CHESSBOARD_WIDTH][GomokuHelper.CHESSBOARD_HEIGHT];

    private static DebugPane pane;

    public static void refill(DroppingGomokuGame game) {
        if (pane != null) {
            pane.update(game.getChessboard());
        }
    }

    public static void set(int i, int j, ChessState state, boolean delay) {
        if (pane != null) {
            Platform.runLater(() -> pane.setStatus(i, j, state));

            if (delay) {
                delay();
            }
        }
    }

    public static void fill(List<ImmutableIntegerPair> pairs, OperationType type, boolean delay) {
        if (pane != null) {
            Platform.runLater(() -> pane.fillFrames(pairs, type));

            if (delay) {
                delay();
            }
        }
    }

    public static void reset() {
        if (pane != null) {
            pane.reset();
        }
    }

    public static void bind(DebugPane debugPane) {
        pane = debugPane;
    }

    private static void delay() {
        if (!Options.STEP_DEBUG.getBooleanValue() && Options.STEP_DELAY.getValue() < 50) {
            return;
        }

        try {
            Thread.sleep(Options.STEP_DELAY.getValue());
        } catch (InterruptedException ignored) {}
    }

    static {
        for (ChessState[] states : cached) {
            Arrays.fill(states, ChessState.NONE);
        }
    }
}
