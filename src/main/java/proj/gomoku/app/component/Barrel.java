package proj.gomoku.app.component;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.GomokuHelper;

public class Barrel extends Pane {
    public static final Color COLOR_BLUE_PREVIEW = Color.rgb(150, 220, 248);
    public static final Color COLOR_BLUE_PLACED = Color.rgb(83, 197, 243);
    public static final Color COLOR_RED_PREVIEW = Color.rgb(252, 104, 104);
    public static final Color COLOR_RED_PLACED = Color.rgb(252, 33, 33);

    private final int column;
    private final DroppingGomokuGame game;
    private final ObjectProperty<Paint> background;
    private final Circle[] circles;

    public Barrel(DroppingGomokuGame game, int column) {
        this.column = column;
        this.game = game;
        this.setOnMouseClicked(this::onMouseClicked);
        this.setOnMouseEntered(this::onMouseEntered);
        this.setOnMouseExited(this::onMouseExited);

        this.setPrefWidth(80);
        this.setPrefHeight(80 * GomokuHelper.CHESSBOARD_SIZE);
        Rectangle rectangle = new Rectangle();
        rectangle.setArcWidth(30);
        rectangle.setArcHeight(30);
        this.background = rectangle.fillProperty();
        rectangle.setFill(Color.GRAY);

        rectangle.widthProperty().bind(this.widthProperty());
        rectangle.heightProperty().bind(this.heightProperty());
        
        this.circles = new Circle[GomokuHelper.CHESSBOARD_SIZE];
        for (int i = 0; i < GomokuHelper.CHESSBOARD_SIZE; i++) {
            Circle circle = new Circle(40, 80 * GomokuHelper.CHESSBOARD_SIZE - 40 - i * 80, 32, Color.GRAY);
            circle.setVisible(false);
            this.circles[i] = circle;
        }
        this.circles[0].setVisible(true);
        this.getChildren().add(rectangle);
        this.getChildren().addAll(this.circles);
    }
    
    public void undo() {
        int height = this.game.getHeight(this.column);
        this.circles[height].setVisible(false);
    }

    private void getTop() {
        int height = this.game.getHeight(this.column);
        int y = 80 * GomokuHelper.CHESSBOARD_SIZE - 40 - height * 80;
        Circle top = this.circles[height];
        if (top.getCenterY() != y) {
            Path path = new Path();
            path.getElements().add(new MoveTo(40, 40));
            path.getElements().add(new MoveTo(40, y + 80));

            Timeline timeline = new Timeline();
            int bottom = y + 80;
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(top.centerYProperty(), 40)),
                    new KeyFrame(Duration.millis(bottom - 40), new KeyValue(top.centerYProperty(), bottom))
            );
            timeline.play();

            top.setFill(this.getPlacedColor());
        }
    }

    private void onMouseClicked(final MouseEvent event) {
        if (this.game.add(this.column)) {
            int height = this.game.getHeight(this.column);
            int y = 80 * GomokuHelper.CHESSBOARD_SIZE - 40 - height * 80;
            Circle top = this.circles[height - 1];

            Path path = new Path();
            path.getElements().add(new MoveTo(40, 40));
            path.getElements().add(new MoveTo(40, y + 80));

            Timeline timeline = new Timeline();
            int bottom = y + 80;
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(top.centerYProperty(), 40)),
                    new KeyFrame(Duration.millis(bottom - 40), new KeyValue(top.centerYProperty(), bottom))
            );
            timeline.play();

            top.setFill(this.getPlacedColor());

            Circle next = this.circles[height];
            next.setVisible(true);
            next.setFill(this.getPreviewColor());
        }
    }

    private void onMouseEntered(final MouseEvent event) {
        this.background.setValue(Color.DARKGRAY);
        int height = this.game.getHeight(this.column);
        Circle top = this.circles[height];

        top.setFill(this.getPreviewColor());
        top.setVisible(true);
    }

    private void onMouseExited(final MouseEvent event) {
        this.background.setValue(Color.GRAY);
        int height = this.game.getHeight(this.column);
        Circle top = this.circles[height];
        top.setVisible(false);
    }

    private Color getPlacedColor() {
        return this.game.getLastState() == ChessState.RED ? COLOR_RED_PLACED : COLOR_BLUE_PLACED;
    }

    private Color getPreviewColor() {
        return this.game.getCurrentState() == ChessState.RED ? COLOR_RED_PREVIEW : COLOR_BLUE_PREVIEW;
    }
}
