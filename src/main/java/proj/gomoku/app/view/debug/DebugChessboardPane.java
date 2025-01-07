package proj.gomoku.app.view.debug;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import proj.gomoku.app.view.Card;
import proj.gomoku.app.view.Palette;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.GomokuHelper;
import proj.gomoku.model.ImmutableIntegerPair;
import proj.gomoku.model.debug.OperationType;

import java.util.List;

public class DebugChessboardPane extends Card {
    private static final Color[] STATUS_COLOR;
    private static final Color[] OPERATION_COLOR;
    private final Rectangle[][] status;

    public DebugChessboardPane() {
        super(32 * GomokuHelper.CHESSBOARD_WIDTH, 32 * GomokuHelper.CHESSBOARD_HEIGHT);
        this.status = new Rectangle[GomokuHelper.CHESSBOARD_WIDTH][GomokuHelper.CHESSBOARD_HEIGHT];

        Color fill = STATUS_COLOR[0];

        float halfWidth = (GomokuHelper.CHESSBOARD_WIDTH - 1) / 2.0f;
        float halfHeight = (GomokuHelper.CHESSBOARD_HEIGHT - 1) / 2.0f;

        ObservableList<Node> children = this.getChildren();

        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            for (int j = 0; j < GomokuHelper.CHESSBOARD_HEIGHT; j++) {
                Rectangle rectangle = new Rectangle(24, 24);
                rectangle.setTranslateX((i - halfWidth) * 28.0);
                rectangle.setTranslateY((GomokuHelper.CHESSBOARD_HEIGHT - 1 - j - halfHeight) * 28.0);
                rectangle.setArcWidth(10);
                rectangle.setArcHeight(10);
                rectangle.setFill(fill);
                rectangle.setStrokeWidth(4);

                this.status[i][j] = rectangle;
            }
            children.addAll(this.status[i]);
        }
    }

    public void update(ChessState[][] chessboard) {
        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            for (int j = 0; j < GomokuHelper.CHESSBOARD_HEIGHT; j++) {
                this.status[i][j].setFill(STATUS_COLOR[chessboard[i][j].ordinal()]);
            }
        }
    }

    public void setStatus(int i, int j, ChessState state) {
        if (!this.isValidPosition(i, j)) {
            return;
        }

        this.status[i][j].setFill(STATUS_COLOR[state.ordinal()]);
    }

    public void fillFrames(List<ImmutableIntegerPair> pairs, OperationType type) {
        for (ImmutableIntegerPair pair : pairs) {
            if (!this.isValidPosition(pair.x(), pair.y())) {
                continue;
            }

            this.status[pair.x()][pair.y()].setStroke(OPERATION_COLOR[type.ordinal()]);
        }
    }

    public void reset() {
        for (Rectangle[] rectangles : this.status) {
            for (Rectangle rectangle : rectangles) {
                rectangle.setFill(STATUS_COLOR[0]);
                rectangle.setStroke(null);
            }
        }
    }

    private boolean isValidPosition(int i, int j) {
        return i > -1 && j > -1 && i < GomokuHelper.CHESSBOARD_WIDTH && j < GomokuHelper.CHESSBOARD_HEIGHT;
    }

    static {
        STATUS_COLOR = new Color[ChessState.values().length];
        STATUS_COLOR[ChessState.NONE.ordinal()] = Color.gray(0.75);
        STATUS_COLOR[ChessState.BLUE.ordinal()] = Palette.COLOR_BLUE_BASE;
        STATUS_COLOR[ChessState.RED.ordinal()] = Palette.COLOR_RED_BASE;

        OPERATION_COLOR = new Color[OperationType.values().length];
        OPERATION_COLOR[OperationType.SEARCHING.ordinal()] = Color.ORANGE;
        OPERATION_COLOR[OperationType.ACCEPTED.ordinal()] = Color.rgb(119, 206, 121);
        OPERATION_COLOR[OperationType.BLOCKED.ordinal()] = Color.rgb(255, 0, 0);
        OPERATION_COLOR[OperationType.HIGHLIGHT.ordinal()] = Color.YELLOW;
    }
}
