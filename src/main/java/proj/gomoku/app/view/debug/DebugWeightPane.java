package proj.gomoku.app.view.debug;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import proj.gomoku.app.view.Card;
import proj.gomoku.model.GomokuHelper;

import java.util.Arrays;

public class DebugWeightPane extends Card {
    private static final Font FONT = new Font("Consolas", 16);
    private final Label[] labels;
    public DebugWeightPane() {
        super(200, GomokuHelper.CHESSBOARD_WIDTH * 20 + 40);

        VBox vBox = new VBox(4);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setPadding(new Insets(0, 10, 0, 10));
        this.labels = new Label[GomokuHelper.CHESSBOARD_WIDTH];
        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            this.labels[i] = new Label(i + " ");
            this.labels[i].setFont(FONT);
        }

        vBox.getChildren().addAll(this.labels);
        this.getChildren().add(vBox);
    }

    public void setLine(int i, int[] weights) {
        this.labels[i].setText(i + " " + Arrays.toString(weights));
    }
}
