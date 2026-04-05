public class Rook extends Piece {
    public Rook(int x, int y, Side side) { super(x, y, side, "Rook"); }
    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY) {
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.side) return false;
        if (fromX != toX && fromY != toY) return false;
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
    public Piece copy() { return new Rook(this.x, this.y, this.side); }
}
