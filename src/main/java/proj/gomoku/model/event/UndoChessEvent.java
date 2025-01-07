package proj.gomoku.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.quepierts.papyri.event.Event;
import proj.gomoku.model.ChessState;
import proj.gomoku.model.DroppingGomokuGame;

@Getter
@AllArgsConstructor
public class UndoChessEvent extends Event {
    private final DroppingGomokuGame game;
    private final int column;
    private final int row;
    private final ChessState team;
}
