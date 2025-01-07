package proj.gomoku.app.view.option;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import proj.gomoku.app.Options;
import proj.gomoku.app.view.BaseBoard;
import proj.gomoku.model.GomokuHelper;

public class OptionsPane extends StackPane {
    public OptionsPane() {
        this.setMinWidth(360);

        Rectangle baseBoard = new BaseBoard(240, 80 * GomokuHelper.CHESSBOARD_HEIGHT + 40);
        VBox vBox = new VBox();
        vBox.setMaxWidth(220);
        vBox.setMaxHeight(80 * GomokuHelper.CHESSBOARD_HEIGHT);

        vBox.getChildren().addAll(
                new BooleanOptionEntry(Options.ENABLED_AI),
                new BooleanOptionEntry(Options.BLUE_RED),
                new BooleanOptionEntry(Options.ENABLED_DEBUG),
                new BooleanOptionEntry(Options.STEP_DEBUG),
                new IntegerOptionEntry(Options.STEP_DELAY)
        );

        this.getChildren().addAll(baseBoard, vBox);
    }
}
