package proj.gomoku.app.view;

import javafx.animation.FillTransition;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import lombok.Setter;

public class ClickButton extends StackPane {
    private static final Font FONT = Font.font("Consolas", 16);

    private Color normalColor = Color.gray(0.6);
    @Setter private Color hoveredColor = Color.gray(0.75);
    @Setter private Color pressedColor = Color.rgb(204, 231, 253);

    private final Rectangle rectangle;
    private final Label label;
    private final FillTransition transition;

    public ClickButton(double width, double height, String text) {
        this.rectangle = new Rectangle(width, height);
        this.rectangle.setArcWidth(30);
        this.rectangle.setArcHeight(30);
        this.transition = new FillTransition(Duration.millis(100), this.rectangle);

        this.rectangle.setFill(normalColor);

        this.label = new Label(text);
        this.label.setFont(FONT);

        this.getChildren().addAll(this.rectangle, label);

        this.setOnMouseEntered(this::onMouseEntered);
        this.setOnMouseExited(this::onMouseExited);
        this.setOnMousePressed(this::onMousePressed);
        this.setOnMouseReleased(this::onMouseReleased);
    }

    public void setNormalColor(Color normalColor) {
        this.normalColor = normalColor;
        this.rectangle.setFill(normalColor);
    }

    public void setTextColor(Color textColor) {
        this.label.setTextFill(textColor);
    }

    private void onMouseEntered(MouseEvent event) {
        this.changeColor(hoveredColor);
    }

    private void onMouseExited(MouseEvent event) {
        this.changeColor(normalColor);
    }

    private void onMousePressed(MouseEvent event) {
        this.changeColor(pressedColor);
    }

    private void onMouseReleased(MouseEvent event) {
        this.changeColor(hoveredColor);
    }

    private void changeColor(Color color) {
        this.transition.stop();
        this.transition.setFromValue((Color) this.rectangle.getFill());
        this.transition.setToValue(color);
        this.transition.play();
    }
}
