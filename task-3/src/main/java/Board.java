import java.util.ArrayList;
import java.util.List;

public class Board {
    private final BoardState state;
    private final List<BoardState> history = new ArrayList<>();

    public Board() {
        state = new BoardState();
        FEN.loadFromFEN(state, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        pushState();
    }

    public Board(Board other) {
        this.state = new BoardState(other.state);
    }

    private void pushState() {
        history.add(new BoardState(state));
    }

    public int countOccurrences(BoardState current) {
        int count = 0;
        for (BoardState prev : history) {
            if (prev.equalsPosition(current)) count++;
        }
        return count;
    }

    public boolean isThreefoldRepetition() {
        return countOccurrences(state) >= 3;
    }


    public boolean makeMove(int fromX, int fromY, int toX, int toY, String promotionType) {
        Piece piece = state.getPiece(fromX, fromY);
        if (piece == null || piece.getColor() != state.getCurrentTurn()) return false;


        if (MoveEngine.tryHandleCastling(state, piece, fromX, fromY, toX, toY)) {
            pushState();
            return true;
        }

        if (!piece.isValidMove(state.cells, state, fromX, fromY, toX, toY)) return false;

        boolean enPassant = MoveEngine.tryEnPassant(state, piece, fromX, fromY, toX, toY);

        if (MoveEngine.wouldMoveCauseSelfCheck(state, fromX, fromY, toX, toY)) {
            if (enPassant) {
                Pawn last = state.getLastDoubleMovePawn();
                state.setPiece(toX, fromY, last);
            }
            return false;
        }


        MoveEngine.applyPieceMove(state, fromX, fromY, toX, toY);
        MoveEngine.finalizeMove(state, piece, fromX, fromY, toX, toY, promotionType);

        pushState();

        return true;
    }

    public boolean makeMove(int fromX, int fromY, int toX, int toY) {
        return makeMove(fromX, fromY, toX, toY, "queen");
    }


    public boolean isCheck() {
        return MoveEngine.isCheck(state, state.getCurrentTurn());
    }

    public boolean isCheckmate() {
        if (!isCheck()) return false;
        List<Move> moves = MoveEngine.generateMoves(state);
        for (Move move : moves) {
            Board copy = this.copy();
            if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                return false;
            }
        }
        return true;
    }


    public Board copy() {
        return new Board(this);
    }

    public BoardState getState() {
        return this.state;
    }
}