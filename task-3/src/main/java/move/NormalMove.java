package move;

import model.BoardState;
import model.Position;
import piece.Pawn;

import piece.PieceType;

public  class NormalMove extends BaseMove {
    public NormalMove(Position from, Position to) {
        super(from, to);
    }

    @Override
    protected void ExecuteCommand(BoardState state) {
        state.movePiece(from, to);
        updateAuxiliaryData(state);
    }

    @Override
    protected void UndoCommand(BoardState state) {
        state.putPiece(from, movingPiece);
        if (capturedPiece != null) state.putPiece(to, capturedPiece);
        else state.removePiece(to);
    }


    @Override
    public boolean isCapture() {
        return capturedPiece != null;
    }

    protected void updateAuxiliaryData(BoardState state) {
        clearEnPassant(state);
        if (movingPiece.getType() == PieceType.KING)
            state.setKingMoved(movingPiece.getColor());
        if (movingPiece.getType() == PieceType.ROOK)
            state.setRookMoved(movingPiece.getColor(), from.x());
        if (movingPiece.getType() == PieceType.PAWN
                && Math.abs(to.y() - from.y()) == 2) {
            state.setLastDoubleMovePawn((Pawn) movingPiece);
        }
    }
}