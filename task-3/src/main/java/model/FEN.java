package model;

import piece.Piece;
import piece.PieceFactory;

public class FEN {
    private final PieceFactory pieceFactory;

    public FEN(PieceFactory pieceFactory) {
        this.pieceFactory = pieceFactory;
    }

    public void loadFromFEN(BoardState state, String fen) {
        String[] parts = fen.split(" ");
        String boardPart = parts[0];
        String turnPart = parts[1];
        String castlePart = parts[2];

        state.setCastleFlags(0);

        int x = 0, y = 7;
        for (char c : boardPart.toCharArray()) {
            if (c == '/') {
                x = 0;
                y--;
            } else if (Character.isDigit(c)) {
                x += (c - '0');
            } else {
                Piece piece = pieceFactory.createPiece(c, x, y);
                state.putPiece(new Position(x, y), piece);
                x++;
            }
        }

        state.setCurrentTurn(turnPart.equals("w") ? Side.WHITE : Side.BLACK);
        parseCastleFlags(state, castlePart);
        state.updateKings();
    }

    private void parseCastleFlags(BoardState state, String castlePart) {
        int flags = 0;
        if (!castlePart.contains("K")) flags |= BoardState.WHITE_ROOK_K_MOVED;
        if (!castlePart.contains("Q")) flags |= BoardState.WHITE_ROOK_Q_MOVED;
        if (!castlePart.contains("k")) flags |= BoardState.BLACK_ROOK_K_MOVED;
        if (!castlePart.contains("q")) flags |= BoardState.BLACK_ROOK_Q_MOVED;
        state.setCastleFlags(flags);
    }
}