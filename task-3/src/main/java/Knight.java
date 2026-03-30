public class Knight extends Piece {
    public Knight(int x, int y, Piece.Color color) {
        super(x, y, color, "Knight");
    }
    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY){
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.color) {return false;}

        int deltaX = Math.abs(fromX - toX);
        int deltaY = Math.abs(fromY - toY);

        return (deltaX == 1 && deltaY == 2) || (deltaX == 2 && deltaY == 1);

    }
}
