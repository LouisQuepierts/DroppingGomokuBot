package proj.gomoku.app.view;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import net.quepierts.papyri.annotation.HandleEvent;
import proj.gomoku.app.Options;
import proj.gomoku.model.event.GameFinishedEvent;
import proj.gomoku.model.event.GameResetEvent;
import proj.gomoku.model.event.UndoChessEvent;

public class GameFinishPane extends Card {
    private final Label message;
    public GameFinishPane() {
        super(300, 200);

        Label title = new Label("Game Over");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 32));

        title.setTranslateY(-60);

        this.message = new Label();
        this.message.setFont(Font.font("Consolas", 20));

        ClickButton close = new ClickButton(100, 32, "Close");
        close.setOnMouseClicked(event -> GameFinishPane.this.setVisible(false));
        close.setTranslateY(60);

        this.getChildren().addAll(title, this.message, close);
    }

    private void setMessage(String message) {
        this.message.setText(message);
        this.setVisible(true);
        this.setTranslateX(0);
        this.setTranslateY(0);
    }

    @HandleEvent
    public void onGameFinish(final GameFinishedEvent event) {
        Platform.runLater(() -> {
            boolean enabledAI = Options.ENABLED_AI.getBooleanValue();

            switch (event.getGame().getWinner()) {
                case NONE:
                    this.setMessage("Dead Hea!");
                    break;
                case BLUE:
                    this.setMessage(enabledAI ? "Player Wins!" : "Blue Team Wins!");
                    break;
                case RED:
                    this.setMessage(enabledAI ? "AI Wins!" : "Red Team Wins!");
                    break;
            }
        });
    }

    @HandleEvent
    public void onUndoChess(final UndoChessEvent event) {
        if (this.isVisible()) {
            this.setVisible(false);
        }
    }

    @HandleEvent
    public void onGameReset(final GameResetEvent event) {
        if (this.isVisible()) {
            this.setVisible(false);
        }
    }
}
