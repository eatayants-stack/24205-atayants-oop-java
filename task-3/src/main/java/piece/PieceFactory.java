package piece;

import model.Side;

public class PieceFactory {
    public Piece createPiece(char symbol, int x, int y) {
        Side side = Character.isUpperCase(symbol) ? Side.WHITE : Side.BLACK;
        PieceType type = PieceType.fromChar(symbol);
        return switch (type) {
            case PAWN -> new Pawn(x, y, side);
            case KNIGHT -> new Knight(x, y, side);
            case BISHOP -> new Bishop(x, y, side);
            case ROOK -> new Rook(x, y, side);
            case QUEEN -> new Queen(x, y, side);
            case KING -> new King(x, y, side);
        };
    }
}