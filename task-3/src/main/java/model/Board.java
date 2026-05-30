package model;

import move.MoveCommand;
import move.MoveCommandFactory;
import move.CheckUtil;
import piece.Piece;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private final BoardState state;
    private final List<BoardState> history = new ArrayList<>();

    public Board() {
        this.state = new BoardState();
        FEN fenLoader = new FEN(new piece.PieceFactory());
        fenLoader.loadFromFEN(state, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        pushState();
    }

    public Board(Board other) {
        this.state = new BoardState(other.state);
    }


    public void pushState() {
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

    public boolean makeMove(Position from, Position to, String promotionType) {
        MoveCommand move = MoveCommandFactory.createMove(state, from, to, promotionType);
        if (move == null) return false;
        if (move.execute(state)) {
            pushState();
            return true;
        }
        return false;
    }

    public List<MoveCommand> generateLegalMoves() {
        List<MoveCommand> moves = new ArrayList<>();
        Side side = state.getCurrentTurn();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = state.getPiece(x, y);
                if (piece != null && piece.getColor() == side) {
                    moves.addAll(piece.getLegalMoves(state, new Position(x, y)));
                }
            }
        }
        return moves;
    }
    public boolean isCheck() {
        return CheckUtil.isCheck(state, state.getCurrentTurn());
    }

    public boolean isCheckmate() {
        if (!isCheck()) return false;
        return generateLegalMoves().isEmpty();
    }

    public boolean isStalemate() {
        if (isCheck()) return false;
        return generateLegalMoves().isEmpty();
    }

    public Board copy() {
        return new Board(this);
    }

    public model.BoardState getState() {
        return state;
    }
}