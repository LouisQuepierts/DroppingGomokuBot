package proj.gomoku.model;

import lombok.Getter;

@Getter
public enum Direction {
    HORIZONTAL(1, 0),
    VERTICAL(0, 1),
    MAIN_DIAGONAL(1, 1),
    ANTI_DIAGONAL(-1, 1);

    private final int x;
    private final int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
