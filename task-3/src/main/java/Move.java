public class Move {
    public final int fromX, fromY, toX, toY;
    public final Piece capturedPiece;
    public final boolean isCastle;
    public final boolean isEnPassant;
    public final int enPassantPawnX;
    public final String promotionPiece;

    public Move(int fromX, int fromY, int toX, int toY, Piece capturedPiece,
                boolean isCastle, boolean isEnPassant, int enPassantPawnX,
                String promotionPiece) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.capturedPiece = capturedPiece;
        this.isCastle = isCastle;
        this.isEnPassant = isEnPassant;
        this.enPassantPawnX = enPassantPawnX;
        this.promotionPiece = promotionPiece;
    }
}