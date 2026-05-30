package move;

import model.BoardState;
import model.Position;

public interface MoveCommand {
    boolean execute(BoardState state);
    void undo(BoardState state);
    boolean isLegal(BoardState state);
    boolean isCapture();
    Position getTo();
    Position getFrom();
}