package proj.gomoku.model.bot;

import proj.gomoku.model.ChessState;
import proj.gomoku.model.GomokuHelper;

public class ChessboardCache {
    private final CacheEntry[][] entries = new CacheEntry[GomokuHelper.CHESSBOARD_SIZE][GomokuHelper.CHESSBOARD_SIZE];

    public ChessboardCache() {
        for (int x = 0; x < GomokuHelper.CHESSBOARD_SIZE; x++) {
            for (int y = 0; y < GomokuHelper.CHESSBOARD_SIZE; y++) {
                this.entries[x][y] = new CacheEntry();

                for (Direction direction : Direction.values()) {
                    int nx = x - direction.getX();
                    int ny = y - direction.getY();
                    if (this.isValidPosition(nx, ny) && this.entries[nx][ny] != null) {
                        this.entries[x][y].shared()[direction.ordinal()] = this.entries[nx][ny].shared()[direction.ordinal()];
                    }
                }
            }
        }
    }

    public void update(int column, int row, ChessState[][] chessboard, ChessState type) {
        CacheEntry entry = this.entries[column][row];
        DirectionCache[] shared = entry.shared();
        System.out.println(column + "," + row + ":");
        for (Direction direction : Direction.values()) {
            DirectionCache cache = shared[direction.ordinal()];
            cache.setRedLength(
                    GomokuHelper.countDirection(column, row, direction.getX(), direction.getY(), chessboard, type)
                    + GomokuHelper.countDirection(column, row, -direction.getX(), -direction.getY(), chessboard, type)
                    + 1
            );
            cache.setRedAvailable(cache.getRedLength() < GomokuHelper.REQUIRED_LENGTH);
            System.out.println(direction + ":" + cache);
        }
    }

    public void undo(int column, int row, ChessState[][] chessboard, ChessState type) {
        CacheEntry entry = this.entries[column][row];
        DirectionCache[] shared = entry.shared();
        System.out.println(column + "," + row + ":");
        for (Direction direction : Direction.values()) {
            DirectionCache cache = shared[direction.ordinal()];
            cache.setRedLength(
                    GomokuHelper.countDirection(column, row, direction.getX(), direction.getY(), chessboard, type)
                            + GomokuHelper.countDirection(column, row, -direction.getX(), -direction.getY(), chessboard, type)
            );
            cache.setRedAvailable(cache.getRedLength() < GomokuHelper.REQUIRED_LENGTH);
            System.out.println(direction + ":" + cache);
        }
    }

    private boolean isValidPosition(int column, int row) {
        return row >= 0 && column >= 0 && row < GomokuHelper.CHESSBOARD_SIZE && column < GomokuHelper.CHESSBOARD_SIZE;
    }
}
