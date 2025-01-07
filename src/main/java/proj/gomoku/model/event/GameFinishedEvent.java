package proj.gomoku.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.quepierts.papyri.event.Event;
import proj.gomoku.model.DroppingGomokuGame;

@Getter
@AllArgsConstructor
public class GameFinishedEvent extends Event {
    private final DroppingGomokuGame game;
}
