package proj.gomoku.model.bot;

import lombok.Getter;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;
import proj.gomoku.model.GomokuHelper;

public class GomokuBot {
    @Getter // debug
    private final ChessboardCache cache;
    private final DroppingGomokuGame game;
    private final DroppingGomokuGame simulate;

    private final ChessState[][] chessboard = new ChessState[GomokuHelper.CHESSBOARD_SIZE][GomokuHelper.CHESSBOARD_SIZE];

    public GomokuBot(DroppingGomokuGame game) {
        this.game = game;
        this.cache = new ChessboardCache();
        this.simulate = new DroppingGomokuGame();
    }

    // 进行下一步计算
    public void step() {
        int step = this.game.getLastStep();

        this.game.getChessboard(this.chessboard);
        ChessState current = this.game.getCurrentState();

        if (step != -1) {
            int column = GomokuHelper.getColumn(step);
            this.simulate.add(column);
        }

        for (int i = 0; i < GomokuHelper.CHESSBOARD_SIZE; i++) {
            if (!this.game.canAdd(i)) {
                continue;
            }

            int row = this.game.getHeight(i);
            this.simulate.add(i);

            this.simulate.undo();
        }
    }

    // 当玩家悔棋后，让机器人同步
    public void undo() {

    }
}
