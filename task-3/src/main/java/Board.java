public class Board {
    private Piece[][] cells;

    public Board() {
        cells = new Piece[8][8];
        setupBoard();
    }

    private void setupBoard() {
        cells[0][0] = new Rook(0, 0, Piece.Color.BLACK);
        cells[1][0] = new Knight(1, 0, Piece.Color.BLACK);
        cells[2][0] = new Bishop(2, 0, Piece.Color.BLACK);
        cells[3][0] = new Queen(3, 0, Piece.Color.BLACK);
        cells[4][0] = new King(4, 0, Piece.Color.BLACK);
        cells[5][0] = new Bishop(5, 0, Piece.Color.BLACK);
        cells[6][0] = new Knight(6, 0, Piece.Color.BLACK);
        cells[7][0] = new Rook(7, 0, Piece.Color.BLACK);

        for (int i = 0; i < 8; i++) {
            cells[i][1] = new Pawn(i, 1, Piece.Color.BLACK);
        }



        cells[0][7] = new Rook(0, 7, Piece.Color.WHITE);
        cells[1][7] = new Knight(1, 7, Piece.Color.WHITE);
        cells[2][7] = new Bishop(2, 7, Piece.Color.WHITE);
        cells[3][7] = new Queen(3, 7, Piece.Color.WHITE);
        cells[4][7] = new King(4, 7, Piece.Color.WHITE);
        cells[5][7] = new Bishop(5, 7, Piece.Color.WHITE);
        cells[6][7] = new Knight(6, 7, Piece.Color.WHITE);
        cells[7][7] = new Rook(7, 7, Piece.Color.WHITE);
        for (int i = 0; i < 8; i++) {
            cells[i][6] = new Pawn(i, 6, Piece.Color.WHITE);
        }
    }


    public boolean makeMove(int fromX, int fromY, int toX, int toY) {
        Piece piece = getPiece(fromX, fromY);

        if (piece == null) return false;

        if (piece.isValidMove(cells, fromX, fromY, toX, toY)) {

            cells[toX][toY] = piece;
            cells[fromX][fromY] = null;

            piece.setX(toX);
            piece.setY(toY);

            return true;
        }

        return false;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return cells[x][y];
    }
}