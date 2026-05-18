package piece;

import model.BoardState;
import model.Position;
import model.Side;
import move.MoveCommand;
import move.MoveCommandFactory;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(int x, int y, Side side) {
        super(x, y, side, PieceType.BISHOP);
    }

    @Override
    public List<Position> getPossibleMoves(BoardState state) {
        List<Position> moves = new ArrayList<>();
        int[][] dirs = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
        for (int[] d : dirs) {
            Position pos = new Position(x, y);
            while (true) {
                pos = pos.add(d[0], d[1]);
                if (!pos.isInside()) break;
                Piece p = state.getPiece(pos);
                if (p == null) {
                    moves.add(pos);
                } else {
                    if (p.getColor() != side) moves.add(pos);
                    break;
                }
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
        return new Bishop(this.x, this.y, this.side);
    }
}