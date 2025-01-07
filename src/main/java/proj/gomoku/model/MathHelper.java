package proj.gomoku.model;

@SuppressWarnings("all")
public class MathHelper {
    public static double clamp(double value, double min, double max) {
        return value < min ? min : value > max ? max : value;
    }
}
