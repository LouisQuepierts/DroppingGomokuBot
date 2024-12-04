package proj.gomoku.model.bot;

import lombok.Data;

@Data
public class DirectionCache {
    private int redLength = 0;
    private int blueLength = 0;
    private boolean redAvailable = true;
    private boolean blueAvailable = true;
}
