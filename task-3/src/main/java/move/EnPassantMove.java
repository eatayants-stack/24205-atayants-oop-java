package move;

import model.BoardState;
import model.Position;
import model.Side;
import piece.Pawn;
import piece.Piece;
import piece.PieceType;

public class EnPassantMove extends BaseMove {
    private final Position capturedPawnPos;

    public EnPassantMove(Position from, Position to, Position capturedPawnPos) {
        super(from, to);
        this.capturedPawnPos = capturedPawnPos;
    }

    @Override
    protected void ExecuteCommand(BoardState state) {
        state.movePiece(from, to);
        capturedPiece = state.getPiece(capturedPawnPos);
        if (capturedPiece != null) {
            state.removePiece(capturedPawnPos);
        }
        clearEnPassant(state);
    }

    @Override
    protected void UndoCommand(BoardState state) {
        state.putPiece(from, movingPiece);
        state.removePiece(to);
        if (capturedPiece != null)
            state.putPiece(capturedPawnPos, capturedPiece);
    }

    @Override
    public boolean isCapture() {
        return true;
    }
    @Override
    protected boolean isLegalCommand(BoardState state) {
        Piece piece = state.getPiece(from);
        if (!(piece instanceof Pawn pawn)) return false;
        if (pawn.getColor() != state.getCurrentTurn()) return false;
        Pawn last = state.getLastDoubleMovePawn();
        if (last == null || last.getColor() == pawn.getColor()) return false;
        int expectedY = (pawn.getColor() == Side.WHITE) ? 4 : 3;
        if (from.y() != expectedY) return false;

        if (!(last.getX() == to.x() && last.getY() == from.y())) return false;

        Piece pawnOnBoard = state.getPiece(last.getX(), last.getY());
        return pawnOnBoard != null
                && pawnOnBoard.getColor() != pawn.getColor()
                && pawnOnBoard.getType() == PieceType.PAWN;
    }
}