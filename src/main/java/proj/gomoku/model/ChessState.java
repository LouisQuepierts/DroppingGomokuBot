package proj.gomoku.model;

/*
* Type for chess type and team
* */
public enum ChessState {
    NONE,
    RED,
    BLUE;

    public static ChessState getOpposite(ChessState state) {
        return switch (state) {
            case NONE -> NONE;
            case BLUE -> RED;
            case RED -> BLUE;
        };
    }
}
