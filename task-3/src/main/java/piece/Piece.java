package piece;

import model.*;
import move.MoveCommand;

import java.util.List;

public abstract class Piece {
    protected int x;
    protected int y;
    protected Side side;
    protected final PieceType type;

    public Piece(int x, int y, Side side, PieceType type) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.type = type;
    }

    public abstract List<model.Position> getPossibleMoves(BoardState state);
    public abstract Piece copy();
    public abstract List<MoveCommand> getLegalMoves(BoardState state, Position from);

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public Side getColor() { return side; }
    public PieceType getType() { return type; }
}