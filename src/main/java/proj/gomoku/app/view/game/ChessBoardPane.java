package proj.gomoku.app.view.game;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import net.quepierts.papyri.annotation.HandleEvent;
import net.quepierts.papyri.event.PapyriEventBus;
import proj.gomoku.app.view.BaseBoard;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.GomokuHelper;
import proj.gomoku.model.ImmutableIntegerPair;
import proj.gomoku.model.Direction;
import proj.gomoku.model.event.GameFinishedEvent;
import proj.gomoku.model.event.GameResetEvent;
import proj.gomoku.model.event.PlacedChessEvent;
import proj.gomoku.model.event.UndoChessEvent;

public class ChessBoardPane extends StackPane {
    private final ChessColumn[] chessColumns = new ChessColumn[GomokuHelper.CHESSBOARD_WIDTH];
    private final Line line;

    public ChessBoardPane(DroppingGomokuGame game) {
        BaseBoard baseBoard = new BaseBoard(80 * (GomokuHelper.CHESSBOARD_WIDTH + 1), 80 * GomokuHelper.CHESSBOARD_HEIGHT + 40);
        this.setMinWidth(80 * (GomokuHelper.CHESSBOARD_WIDTH + 1));
        this.setMinHeight(80 * GomokuHelper.CHESSBOARD_HEIGHT + 40);

        HBox columnsPane = new HBox(8);
        columnsPane.setAlignment(Pos.CENTER);
        columnsPane.setPadding(new Insets(10, 10, 10, 10));

        ObservableList<Node> children = columnsPane.getChildren();
        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            ChessColumn chessColumn = new ChessColumn(game, i);
            children.add(chessColumn);
            this.chessColumns[i] = chessColumn;
        }

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setColor(Color.GOLD);

        this.line = new Line();
        this.line.setStrokeWidth(4.0);
        this.line.setVisible(false);
        this.line.setStroke(Color.WHITE);
        this.line.setEffect(shadow);

        this.getChildren().addAll(baseBoard, columnsPane, this.line);
        PapyriEventBus.subscribe(this);
    }

    @HandleEvent
    public void onPlacedChess(final PlacedChessEvent event) {
        int column = event.getColumn();
        this.chessColumns[column].place();

        for (ChessColumn wgt : this.chessColumns) {
            wgt.updateDisplay();
        }
    }

    @HandleEvent
    public void onUndoChess(final UndoChessEvent event) {
        int column = event.getColumn();
        this.chessColumns[column].undo();
        this.line.setVisible(false);
    }

    @HandleEvent
    public void onGameReset(final GameResetEvent event) {
        for (ChessColumn column : this.chessColumns) {
            column.reset();
        }
        this.line.setVisible(false);
    }

    @HandleEvent
    public void onGameFinished(final GameFinishedEvent event) {
        DroppingGomokuGame game = event.getGame();
        ChessState winner = game.getWinner();
        if (winner != ChessState.NONE) {
            Direction direction = game.getWinDirection();
            ImmutableIntegerPair lastStep = game.getLastStep();

            int c0 = lastStep.x();
            int r0 = lastStep.y();
            int c1 = c0;
            int r1 = r0;

            while (game.getState(c0 - direction.getX(), r0 - direction.getY()) == winner) {
                c0 -= direction.getX();
                r0 -= direction.getY();
            }

            while (game.getState(c1 + direction.getX(), r1 + direction.getY()) == winner) {
                c1 += direction.getX();
                r1 += direction.getY();
            }

            float x = (c0 + c1 + 1 - GomokuHelper.CHESSBOARD_WIDTH) / 2.0f;
            float y = (GomokuHelper.CHESSBOARD_HEIGHT - r0 - r1 - 1) / 2.0f;

            if (direction == Direction.ANTI_DIAGONAL) {
                direction = Direction.MAIN_DIAGONAL;
            } else if (direction == Direction.MAIN_DIAGONAL) {
                direction = Direction.ANTI_DIAGONAL;
            }

            this.line.setVisible(true);
            this.line.setTranslateX(x * 88);
            this.line.setTranslateY(y * 80);
            this.line.setEndX(direction.getX() * 88 * GomokuHelper.REQUIRED_LENGTH);
            this.line.setEndY(direction.getY() * 80 * GomokuHelper.REQUIRED_LENGTH);
        }
    }
}
