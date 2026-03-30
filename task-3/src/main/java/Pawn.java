public class Pawn extends Piece {
    public Pawn(int x, int y, Piece.Color color) {
        super(x, y, color, "Pawn");
    }
    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY){
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.color) {return false;}

        int direction = (color == Color.WHITE)? -1 : 1;
        int startRow = (color == Color.WHITE)? 6 : 1;
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;

        if (deltaX == 0){
            if (deltaY == direction){
                return board[toX][toY] == null;
            }
            if(fromY == startRow && deltaY == 2*direction){
                return (board[toX][toY] == null) && (board[fromX][fromY + direction] == null);
            }
        }
        if (Math.abs(deltaX) == 1 && deltaY == direction){
            return board[toX][toY] != null;
        }
        return true;
    }
}
