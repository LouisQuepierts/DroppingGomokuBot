package proj.gomoku.app.view;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import lombok.Setter;
import proj.gomoku.model.AABB;
import proj.gomoku.model.MathHelper;

public class Card extends StackPane {
    private double xOffset = 0;
    private double yOffset = 0;

    @Setter
    private AABB bound;

    public Card(double width, double height) {
        this.getChildren().add(new BaseBoard(width, height, false));
        this.setMinWidth(width);
        this.setMinHeight(height);
        this.setMaxWidth(width);
        this.setMaxHeight(height);

        this.setOnMousePressed(this::onMousePressed);
        this.setOnMouseDragged(this::onMouseDragged);
    }

    private void onMousePressed(MouseEvent event) {
        this.xOffset = event.getSceneX() - this.getTranslateX();
        this.yOffset = event.getSceneY() - this.getTranslateY();
    }

    private void onMouseDragged(MouseEvent event) {
        double x = event.getSceneX() - this.xOffset;
        double y = event.getSceneY() - this.yOffset;

        if (this.bound != null) {
            double width = this.getWidth() / 2;
            double height = this.getHeight() / 2;
            x = MathHelper.clamp(x, this.bound.x0() + width, this.bound.x1() - width);
            y = MathHelper.clamp(y, this.bound.y0() + height, this.bound.y1() - height);
        }

        this.setTranslateX(x);
        this.setTranslateY(y);
    }
}
