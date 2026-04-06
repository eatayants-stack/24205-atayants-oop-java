public class FEN {
    public static void loadFromFEN(BoardState state, String fen) {
        String[] parts = fen.split(" ");
        String boardPart = parts[0];
        String turnPart = parts[1];
        String castlePart = parts[2];

        resetCastleFlags(state);

        int x = 0, y = 7;
        for (char c : boardPart.toCharArray()) {
            if (c == '/') {
                x = 0;
                y--;
            } else if (Character.isDigit(c)) {
                x += (c - '0');
            } else {
                Side side = Character.isUpperCase(c) ? Side.BLACK : Side.WHITE;
                char lower = Character.toLowerCase(c);
                if (lower == 'k') {
                    King king = new King(x, y, side);
                    state.setPiece(x, y, king);
                    if (side == Side.WHITE) state.setWhiteKing(king);
                    else state.setBlackKing(king);
                } else {
                    state.setPiece(x, y, createPiece(lower, x, y, side));
                }
                x++;
            }
        }

        state.setCurrentTurn(turnPart.equals("w") ? Side.WHITE : Side.BLACK);
        parseCastleFlags(state, castlePart);
    }

    private static void resetCastleFlags(BoardState state) {
        state.setCastleFlags(false, false, false, false, false, false);
    }

    private static void parseCastleFlags(BoardState state, String castlePart) {
        boolean whiteRookKMoved = !castlePart.contains("K");
        boolean whiteRookQMoved = !castlePart.contains("Q");
        boolean blackRookKMoved = !castlePart.contains("k");
        boolean blackRookQMoved = !castlePart.contains("q");
        state.setCastleFlags(false, false, whiteRookKMoved, whiteRookQMoved, blackRookKMoved, blackRookQMoved);
    }

    private static Piece createPiece(char type, int x, int y, Side side) {
        switch (type) {
            case 'p': return new Pawn(x, y, side);
            case 'n': return new Knight(x, y, side);
            case 'b': return new Bishop(x, y, side);
            case 'r': return new Rook(x, y, side);
            case 'q': return new Queen(x, y, side);
            default: throw new IllegalArgumentException("Unknown piece type: " + type);
        }
    }
}
