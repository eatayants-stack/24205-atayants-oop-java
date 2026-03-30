public class King extends Piece {
    public King(int x, int y, Piece.Color color) {
        super(x, y, color, "King");
    }
    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY){
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.color) {return false;}

        int deltaX = Math.abs(fromX - toX);
        int deltaY = Math.abs(fromY - toY);

        return (deltaX <= 1 && deltaY <= 1);
    }
}
