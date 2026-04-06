public class Pawn extends Piece {
    public Pawn(int x, int y, Side side) {
        super(x, y, side, "Pawn");
    }

    public boolean isValidMove(Piece[][] board, BoardState state, int fromX, int fromY, int toX, int toY) {
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


            Pawn lastPawn = state.getLastDoubleMovePawn();
            boolean correctRank = (side == Side.WHITE && fromY == 3) || (side == Side.BLACK && fromY == 4);
            if (board[toX][toY] == null && lastPawn != null && correctRank
                    && lastPawn.getX() == toX && lastPawn.getY() == fromY) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Piece copy() {
        return new Pawn(this.x, this.y, this.side);
    }
}