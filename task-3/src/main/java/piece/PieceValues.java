package piece;


public final class PieceValues {
    private PieceValues() {}
    public static int getValue(Piece piece) {
        return switch (piece.getType()) {
            case PAWN   -> 100;
            case KNIGHT -> 320;
            case BISHOP -> 330;
            case ROOK   -> 500;
            case QUEEN  -> 900;
            case KING   -> 20000;
        };
    }
}