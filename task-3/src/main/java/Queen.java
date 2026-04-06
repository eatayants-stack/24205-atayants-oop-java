public class Queen extends Piece {
    public Queen(int x, int y, Side side) { super(x, y, side, "Queen"); }
    @Override
    public boolean isValidMove(Piece[][] board, BoardState state, int fromX, int fromY, int toX, int toY) {
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.side) return false;
        if (fromX == toX || fromY == toY) {
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
        if (Math.abs(fromX - toX) == Math.abs(fromY - toY)) {
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
        return false;
    }
    @Override
    public Piece copy() { return new Queen(this.x, this.y, this.side); }
}