package proj.gomoku.app.view.option;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import net.quepierts.papyri.model.option.OptionEntry;
import proj.gomoku.app.view.DoublePropertyTransition;

public abstract class AbstractOptionEntry<T> extends StackPane {
    private static final Font FONT = Font.font("Consolas", 16);

    private final DoublePropertyTransition backgroundHovered;

    public AbstractOptionEntry(OptionEntry<T> option) {
        Color gray = Color.gray(244.0 / 255.0);
        Stop[] stops = new Stop[] {
                new Stop(0, gray),
                new Stop(0.3, Color.WHITE),
                new Stop(0.7, Color.WHITE),
                new Stop(1, gray)
        };
        LinearGradient fill = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        Rectangle background = new Rectangle(220, 48);
        background.setFill(fill);
        background.setOpacity(0.0);

        this.getChildren().add(background);

        this.backgroundHovered = new DoublePropertyTransition(Duration.millis(200), background.opacityProperty());

        this.setOnMouseEntered(this::onMouseEntered);
        this.setOnMouseExited(this::onMouseExited);

        this.initContent(option);

        option.setCallback(this::onOptionChanged);
    }

    protected void onOptionChanged(T t) {

    }

    protected abstract Node getInputWidget(OptionEntry<T> option);

    private void initContent(OptionEntry<T> option) {
        Label label = new Label(option.getName());
        label.setFont(FONT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(label, spacer, this.getInputWidget(option));

        this.getChildren().add(hBox);
    }

    private void onMouseEntered(MouseEvent event) {
        this.backgroundHovered.setFrom(this.backgroundHovered.getProperty());
        this.backgroundHovered.setTo(0.8);
        this.backgroundHovered.play();
    }

    private void onMouseExited(MouseEvent event) {
        this.backgroundHovered.setFrom(this.backgroundHovered.getProperty());
        this.backgroundHovered.setTo(0.0);
        this.backgroundHovered.play();
    }
}
