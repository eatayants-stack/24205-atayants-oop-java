public abstract class Piece {
    protected int x;
    protected int y;
    protected Side side;
    protected final String type;

    public Piece(int x, int y, Side side, String type) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.type = type;
    }

    public abstract boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY);
    public abstract Piece copy();

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public Side getColor() { return side; }
}