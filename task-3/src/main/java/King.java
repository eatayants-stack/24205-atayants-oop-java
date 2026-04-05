public class King extends Piece {
    public King(int x, int y, Side side) {
        super(x, y, side, "King");
    }

    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY) {
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.side) {
            return false;
        }
        int deltaX = Math.abs(fromX - toX);
        int deltaY = Math.abs(fromY - toY);
        return (deltaX <= 1 && deltaY <= 1) || (deltaX == 2 && deltaY == 0);
    }

    @Override
    public Piece copy() {
        return new King(this.x, this.y, this.side);
    }
}