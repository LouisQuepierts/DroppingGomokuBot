package proj.gomoku.app.view;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BaseBoard extends Rectangle {
    public BaseBoard(double width, double height, boolean inner) {
        this.setWidth(width);
        this.setHeight(height);
        this.setArcWidth(30);
        this.setArcHeight(30);
        this.setFill(Color.gray(0.95));

        if (inner) {
            InnerShadow shadow = new InnerShadow();
            shadow.setRadius(10);
            shadow.setColor(Color.gray(0.7));

            this.setEffect(shadow);
        } else {
            DropShadow shadow = new DropShadow();
            shadow.setRadius(10);
            shadow.setColor(Color.gray(0.7));

            this.setEffect(shadow);
        }
    }

    public BaseBoard(double width, double height) {
        this.setWidth(width);
        this.setHeight(height);
        this.setArcWidth(30);
        this.setArcHeight(30);
        this.setFill(Color.gray(0.95));

        InnerShadow thisShadow = new InnerShadow();
        thisShadow.setRadius(10);
        thisShadow.setColor(Color.gray(0.7));

        this.setEffect(thisShadow);
    }
}
