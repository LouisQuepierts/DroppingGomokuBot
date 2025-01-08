package proj.gomoku.model.bot;

import lombok.Getter;
import net.quepierts.papyri.event.OptionUpdateEvent;
import net.quepierts.papyri.event.PapyriEventBus;
import proj.gomoku.app.Options;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.GomokuHelper;
import proj.gomoku.model.ImmutableIntegerPair;
import proj.gomoku.model.debug.DebugHandler;

public class GomokuBot {
    @Getter // debug
    private final DroppingGomokuGame game;
    private final DroppingGomokuGame simulate;

    private final GomokuGameEvaluator evaluator;
    private final BacktraceEvaluator backtrace;

    private int steps = 0;

    public GomokuBot(DroppingGomokuGame game) {
        this.game = game;
        this.simulate = new DroppingGomokuGame(true);
        this.evaluator = new GomokuGameEvaluator(this.simulate);

        this.backtrace = new BacktraceEvaluator(this.simulate, GomokuHelper.AI_STATE, this.evaluator);

        PapyriEventBus.subscribe(OptionUpdateEvent.class, this.simulate::onOptionUpdate);
    }

    public void reset() {
        this.simulate.reset();
        this.steps = 0;
        DebugHandler.reset();
    }

    public void fillDebugScreen() {
        DebugHandler.refill(this.simulate);
    }

    public void simulate() {
        ImmutableIntegerPair step = this.game.getLastStep();

        if (step != null) {
            int column = step.x();
            this.simulate.add(column);
            this.steps++;

            if (Options.ENABLED_DEBUG.getBooleanValue()) {
                DebugHandler.set(step.x(), step.y(), this.game.getLastState(), false);
            }
        }
    }

    // 进行下一步计算
    public void step() {
        if (this.game.isGameFinished()) {
            return;
        }

        if (Options.ENABLED_AI.getBooleanValue() && this.game.isAIRound()) {
            this.steps ++;

            int selected = this.greedy();

            this.simulate.add(selected);
            this.game.add(selected);

            if (Options.ENABLED_DEBUG.getBooleanValue()) {
                ImmutableIntegerPair step = this.simulate.getLastStep();
                DebugHandler.set(step.x(), step.y(), this.game.getLastState(), false);
            }
        }
    }

    // 当玩家悔棋后，让机器人同步
    public void undo() {
        ImmutableIntegerPair step = this.simulate.getLastStep();

        if (this.simulate.undo()) {
            DebugHandler.set(step.x(), step.y(), ChessState.NONE, false);
            this.steps --;
        }
    }

    private int greedy() {
        if (this.steps == 1) {
            int left = (GomokuHelper.CHESSBOARD_WIDTH - GomokuHelper.REQUIRED_LENGTH) / 2;
            return left + (int) (Math.random() * (GomokuHelper.REQUIRED_LENGTH));
        }

        int maxScore = Integer.MIN_VALUE;
        int column = 0;

        int mid = GomokuHelper.CHESSBOARD_WIDTH / 2;
        for (int i = 0; i < GomokuHelper.CHESSBOARD_WIDTH; i++) {
            int col = mid + ((i % 2 == 0) ? (i / -2) : (i / 2 + 1));
            if (!this.simulate.add(col)) {
                continue;
            }

            int row = this.game.getHeight(col);
            int evaluated = this.evaluator.evaluate(col, row, GomokuHelper.AI_STATE);

            this.simulate.undo();
            if (evaluated > maxScore) {
                maxScore = evaluated;
                column = col;
            }
        }

        if (maxScore == Integer.MIN_VALUE) {
            while (!this.game.canAdd(column)) {
                column ++;
            }
        }
        return column;
    }
}
