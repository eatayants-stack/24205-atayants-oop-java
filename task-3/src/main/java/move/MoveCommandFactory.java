package move;

import model.BoardState;
import model.Position;
import model.Side;
import piece.King;
import piece.Pawn;
import piece.Piece;
import piece.PieceType;

public class MoveCommandFactory {

    public static MoveCommand createMove(BoardState state, Position from, Position to, String promotionType) {
        Piece piece = state.getPiece(from);
        if (piece == null) return null;

        if (piece instanceof King && Math.abs(from.x() - to.x()) == 2) {
            return new CastlingMove(from, to);
        }

        if (piece.getType() == PieceType.PAWN && to.x() != from.x() && state.getPiece(to) == null) {
            Pawn last = state.getLastDoubleMovePawn();
            if (last != null && last.getColor() != piece.getColor() &&
                    last.getY() == from.y() && last.getX() == to.x()) {
                Position capturedPos = new Position(to.x(), from.y());
                return new EnPassantMove(from, to, capturedPos);
            }
        }

        int promotionRank = (piece.getColor() == Side.WHITE) ? 7 : 0;
        if (piece.getType() == PieceType.PAWN && to.y() == promotionRank) {
            String promo = (promotionType == null) ? "queen" : promotionType;
            return new PromotionMove(from, to, promo);
        }

        return new NormalMove(from, to);
    }
}