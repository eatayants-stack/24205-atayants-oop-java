package piece;

import model.*;
import move.MoveCommand;
import move.MoveCommandFactory;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(int x, int y, Side side) {
        super(x, y, side, PieceType.KNIGHT);
    }

    @Override
    public List<Position> getPossibleMoves(BoardState state) {
        List<Position> moves = new ArrayList<>();
        int[][] offs = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] off : offs) {
            Position to = new Position(x + off[0], y + off[1]);
            if (to.isInside()) {
                Piece p = state.getPiece(to);
                if (p == null || p.getColor() != side) moves.add(to);
            }
        }
        return moves;
    }
    @Override
    public List<MoveCommand> getLegalMoves(BoardState state, Position from) {
        List<MoveCommand> moves = new ArrayList<>();
        for (Position to : getPossibleMoves(state)) {
            MoveCommand move = MoveCommandFactory.createMove(state, from, to, null);
            if (move != null && move.isLegal(state)) moves.add(move);
        }
        return moves;
    }

    @Override
    public Piece copy() {
        return new Knight(this.x, this.y, this.side);
    }
}