package proj.gomoku.model;

public class GomokuHelper {
    public static final int CHESSBOARD_SIZE = 5;
    public static final int REQUIRED_LENGTH = 4;

    public static final int[][] DIRECTIONS = {
            {1, 0},
            {0, 1},
            {1, 1},
            {1, -1}
    };

    public static int getPackedIndex(int x, int y) {
        return x * CHESSBOARD_SIZE + y;
    }

    public static int countDirection(int x, int y, int dx, int dy, ChessState[][] chessboard, ChessState type) {
        int count = 0;
        int nx = x + dx;
        int ny = y + dy;

        while (nx >= 0 && ny >= 0
                && nx < GomokuHelper.CHESSBOARD_SIZE && ny < GomokuHelper.CHESSBOARD_SIZE
                && chessboard[nx][ny] == type
        ) {
            count++;
            nx += dx;
            ny += dy;
        }

        return count;
    }

    public static int getColumn(int step) {
        return step / GomokuHelper.CHESSBOARD_SIZE;
    }
}
