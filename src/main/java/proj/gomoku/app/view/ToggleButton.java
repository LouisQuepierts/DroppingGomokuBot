package proj.gomoku.app.view;

import javafx.animation.FillTransition;
import javafx.animation.StrokeTransition;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public class ToggleButton extends StackPane {
    private final Color BUTTON_DISABLED = Color.gray(0.45);
    private final Color BUTTON_ENABLED = Color.gray(0.95);

    private final Color BACKGROUND_DISABLED = Color.gray(0.75);
    private final Color BACKGROUND_ENABLED = Color.rgb(118, 178, 109);

    @Getter
    private boolean enabled = false;

    private final DoublePropertyTransition buttonPress;
    private final DoublePropertyTransition buttonTranslate;

    private FillTransition buttonColor;
    private FillTransition backgroundColor;
    private StrokeTransition strokeColor;

    private final Circle button;
    private final Rectangle background;

    @Setter
    private Consumer<Boolean> bound;

    public ToggleButton(Consumer<Boolean> bound, boolean def) {
        this();
        this.bound = bound;

        if (def) {
            this.toggle();
        }
    }

    public ToggleButton() {
        this.background = new Rectangle(45, 25);
        this.background.setArcWidth(25);
        this.background.setArcHeight(25);
        this.background.setFill(Color.gray(0.75));
        this.background.setStroke(Color.gray(0.45));
        this.background.setStrokeWidth(2);

        this.button = new Circle(8);
        this.button.setFill(Color.gray(0.45));
        this.button.setTranslateX(-10);

        this.buttonPress = new DoublePropertyTransition(Duration.millis(100), this.button.radiusProperty());
        this.buttonTranslate = new DoublePropertyTransition(Duration.millis(120), this.button.translateXProperty());
        this.buttonColor = new FillTransition(Duration.millis(120), this.button);
        this.backgroundColor = new FillTransition(Duration.millis(120), this.background);
        this.strokeColor = new StrokeTransition(Duration.millis(120), this.background);
        this.getChildren().addAll(this.background, this.button);

        this.setOnMousePressed(this::onMousePressed);
        this.setOnMouseReleased(this::onMouseReleased);
    }

    private void onMouseReleased(MouseEvent event) {
        this.toggle();
    }

    private void onMousePressed(MouseEvent event) {
        this.buttonPress.setFrom(this.buttonPress.getProperty());
        this.buttonPress.setTo(this.enabled ? 11 : 10);
        this.buttonPress.play();
    }

    public void toggle() {
        this.buttonPress.setFrom(this.buttonPress.getProperty());
        this.buttonPress.setTo(this.enabled ? 8 : 9);
        this.buttonPress.play();

        this.buttonTranslate.setFrom(this.buttonTranslate.getProperty());
        this.buttonTranslate.setTo(this.enabled ? -10 : 10);
        this.buttonTranslate.play();

        this.buttonColor.setFromValue((Color) this.button.getFill());
        this.buttonColor.setToValue(this.enabled ? BUTTON_DISABLED : BUTTON_ENABLED);
        this.buttonColor.play();

        this.backgroundColor.setFromValue((Color) this.background.getFill());
        this.backgroundColor.setToValue(this.enabled ? BACKGROUND_DISABLED : BACKGROUND_ENABLED);
        this.backgroundColor.play();

        this.strokeColor.setFromValue((Color) this.background.getStroke());
        this.strokeColor.setToValue(this.enabled ? BUTTON_DISABLED : BACKGROUND_ENABLED);
        this.strokeColor.play();

        this.enabled = !enabled;

        if (this.bound != null) {
            this.bound.accept(this.enabled);
        }
    }
}
