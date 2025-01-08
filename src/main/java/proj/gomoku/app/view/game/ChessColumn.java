package proj.gomoku.app.view.game;

import javafx.animation.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import proj.gomoku.app.Options;
import proj.gomoku.app.view.Palette;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.GomokuHelper;

public class ChessColumn extends Pane {

    public static final int TRANSITION_MILLIS = 200;

    private final int column;
    private final DroppingGomokuGame game;
    private final Circle[] circles;

    private final FadeTransition bgTransition;
    private final FadeTransition pvTransition;

    private boolean preview = false;

    public ChessColumn(DroppingGomokuGame game, int column) {
        this.column = column;
        this.game = game;
        this.setOnMouseClicked(this::onMouseClicked);
        this.setOnMouseEntered(this::onMouseEntered);
        this.setOnMouseExited(this::onMouseExited);

        this.setPrefWidth(80);
        this.setPrefHeight(80 * GomokuHelper.CHESSBOARD_HEIGHT);

        this.setMaxWidth(80);
        this.setMaxHeight(80 * GomokuHelper.CHESSBOARD_HEIGHT);

        Rectangle rectangle = new Rectangle();
        rectangle.setArcWidth(30);
        rectangle.setArcHeight(30);
        rectangle.setOpacity(0.2);
        rectangle.setFill(Palette.COLOR_BG_NORMAL);

        rectangle.widthProperty().bind(this.widthProperty());
        rectangle.heightProperty().bind(this.heightProperty());

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.gray(0.7));
        shadow.setRadius(10);
        rectangle.setEffect(shadow);

        this.circles = new Circle[GomokuHelper.CHESSBOARD_HEIGHT + 1];
        for (int i = 0; i < GomokuHelper.CHESSBOARD_HEIGHT + 1; i++) {
            Circle circle = new Circle(40, 80 * GomokuHelper.CHESSBOARD_HEIGHT - 40 - i * 80, 32);
            circle.setVisible(false);
            circle.setOpacity(0.0);
            circle.setEffect(shadow);
            this.circles[i] = circle;
        }
        circles[0].setVisible(true);
        this.getChildren().add(rectangle);
        this.getChildren().addAll(this.circles);

        this.bgTransition = new FadeTransition(Duration.millis(TRANSITION_MILLIS), rectangle);
        this.pvTransition = new FadeTransition(Duration.millis(TRANSITION_MILLIS), circles[0]);
    }

    public void undo() {
        int height = this.game.getHeight(this.column);

        if (height != GomokuHelper.CHESSBOARD_HEIGHT - 1) {
            Circle prev = this.circles[height + 1];
            prev.setVisible(false);
        }

        Circle top = this.circles[height];
        top.setStroke(null);
        top.setOpacity(0.0);
        this.pvTransition.setNode(top);
    }

    public void reset() {
        this.circles[0].setVisible(true);
        this.circles[0].setOpacity(0.0);
        this.circles[0].setStroke(null);
        this.pvTransition.setNode(this.circles[0]);

        for (int i = 1; i < GomokuHelper.CHESSBOARD_HEIGHT; i++) {
            this.circles[i].setVisible(false);
            this.circles[i].setOpacity(0.0);
            this.circles[i].setStroke(null);
        }
    }

    public void place() {
        int height = this.game.getHeight(this.column);
        int y = 80 * GomokuHelper.CHESSBOARD_HEIGHT - 40 - height * 80;
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

        this.pvTransition.stop();
        top.setFill(this.game.getLastState() == ChessState.RED ? Palette.COLOR_RED_BASE : Palette.COLOR_BLUE_BASE);
        top.setStroke(this.game.getLastState() == ChessState.RED ? Palette.COLOR_RED_STROKE : Palette.COLOR_BLUE_STROKE);
        top.setStrokeWidth(8);
        top.setOpacity(1.0);

        if (this.game.canAdd(this.column)) {
            Circle next = this.circles[height];
            next.setVisible(true);
            next.setFill(this.getPreviewColor());
            this.pvTransition.setNode(next);
        } else {
            this.pvTransition.setNode(this.circles[this.circles.length - 1]);
        }
    }

    public void updateDisplay() {
        if (!this.isHover()) {
            return;
        }

        if (this.game.isAIRound() && Options.ENABLED_AI.getBooleanValue()) {
            return;
        }

        if (this.game.canAdd(this.column)) {
            int height = this.game.getHeight(this.column);
            Circle top = this.circles[height];
            top.setFill(this.getPreviewColor());
            this.showPreview();
        }
    }

    private void onMouseClicked(final MouseEvent event) {
        if (this.game.isAIRound() && Options.ENABLED_AI.getBooleanValue()) {
            return;
        }

        this.game.add(this.column);
    }

    private void onMouseEntered(final MouseEvent event) {
        if (this.game.canAdd(this.column)) {
            int height = this.game.getHeight(this.column);
            Circle top = this.circles[height];
            top.setFill(this.getPreviewColor());
        }

        this.playFadeIn();
    }

    private void onMouseExited(final MouseEvent event) {
        this.playFadeOut();
    }

    private void playFadeIn() {
        if (this.bgTransition.getStatus() == Animation.Status.RUNNING) {
            this.bgTransition.stop();
        }

        this.bgTransition.setFromValue(this.bgTransition.getNode().getOpacity());
        this.bgTransition.setToValue(1.0);
        this.bgTransition.play();

        if (this.game.isGameFinished() || (Options.ENABLED_AI.getBooleanValue() && this.game.isAIRound())) {
            return;
        }

        this.showPreview();
    }

    private void playFadeOut() {
        this.preview = false;
        if (this.bgTransition.getStatus() == Animation.Status.RUNNING) {
            this.bgTransition.stop();
        }

        this.bgTransition.setFromValue(this.bgTransition.getNode().getOpacity());
        this.bgTransition.setToValue(0.2);

        this.bgTransition.play();

        if (this.game.isGameFinished()) {
            return;
        }

        this.hidePreview();
    }

    private void showPreview() {
        this.preview = true;

        if (this.pvTransition.getStatus() == Animation.Status.RUNNING) {
            this.pvTransition.stop();
        }

        this.pvTransition.setFromValue(this.pvTransition.getNode().getOpacity());
        this.pvTransition.setToValue(1.0);
        this.pvTransition.play();

    }

    private void hidePreview() {
        this.preview = false;

        if (this.pvTransition.getStatus() == Animation.Status.RUNNING) {
            this.pvTransition.stop();
        }

        this.pvTransition.setFromValue(this.pvTransition.getNode().getOpacity());
        this.pvTransition.setToValue(0.0);
        this.pvTransition.play();
    }

    private Color getPreviewColor() {
        return this.game.getCurrentState() == ChessState.RED ? Palette.COLOR_RED_BASE : Palette.COLOR_BLUE_BASE;
    }
}
