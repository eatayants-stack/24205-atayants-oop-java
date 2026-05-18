package piece;

import model.BoardState;
import model.Position;
import model.Side;
import move.MoveCommand;
import move.MoveCommandFactory;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(int x, int y, Side side) {
        super(x, y, side, PieceType.PAWN);
    }

    @Override
    public List<Position> getPossibleMoves(BoardState state) {
        List<Position> moves = new ArrayList<>();
        int dir = (side == Side.WHITE) ? 1 : -1;
        Position from = new Position(x, y);

        Position oneStep = from.add(0, dir);
        if (oneStep.isInside() && state.getPiece(oneStep) == null) {
            moves.add(oneStep);
            Position twoStep = from.add(0, 2 * dir);
            if ((side == Side.WHITE && y == 1) || (side == Side.BLACK && y == 6)) {
                if (twoStep.isInside() && state.getPiece(twoStep) == null
                        && state.getPiece(oneStep) == null) {
                    moves.add(twoStep);
                }
            }
        }

        for (int dx : new int[]{-1, 1}) {
            Position diag = from.add(dx, dir);
            if (diag.isInside()) {
                Piece target = state.getPiece(diag);
                if (target != null && target.getColor() != side) {
                    moves.add(diag);
                }
            }
        }


        Pawn last = state.getLastDoubleMovePawn();
        if (last != null && last.getColor() != side) {
            int epRank = (side == Side.WHITE) ? 4 : 3;
            if (y == epRank) {
                for (int dx : new int[]{-1, 1}) {
                    Position ep = new Position(x + dx, y + dir);
                    if (ep.isInside() && last.getX() == x + dx && last.getY() == y) {
                        moves.add(ep);
                    }
                }
            }
        }
        return moves;
    }
    @Override
    public List<MoveCommand> getLegalMoves(BoardState state, Position from) {
        List<MoveCommand> moves = new ArrayList<>();
        int promoRank = (side == Side.WHITE) ? 7 : 0;
        for (Position to : getPossibleMoves(state)) {
            if (to.y() == promoRank) {
                for (String promo : new String[]{"queen", "rook", "bishop", "knight"}) {
                    MoveCommand move = MoveCommandFactory.createMove(state, from, to, promo);
                    if (move != null && move.isLegal(state)) moves.add(move);
                }
            } else {
                MoveCommand move = MoveCommandFactory.createMove(state, from, to, null);
                if (move != null && move.isLegal(state)) moves.add(move);
            }
        }
        return moves;
    }

    @Override
    public Piece copy() {
        return new Pawn(this.x, this.y, this.side);
    }
}