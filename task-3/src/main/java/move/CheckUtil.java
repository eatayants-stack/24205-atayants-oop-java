package move;

import model.BoardState;
import model.Position;
import model.Side;
import piece.King;
import piece.Piece;

public final class CheckUtil {

    private CheckUtil() {}

    public static boolean wouldMoveCauseSelfCheck(BoardState state, Position from, Position to) {
        if (from.equals(to)) return false;
        Piece moving = state.getPiece(from);
        if (moving == null) return false;
        BoardState copy = new BoardState(state);
        Piece movingCopy = copy.getPiece(from);
        if (movingCopy == null) return false;
        Piece targetCopy = copy.getPiece(to);
        if (targetCopy != null) {
            copy.removePiece(to);
        }
        copy.movePiece(from, to);
        return isCheck(copy, movingCopy.getColor());
    }

    public static boolean isCheck(BoardState state, Side side) {
        King king = (side == Side.WHITE) ? state.getWhiteKing() : state.getBlackKing();
        if (king == null) return false;
        return isSquareAttacked(state, new Position(king.getX(), king.getY()), side.opposite());
    }

    public static boolean isSquareAttacked(BoardState state, Position pos, Side attackerSide) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = state.getPiece(i, j);
                if (p != null && p.getColor() == attackerSide) {
                    if (p.getType() == piece.PieceType.PAWN) {
                        int dir = (attackerSide == Side.WHITE) ? 1 : -1;
                        if (Math.abs(pos.x() - i) == 1 && (pos.y() - j) == dir) return true;
                    } else if (p.getPossibleMoves(state).contains(pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}