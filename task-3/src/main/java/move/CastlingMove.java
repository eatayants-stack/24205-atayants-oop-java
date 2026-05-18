package move;

import model.BoardState;
import model.Position;
import piece.King;
import piece.Piece;
import piece.PieceType;

public class CastlingMove extends BaseMove {
    private final Position rookFrom;
    private final Position rookTo;
    private Piece rook;

    public CastlingMove(Position from, Position to) {
        super(from, to);
        boolean kingside = to.x() > from.x();
        if (kingside) {
            rookFrom = new Position(7, from.y());
            rookTo = new Position(to.x() - 1, from.y());
        } else {
            rookFrom = new Position(0, from.y());
            rookTo = new Position(to.x() + 1, from.y());
        }
    }


    @Override
    protected void ExecuteCommand(BoardState state) {
        rook = state.getPiece(rookFrom);
        state.movePiece(from, to);
        state.movePiece(rookFrom, rookTo);
        state.setKingMoved(movingPiece.getColor());
        state.setRookMoved(movingPiece.getColor(), rookFrom.x());
        clearEnPassant(state);
    }

    @Override
    protected void UndoCommand(BoardState state) {
        state.putPiece(from, movingPiece);
        state.removePiece(to);
        state.putPiece(rookFrom, rook);
        state.removePiece(rookTo);
    }

    @Override
    public boolean isCapture() {
        return false;
    }

    @Override
    protected boolean isLegalCommand(BoardState state) {
        Piece king = state.getPiece(from);
        if (!(king instanceof King) || king.getColor() != state.getCurrentTurn())
            return false;

        boolean kingside = to.x() > from.x();

        Piece rookPiece = state.getPiece(rookFrom);
        if (rookPiece == null || rookPiece.getType() != PieceType.ROOK || rookPiece.getColor() != king.getColor())
            return false;

        int step = kingside ? 1 : -1;
        for (int x = from.x() + step; x != rookFrom.x(); x += step) {
            if (state.getPiece(new Position(x, from.y())) != null)
                return false;
        }
    if (CheckUtil.isCheck(state, king.getColor()))
        return false;

    for (int x = from.x(); x != to.x() + step; x += step) {
        if (CheckUtil.isSquareAttacked(state, new Position(x, from.y()), king.getColor().opposite()))
            return false;
    }


        return true;
    }
    @Override
    public boolean isLegal(BoardState state) {
        return isLegalCommand(state);
    }
}