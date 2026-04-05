public class Bishop extends Piece {
    public Bishop(int x, int y, Side side) { super(x, y, side, "Bishop"); }
    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY) {
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.side) return false;
        if (Math.abs(fromX - toX) != Math.abs(fromY - toY)) return false;
        int stepX = Integer.signum(toX - fromX);
        int stepY = Integer.signum(toY - fromY);
        int x = fromX + stepX, y = fromY + stepY;
        while (x != toX || y != toY) {
            if (board[x][y] != null) return false;
            x += stepX;
            y += stepY;
        }
        return true;
    }
    @Override
    public Piece copy() { return new Bishop(this.x, this.y, this.side); }
}