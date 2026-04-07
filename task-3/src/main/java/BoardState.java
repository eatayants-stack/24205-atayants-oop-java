public class BoardState {
    public Piece[][] cells;
    private Side currentTurn;
    public King whiteKing;
    public King blackKing;
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private boolean whiteRookKMoved;
    private boolean whiteRookQMoved;
    private boolean blackRookKMoved;
    private boolean blackRookQMoved;
    private Pawn lastDoubleMovePawn;


    public BoardState() {
        cells = new Piece[8][8];
        currentTurn = Side.WHITE;
        whiteKing = null;
        blackKing = null;
        resetCastleFlags();
    }
    public BoardState(BoardState other) {
        this.cells = new Piece[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = other.cells[x][y];
                if (p != null) {
                    this.cells[x][y] = p.copy();
                }
            }
        }
        this.currentTurn = other.currentTurn;
        this.whiteKingMoved = other.whiteKingMoved;
        this.blackKingMoved = other.blackKingMoved;
        this.whiteRookKMoved = other.whiteRookKMoved;
        this.whiteRookQMoved = other.whiteRookQMoved;
        this.blackRookKMoved = other.blackRookKMoved;
        this.blackRookQMoved = other.blackRookQMoved;


        this.whiteKing = findKing(Side.WHITE);
        this.blackKing = findKing(Side.BLACK);


        if (other.lastDoubleMovePawn != null) {
            this.lastDoubleMovePawn = (Pawn) other.lastDoubleMovePawn.copy();
        } else {
            this.lastDoubleMovePawn = null;
        }
    }

    public boolean equalsPosition(BoardState other) {
        if (this.currentTurn != other.currentTurn) return false;
        if (this.whiteKingMoved != other.whiteKingMoved) return false;
        if (this.blackKingMoved != other.blackKingMoved) return false;
        if (this.whiteRookKMoved != other.whiteRookKMoved) return false;
        if (this.whiteRookQMoved != other.whiteRookQMoved) return false;
        if (this.blackRookKMoved != other.blackRookKMoved) return false;
        if (this.blackRookQMoved != other.blackRookQMoved) return false;


        Pawn thisPawn = this.getLastDoubleMovePawn();
        Pawn otherPawn = other.getLastDoubleMovePawn();
        if (thisPawn == null && otherPawn != null) return false;
        if (thisPawn != null && otherPawn == null) return false;
        if (thisPawn != null) {
            if (thisPawn.getX() != otherPawn.getX() || thisPawn.getY() != otherPawn.getY())
                return false;
        }


        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p1 = this.getPiece(x, y);
                Piece p2 = other.getPiece(x, y);
                if (p1 == null && p2 == null) continue;
                if (p1 == null || p2 == null) return false;
                if (p1.getClass() != p2.getClass() || p1.getColor() != p2.getColor())
                    return false;
            }
        }
        return true;
    }

    private King findKing( Side side) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = cells[x][y];
                if (piece instanceof King && piece.getColor() == side) {
                    return (King) piece;
                }
            }
        }
        throw new IllegalStateException("King " + side + " not found on board!");
    }

    private void resetCastleFlags() {
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteRookKMoved = false;
        whiteRookQMoved = false;
        blackRookKMoved = false;
        blackRookQMoved = false;
    }


    public Side getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Side side) {
        currentTurn = side;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return cells[x][y];
    }

    public void setPiece(int x, int y, Piece piece) {
        cells[x][y] = piece;
    }


    public void setWhiteKing(King king) {
        whiteKing = king;
    }


    public void setBlackKing(King king) {
        blackKing = king;
    }

    public void setLastDoubleMovePawn(Pawn piece) {
        this.lastDoubleMovePawn = piece;
    }

    public Pawn getLastDoubleMovePawn() {
        return lastDoubleMovePawn;
    }

    public boolean isWhiteKingMoved() { return whiteKingMoved; }
    public boolean isBlackKingMoved() { return blackKingMoved; }
    public boolean isWhiteRookKMoved() { return whiteRookKMoved; }
    public boolean isWhiteRookQMoved() { return whiteRookQMoved; }
    public boolean isBlackRookKMoved() { return blackRookKMoved; }
    public boolean isBlackRookQMoved() { return blackRookQMoved; }

    public void setWhiteKingMoved(boolean moved) { whiteKingMoved = moved; }
    public void setBlackKingMoved(boolean moved) { blackKingMoved = moved; }
    public void setWhiteRookKMoved(boolean moved) { whiteRookKMoved = moved; }
    public void setWhiteRookQMoved(boolean moved) { whiteRookQMoved = moved; }
    public void setBlackRookKMoved(boolean moved) { blackRookKMoved = moved; }
    public void setBlackRookQMoved(boolean moved) { blackRookQMoved = moved; }

    public void setCastleFlags(boolean whiteKingMoved, boolean blackKingMoved,
                               boolean whiteRookKMoved, boolean whiteRookQMoved,
                               boolean blackRookKMoved, boolean blackRookQMoved) {
        this.whiteKingMoved = whiteKingMoved;
        this.blackKingMoved = blackKingMoved;
        this.whiteRookKMoved = whiteRookKMoved;
        this.whiteRookQMoved = whiteRookQMoved;
        this.blackRookKMoved = blackRookKMoved;
        this.blackRookQMoved = blackRookQMoved;
    }
}