package proj.gomoku.app.view.debug;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import proj.gomoku.app.view.BaseBoard;
import proj.gomoku.model.AABB;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.GomokuHelper;
import proj.gomoku.model.ImmutableIntegerPair;
import proj.gomoku.model.debug.DebugHandler;
import proj.gomoku.model.debug.OperationType;

import java.util.List;

public class DebugPane extends StackPane {
    private final DebugChessboardPane chessboardPane;

    public DebugPane() {
        this.setMinWidth(360);

        Rectangle baseBoard = new BaseBoard(300, 80 * GomokuHelper.CHESSBOARD_HEIGHT + 40);
        double width = baseBoard.getWidth() / 2 - 10;
        double height = baseBoard.getHeight() / 2 - 10;
        AABB bound = new AABB(-width, -height, width, height);

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        this.chessboardPane = new DebugChessboardPane();
        this.chessboardPane.setBound(bound);
        vBox.getChildren().add(this.chessboardPane);

        this.getChildren().addAll(baseBoard, vBox);
        DebugHandler.bind(this);
    }

    public void update(ChessState[][] chessboard) {
        this.chessboardPane.update(chessboard);
    }

    public void setStatus(int i, int j, ChessState state) {
        this.chessboardPane.setStatus(i, j, state);
    }

    public void fillFrames(List<ImmutableIntegerPair> pairs, OperationType type) {
        this.chessboardPane.fillFrames(pairs, type);
    }

    public void reset() {
        this.chessboardPane.reset();
    }
}
