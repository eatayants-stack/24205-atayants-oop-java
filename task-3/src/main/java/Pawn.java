public class Pawn extends Piece {
    private static Pawn lastDoubleMovePawn = null;

    public static Pawn getLastDoubleMovePawn() { return lastDoubleMovePawn; }
    public static void setLastDoubleMovePawn(Pawn pawn) { lastDoubleMovePawn = pawn; }
    public static void clearLastDoubleMovePawn() { lastDoubleMovePawn = null; }

    public Pawn(int x, int y, Side side) {
        super(x, y, side, "Pawn");
    }

    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY) {
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.side) return false;
        int dx = toX - fromX;
        int dy = toY - fromY;
        int direction = (side == Side.WHITE) ? -1 : 1;

        if (dx == 0) {
            if (board[toX][toY] != null) return false;
            if (dy == direction) return true;
            if (dy == 2 * direction) {
                boolean isFirstMove = (side == Side.WHITE && fromY == 6) || (side == Side.BLACK && fromY == 1);
                if (!isFirstMove) return false;
                int middleY = fromY + direction;
                if (board[fromX][middleY] != null) return false;
                return true;
            }
            return false;
        }

        if (Math.abs(dx) == 1 && dy == direction) {
            if (board[toX][toY] != null && board[toX][toY].getColor() != this.side) return true;
            if (board[toX][toY] == null && lastDoubleMovePawn != null) {
                boolean correctRank = (side == Side.WHITE && fromY == 3) || (side == Side.BLACK && fromY == 4);
                if (correctRank && lastDoubleMovePawn.getColor() != this.side) {
                    if (lastDoubleMovePawn.getX() == toX && lastDoubleMovePawn.getY() == fromY) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public Piece copy() {
        return new Pawn(this.x, this.y, this.side);
    }
}