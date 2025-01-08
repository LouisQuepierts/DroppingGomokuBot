package proj.gomoku.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.quepierts.papyri.event.OptionUpdateEvent;
import net.quepierts.papyri.event.PapyriEventBus;
import proj.gomoku.app.view.ChessBoardPane;
import proj.gomoku.app.view.ClickButton;
import proj.gomoku.app.view.debug.DebugPane;
import proj.gomoku.app.view.option.OptionsPane;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.bot.BotHandler;
import proj.gomoku.model.event.CloseApplicationEvent;

public class GomokuApplication extends Application {
    public static final int SCENE_WIDTH = 800;
    public static final int SCENE_HEIGHT = 600;

    private final DroppingGomokuGame game = new DroppingGomokuGame(false);
    private final BotHandler handler = new BotHandler(game);

    @Override
    public void start(Stage stage) throws Exception {
        PapyriEventBus.subscribe(this.handler);
        PapyriEventBus.subscribe(OptionUpdateEvent.class, this.game::onOptionUpdate);
        this.initScene(stage);
    }

    private void initScene(Stage stage) {
        BorderPane pane = new BorderPane();

        Pane chessBoardPane = new ChessBoardPane(game);

        OptionsPane optionsPane = new OptionsPane();
        DebugPane debugPane = new DebugPane();
        Pane bottom = this.initBottom();
        debugPane.setVisible(false);

        pane.setCenter(chessBoardPane);
        pane.setLeft(optionsPane);
        pane.setRight(debugPane);
        pane.setBottom(bottom);

        double minWidth = chessBoardPane.getMinWidth() + optionsPane.getMinWidth() + debugPane.getMinWidth();

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setMinWidth(minWidth);
        stage.setMinHeight(SCENE_HEIGHT + 40);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> PapyriEventBus.post(new CloseApplicationEvent()));

        pane.requestFocus();

        PapyriEventBus.subscribe(OptionUpdateEvent.class, event -> {
            if (event.getOption() == Options.ENABLED_DEBUG) {
                boolean value = Options.ENABLED_DEBUG.getBooleanValue();
                debugPane.setVisible(value);

                if (!value) {
                    Options.STEP_DEBUG.setValue(false);
                }
            }
        });
    }

    private Pane initBottom() {
        ClickButton undo = new ClickButton(160, 32, "Undo");
        undo.setNormalColor(Color.rgb(228,119,11));
        undo.setHoveredColor(Color.rgb(236,135,14));
        undo.setPressedColor(Color.rgb(245,177,109));
        undo.setOnMouseClicked(event -> this.game.undo());

        ClickButton reset = new ClickButton(160, 32, "Reset");
        reset.setNormalColor(Color.rgb(110,195,201));
        reset.setHoveredColor(Color.rgb(153,209,211));
        reset.setPressedColor(Color.rgb(202,229,232));
        reset.setOnMouseClicked(event -> this.game.reset());

        HBox bottom = new HBox(8);
        bottom.setPadding(new Insets(0, 0, 10, 0));
        bottom.getChildren().addAll(undo, reset);
        bottom.setAlignment(Pos.CENTER);
        return bottom;
    }
}
