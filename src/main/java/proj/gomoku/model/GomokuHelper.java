package proj.gomoku.model;

public class GomokuHelper {
    public static final int CHESSBOARD_SIZE = 6;
    public static final int CHESSBOARD_HEIGHT = CHESSBOARD_SIZE;
    public static final int CHESSBOARD_WIDTH = 7;
    public static final int REQUIRED_LENGTH = 4;

    public static final ChessState PLAYER_STATE = ChessState.BLUE;
    public static final ChessState AI_STATE = ChessState.RED;

    public static int countDirection(int x, int y, int dx, int dy, ChessState[][] chessboard, ChessState type) {
        int count = 0;
        int nx = x + dx;
        int ny = y + dy;

        while (nx >= 0 && ny >= 0
                && nx < GomokuHelper.CHESSBOARD_WIDTH && ny < GomokuHelper.CHESSBOARD_HEIGHT
                && chessboard[nx][ny] == type
        ) {
            count++;
            nx += dx;
            ny += dy;
        }

        return count;
    }
}
