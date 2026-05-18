package move;

import model.BoardState;
import model.Position;
import piece.Pawn;
import piece.Piece;

public abstract class BaseMove implements MoveCommand {
    protected final Position from, to;
    protected Piece movingPiece, capturedPiece;
    private int previousCastleFlags;
    private Pawn previousLastDoubleMovePawn;

    protected BaseMove(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    protected void cacheState(BoardState state) {
        movingPiece = state.getPiece(from);
        capturedPiece = state.getPiece(to);
        previousCastleFlags = state.getCastleFlags();
        previousLastDoubleMovePawn = state.getLastDoubleMovePawn();
    }

    protected void restoreState(BoardState state) {
        if (movingPiece != null) state.putPiece(from, movingPiece);
        if (capturedPiece != null) state.putPiece(to, capturedPiece);
        else state.removePiece(to);
        state.setCastleFlags(previousCastleFlags);
        state.setLastDoubleMovePawn(previousLastDoubleMovePawn);
    }

    protected void swapTurn(BoardState state) {
        state.setCurrentTurn(state.getCurrentTurn().opposite());
    }

    protected void clearEnPassant(BoardState state) {
        state.setLastDoubleMovePawn(null);
    }

    @Override
    public final boolean execute(BoardState state) {
        if (!isLegal(state)) return false;
        cacheState(state);
        ExecuteCommand(state);
        swapTurn(state);
        return true;
    }

    @Override
    public final void undo(BoardState state) {
        UndoCommand(state);
        restoreState(state);
        swapTurn(state);
    }

    @Override
    public boolean isLegal(BoardState state) {
        Piece piece = state.getPiece(from);
        if (piece == null || piece.getColor() != state.getCurrentTurn()) return false;
        if (!piece.getPossibleMoves(state).contains(to)) return false;
        if (!isLegalCommand(state)) return false;
        return !CheckUtil.wouldMoveCauseSelfCheck(state, from, to);
    }

    @Override
    public abstract boolean isCapture();

    @Override
    public Position getTo() { return to; }
    @Override
    public Position getFrom() { return from; }
    protected boolean isLegalCommand(BoardState state) {
        return true;
    }
    protected abstract void ExecuteCommand(BoardState state);
    protected abstract void UndoCommand(BoardState state);

}