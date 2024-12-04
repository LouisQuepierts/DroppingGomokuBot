package proj.gomoku.app;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import proj.gomoku.app.component.Barrel;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.GomokuHelper;
import proj.gomoku.model.bot.GomokuBot;

public class GomokuApplication extends Application {
    public static final int SCENE_WIDTH = 800;
    public static final int SCENE_HEIGHT = 600;

    private final DroppingGomokuGame game = new DroppingGomokuGame();
    private final GomokuBot bot = new GomokuBot(this.game);
    private final Barrel[] barrels = new Barrel[GomokuHelper.CHESSBOARD_SIZE];

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane pane = new BorderPane();

        Pane center = new Pane();
        ObservableList<Node> children = center.getChildren();
        for (int i = 0; i < GomokuHelper.CHESSBOARD_SIZE; i++) {
            Barrel barrel = new Barrel(this.game, i);
            barrel.setLayoutX(i * 85);
            children.add(barrel);
            this.barrels[i] = barrel;
        }

        Button button = new Button("Undo");
        button.setPrefWidth(80);
        button.setPrefHeight(32);
        button.setTranslateY(400);
        button.setOnMouseClicked(event -> {
            int lastStep = this.game.getLastStep();
            if (this.game.undo()) {
                int column = GomokuHelper.getColumn(lastStep);
                this.barrels[column].undo();
            }
        });
        children.add(button);
        pane.setCenter(center);

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setWidth(SCENE_WIDTH);
        stage.setMinWidth(SCENE_WIDTH);
        stage.setMaxWidth(SCENE_WIDTH);
        stage.setHeight(SCENE_HEIGHT);
        stage.setMinHeight(SCENE_HEIGHT);
        stage.setMaxHeight(SCENE_HEIGHT);
        stage.show();
    }
}
