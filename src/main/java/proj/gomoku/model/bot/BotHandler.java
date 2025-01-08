package proj.gomoku.model.bot;

import net.quepierts.papyri.annotation.HandleEvent;
import net.quepierts.papyri.event.OptionUpdateEvent;
import net.quepierts.papyri.model.option.BooleanOption;
import proj.gomoku.app.Options;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.event.CloseApplicationEvent;
import proj.gomoku.model.event.GameResetEvent;
import proj.gomoku.model.event.PlacedChessEvent;
import proj.gomoku.model.event.UndoChessEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BotHandler {
    private final DroppingGomokuGame game;
    private final GomokuBot bot;

    private final ExecutorService executor;
    private boolean undoCall = false;

    public BotHandler(DroppingGomokuGame game) {
        this.game = game;
        this.bot = new GomokuBot(game);
        this.executor = Executors.newFixedThreadPool(1);
    }

    @HandleEvent
    public void onPlacedChess(final PlacedChessEvent event) {
        if (event.getGame() != this.game) {
            return;
        }

        if (!Options.ENABLED_AI.getBooleanValue()) {
            this.undoCall = false;
            this.executor.execute(this.bot::simulate);
            return;
        }

        if (this.game.isAIRound()) {
            this.executor.execute(this.bot::simulate);
            this.executor.execute(this::placeLater);
        }
    }

    @HandleEvent
    public void onUndoChess(final UndoChessEvent event) {
        if (event.getGame() != this.game) {
            return;
        }

        this.undoCall = this.game.isAIRound();
        this.bot.undo();
    }

    @HandleEvent
    public void onGameReset(final GameResetEvent event) {
        this.bot.reset();

        if (this.game.isAIRound()) {
            this.executor.execute(this::placeLater);
        }
    }

    @HandleEvent
    public void onCloseApplication(final CloseApplicationEvent event) {
        this.executor.shutdown();
    }

    @HandleEvent
    public void onUpdateOption(final OptionUpdateEvent<BooleanOption> event) {
        BooleanOption option = event.getOption();

        if (option == Options.ENABLED_AI) {
            if (!option.getBooleanValue()) {
                return;
            }

            if (!this.game.isAIRound()) {
                return;
            }

            this.undoCall = false;
            this.executor.execute(this::placeLater);
        } else if (option == Options.ENABLED_DEBUG) {
            if (!option.getBooleanValue()) {
                return;
            }

            this.bot.fillDebugScreen();
        }
    }

    private void placeLater() {
        try {
            Thread.sleep(1000);
            synchronized (this) {
                if (this.undoCall) {
                    this.undoCall = false;
                    return;
                }
                this.bot.step();
            }

        } catch (InterruptedException ignored) {}
    }
}
