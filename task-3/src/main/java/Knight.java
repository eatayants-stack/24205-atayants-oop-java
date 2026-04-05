
public class Knight extends Piece {
    public Knight(int x, int y, Side side) { super(x, y, side, "Knight"); }
    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY) {
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.side) return false;
        int dx = Math.abs(fromX - toX);
        int dy = Math.abs(fromY - toY);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }
    @Override
    public Piece copy() { return new Knight(this.x, this.y, this.side); }
}
