package piece;

import model.BoardState;
import model.Position;
import model.Side;
import move.MoveCommand;
import move.MoveCommandFactory;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(int x, int y, Side side) {
        super(x, y, side, PieceType.KING);
    }

    @Override
    public List<Position> getPossibleMoves(BoardState state) {
        List<Position> moves = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Position to = new Position(x + dx, y + dy);
                if (to.isInside()) {
                    Piece p = state.getPiece(to);
                    if (p == null || p.getColor() != side) {
                        moves.add(to);
                    }
                }
            }
        }
        return moves;
    }

    @Override
    public List<MoveCommand> getLegalMoves(BoardState state, Position from) {
        List<MoveCommand> moves = new ArrayList<>();

        // Обычные ходы
        for (Position to : getPossibleMoves(state)) {
            MoveCommand move = MoveCommandFactory.createMove(state, from, to, "queen");
            if (move != null && move.isLegal(state)) moves.add(move);
        }

        // Короткая рокировка (вправо)
        if (state.canCastleKingside(side)) {
            Position to = new Position(6, from.y());
            MoveCommand castle = MoveCommandFactory.createMove(state, from, to, "queen");

            if (castle != null && castle.isLegal(state)) {
                moves.add(castle);
            }
        }

        return moves;
    }

    @Override
    public piece.Piece copy() {
        return new King(this.x, this.y, this.side);
    }
}