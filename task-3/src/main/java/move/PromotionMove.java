package move;

import model.BoardState;
import model.Position;
import model.Side;
import piece.*;

public class PromotionMove extends BaseMove {
    private final String promotionPieceType;

    public PromotionMove(Position from, Position to, String promotionPieceType) {
        super(from, to);
        this.promotionPieceType = promotionPieceType;
    }

    @Override
    protected void ExecuteCommand(BoardState state) {
        state.movePiece(from, to);
        Piece promotedPiece = createPromotionPiece(to.x(), to.y(), movingPiece.getColor());
        state.putPiece(to, promotedPiece);
        clearEnPassant(state);
    }

    @Override
    protected void UndoCommand(BoardState state) {
        state.removePiece(to);
        if (capturedPiece != null) state.putPiece(to, capturedPiece);
        state.putPiece(from, movingPiece);
    }
    @Override
    public boolean isCapture() {
        return capturedPiece != null;
    }

    @Override
    protected boolean isLegalCommand(BoardState state) {
        Piece piece = state.getPiece(from);
        if (!(piece instanceof Pawn)) return false;
        int promotionRank = (piece.getColor() == Side.WHITE) ? 7 : 0;
        return to.y() == promotionRank;
    }

    private Piece createPromotionPiece(int x, int y, Side side) {
        return switch (promotionPieceType.toLowerCase()) {
            case "queen" -> new Queen(x, y, side);
            case "rook" -> new Rook(x, y, side);
            case "bishop" -> new Bishop(x, y, side);
            case "knight" -> new Knight(x, y, side);
            default -> null;
        };
    }
}