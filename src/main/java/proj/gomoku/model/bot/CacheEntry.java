package proj.gomoku.model.bot;

public final class CacheEntry {
    private final DirectionCache[] shared;

    public CacheEntry() {
        this.shared = new DirectionCache[Direction.values().length];
        for (int i = 0; i < this.shared.length; i++) {
            this.shared[i] = new DirectionCache();
        }
    }

    public DirectionCache[] shared() {
        return shared;
    }
}
