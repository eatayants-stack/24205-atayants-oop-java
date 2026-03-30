public class Rook extends Piece {
    public Rook(int x, int y, Piece.Color color) {
        super(x, y, color, "Rook");
    }
    @Override
    public boolean isValidMove(Piece[][] board, int fromX, int fromY, int toX, int toY){
        if (board[toX][toY] != null && board[toX][toY].getColor() == this.color) {return false;}

        if (fromX != toX && fromY !=toY){return false;}

        int stepX = Integer.compare(toX, fromX);
        int stepY = Integer.compare(toY, fromY);

        int pathX = fromX + stepX;
        int pathY = fromY + stepY;

        while (pathX != toX || pathY != toY){
            if(board[pathX][pathY] != null){return false;}
            pathX += stepX;
            pathY += stepY;
        }
        return true;
    }
}
