package model;

import piece.King;
import piece.Pawn;
import piece.Piece;

public class BoardState {
    public static final int WHITE_KING_MOVED  = 1;
    public static final int BLACK_KING_MOVED  = 1 << 1;
    public static final int WHITE_ROOK_K_MOVED = 1 << 2;
    public static final int WHITE_ROOK_Q_MOVED = 1 << 3;
    public static final int BLACK_ROOK_K_MOVED = 1 << 4;
    public static final int BLACK_ROOK_Q_MOVED = 1 << 5;

    private final Piece[][] cells;
    private Side currentTurn;
    private King whiteKing;
    private King blackKing;
    private int castleFlags;
    private Pawn lastDoubleMovePawn;

    public BoardState() {
        cells = new Piece[8][8];
        currentTurn = Side.WHITE;
        castleFlags = 0;
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
        this.castleFlags = other.castleFlags;
        this.whiteKing = findKing(Side.WHITE);
        this.blackKing = findKing(Side.BLACK);
        if (other.lastDoubleMovePawn != null) {
            this.lastDoubleMovePawn = (Pawn) this.cells[other.lastDoubleMovePawn.getX()][other.lastDoubleMovePawn.getY()];
        } else {
            this.lastDoubleMovePawn = null;
        }
    }

    public void putPiece(Position pos, Piece piece) {
        cells[pos.x()][pos.y()] = piece;
        if (piece != null) {
            piece.setX(pos.x());
            piece.setY(pos.y());
        }
    }

    public void movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece != null) {
            cells[to.x()][to.y()] = piece;
            piece.setX(to.x());
            piece.setY(to.y());
            cells[from.x()][from.y()] = null;
        }
    }

    public void removePiece(Position pos) {
        cells[pos.x()][pos.y()] = null;
    }

    public Piece getPiece(Position pos) {
        return getPiece(pos.x(), pos.y());
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return cells[x][y];
    }

    public boolean canCastleKingside(Side side) {
        if (side == Side.WHITE) {
            return (castleFlags & (WHITE_KING_MOVED | WHITE_ROOK_K_MOVED)) == 0;
        } else {
            return (castleFlags & (BLACK_KING_MOVED | BLACK_ROOK_K_MOVED)) == 0;
        }
    }

    public void setKingMoved(Side side) {
        if (side == Side.WHITE) castleFlags |= WHITE_KING_MOVED;
        else castleFlags |= BLACK_KING_MOVED;
    }

    public void setRookMoved(Side side, int startX) {
        if (side == Side.WHITE) {
            if (startX == 7) castleFlags |= WHITE_ROOK_K_MOVED;
            if (startX == 0) castleFlags |= WHITE_ROOK_Q_MOVED;
        } else {
            if (startX == 7) castleFlags |= BLACK_ROOK_K_MOVED;
            if (startX == 0) castleFlags |= BLACK_ROOK_Q_MOVED;
        }
    }

    public boolean equalsPosition(BoardState other) {
        if (this.currentTurn != other.currentTurn) return false;
        if (this.castleFlags != other.castleFlags) return false;

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

    public Side getCurrentTurn() { return currentTurn; }
    public void setCurrentTurn(Side side) { currentTurn = side; }

    public King getWhiteKing() { return whiteKing; }
    public King getBlackKing() { return blackKing; }

    public void setLastDoubleMovePawn(Pawn piece) { this.lastDoubleMovePawn = piece; }
    public Pawn getLastDoubleMovePawn() { return lastDoubleMovePawn; }

    public int getCastleFlags() { return castleFlags; }
    public void setCastleFlags(int flags) { this.castleFlags = flags; }

    public void updateKings() {
        this.whiteKing = findKing(Side.WHITE);
        this.blackKing = findKing(Side.BLACK);
    }

    private King findKing(Side side) {
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
}