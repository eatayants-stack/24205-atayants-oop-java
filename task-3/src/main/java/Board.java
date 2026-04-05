import java.util.List;

public class Board {
    public Piece[][] cells;
    private Side currentTurn;
    public King whiteKing;
    public King blackKing;
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private boolean whiteRookKMoved;
    private boolean whiteRookQMoved;
    private boolean blackRookKMoved;
    private boolean blackRookQMoved;

    public Board() {
        cells = new Piece[8][8];
        loadFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Board(Board other) {
        this.cells = new Piece[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (other.cells[x][y] != null) {
                    this.cells[x][y] = other.cells[x][y].copy();
                }
            }
        }
        this.currentTurn = other.currentTurn;
        this.whiteKingMoved = other.whiteKingMoved;
        this.blackKingMoved = other.blackKingMoved;
        this.whiteRookKMoved = other.whiteRookKMoved;
        this.whiteRookQMoved = other.whiteRookQMoved;
        this.blackRookKMoved = other.blackRookKMoved;
        this.blackRookQMoved = other.blackRookQMoved;
        if (other.whiteKing != null) this.whiteKing = (King) this.cells[other.whiteKing.getX()][other.whiteKing.getY()];
        if (other.blackKing != null) this.blackKing = (King) this.cells[other.blackKing.getX()][other.blackKing.getY()];
    }

    private void loadFromFEN(String fen) {
        String[] parts = fen.split(" ");
        String boardPart = parts[0];
        String turnPart = parts[1];
        String castlePart = parts[2];

        cells = new Piece[8][8];
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteRookKMoved = false;
        whiteRookQMoved = false;
        blackRookKMoved = false;
        blackRookQMoved = false;

        int x = 0, y = 7;
        for (char c : boardPart.toCharArray()) {
            if (c == '/') { x = 0; y--; }
            else if (Character.isDigit(c)) { x += (c - '0'); }
            else {
                Side side = Character.isUpperCase(c) ? Side.BLACK : Side.WHITE;
                char lower = Character.toLowerCase(c);
                if (lower == 'k') {
                    King king = new King(x, y, side);
                    cells[x][y] = king;
                    if (side == Side.WHITE) whiteKing = king;
                    else blackKing = king;
                } else {
                    cells[x][y] = createPiece(lower, x, y, side);
                }
                x++;
            }
        }
        currentTurn = turnPart.equals("w") ? Side.WHITE : Side.BLACK;
        if (!castlePart.contains("K")) whiteRookKMoved = true;
        if (!castlePart.contains("Q")) whiteRookQMoved = true;
        if (!castlePart.contains("k")) blackRookKMoved = true;
        if (!castlePart.contains("q")) blackRookQMoved = true;
    }

    private Piece createPiece(char type, int x, int y, Side side) {
        switch (type) {
            case 'p': return new Pawn(x, y, side);
            case 'n': return new Knight(x, y, side);
            case 'b': return new Bishop(x, y, side);
            case 'r': return new Rook(x, y, side);
            case 'q': return new Queen(x, y, side);
            default: throw new IllegalArgumentException();
        }
    }

    private Piece createPromotionPiece(String promotionType, int x, int y, Side side) {
        if (promotionType == null) promotionType = "queen";
        switch (promotionType.toLowerCase()) {
            case "queen":  return new Queen(x, y, side);
            case "rook":   return new Rook(x, y, side);
            case "bishop": return new Bishop(x, y, side);
            case "knight": return new Knight(x, y, side);
            default:       return new Queen(x, y, side);
        }
    }

    public boolean isSquareAttacked(int x, int y, Side side) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = cells[i][j];
                if (p != null && p.getColor() == side) {
                    if (p.isValidMove(cells, i, j, x, y)) return true;
                }
            }
        }
        return false;
    }

    private boolean isKingInCheck(Side side) {
        King king = (side == Side.WHITE) ? whiteKing : blackKing;
        return king != null && isSquareAttacked(king.getX(), king.getY(), side.opposite());
    }

    public boolean doesMoveCauseCheck(int fromX, int fromY, int toX, int toY) {
        Piece moving = cells[fromX][fromY];
        Piece target = cells[toX][toY];
        if (moving == null) return false;
        cells[toX][toY] = moving;
        cells[fromX][fromY] = null;
        int oldX = moving.getX(), oldY = moving.getY();
        moving.setX(toX);
        moving.setY(toY);
        boolean inCheck = isKingInCheck(moving.getColor());
        moving.setX(oldX);
        moving.setY(oldY);
        cells[fromX][fromY] = moving;
        cells[toX][toY] = target;
        return inCheck;
    }
    public boolean isCheck() {
        Side side = currentTurn;
        King king = (side == Side.WHITE) ? whiteKing : blackKing;
        return isSquareAttacked(king.getX(), king.getY(), side.opposite());
    }

    public boolean isCheckmate() {
        if (!isCheck()) return false;
        List<Move> moves = MoveGenerator.generateMoves(this);
        for (Move move : moves) {
            Board copy = this.copy();
            if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                return false;
            }
        }
        return true;
    }
    public boolean canCastle(King king, int rookX, int rookY, int toX, int toY) {
        Side side = king.getColor();
        boolean kingMoved = (side == Side.WHITE) ? whiteKingMoved : blackKingMoved;
        if (kingMoved) return false;
        boolean rookMoved;
        if (rookX == 0) rookMoved = (side == Side.WHITE) ? whiteRookQMoved : blackRookQMoved;
        else if (rookX == 7) rookMoved = (side == Side.WHITE) ? whiteRookKMoved : blackRookKMoved;
        else return false;
        if (rookMoved) return false;
        int step = (toX > king.getX()) ? 1 : -1;
        int cx = king.getX() + step;
        while (cx != rookX) {
            if (cells[cx][king.getY()] != null) return false;
            if (isSquareAttacked(cx, king.getY(), side.opposite())) return false;
            cx += step;
        }
        if (isSquareAttacked(king.getX(), king.getY(), side.opposite())) return false;
        if (isSquareAttacked(toX, toY, side.opposite())) return false;
        return true;
    }

    private void executeCastle(King king, int fromX, int fromY, int toX, int toY) {
        int rookFromX, rookToX;
        if (toX > fromX) { rookFromX = 7; rookToX = 5; }
        else { rookFromX = 0; rookToX = 3; }
        Piece rook = cells[rookFromX][fromY];
        cells[toX][toY] = king;
        cells[fromX][fromY] = null;
        king.setX(toX);
        king.setY(toY);
        cells[rookToX][fromY] = rook;
        cells[rookFromX][fromY] = null;
        rook.setX(rookToX);
        rook.setY(fromY);
    }

    public boolean makeMove(int fromX, int fromY, int toX, int toY, String promotionType) {
        Piece piece = cells[fromX][fromY];
        if (piece == null || piece.getColor() != currentTurn) return false;

        if (piece instanceof King && Math.abs(toX - fromX) == 2 && fromY == toY) {
            int rookX = (toX > fromX) ? 7 : 0;
            if (canCastle((King) piece, rookX, fromY, toX, toY)) {
                executeCastle((King) piece, fromX, fromY, toX, toY);
                if (piece.getColor() == Side.WHITE) whiteKingMoved = true;
                else blackKingMoved = true;
                Pawn.clearLastDoubleMovePawn();
                currentTurn = currentTurn.opposite();
                return true;
            }
            return false;
        }

        if (!piece.isValidMove(cells, fromX, fromY, toX, toY)) return false;

        boolean isEnPassant = false;
        int epPawnX = -1;
        if (piece instanceof Pawn && Math.abs(toX - fromX) == 1 && cells[toX][toY] == null) {
            Pawn last = Pawn.getLastDoubleMovePawn();
            if (last != null && last.getColor() != currentTurn && last.getY() == fromY && last.getX() == toX) {
                isEnPassant = true;
                epPawnX = toX;
            }
        }

        if (isEnPassant) {
            cells[epPawnX][fromY] = null;
        }

        if (doesMoveCauseCheck(fromX, fromY, toX, toY)) {
            if (isEnPassant) {
                cells[epPawnX][fromY] = Pawn.getLastDoubleMovePawn();
            }
            return false;
        }

        cells[toX][toY] = piece;
        cells[fromX][fromY] = null;
        piece.setX(toX);
        piece.setY(toY);

        if (isEnPassant) {
        }

        if (piece instanceof Pawn) {
            int promotionRank = (piece.getColor() == Side.WHITE) ? 0 : 7;
            if (toY == promotionRank) {
                Piece newPiece = createPromotionPiece(promotionType, toX, toY, piece.getColor());
                cells[toX][toY] = newPiece;
                piece = newPiece;
            }
        }

        if (piece instanceof King) {
            if (piece.getColor() == Side.WHITE) whiteKingMoved = true;
            else blackKingMoved = true;
        }
        if (piece instanceof Rook) {
            if (piece.getColor() == Side.WHITE) {
                if (fromX == 0) whiteRookQMoved = true;
                if (fromX == 7) whiteRookKMoved = true;
            } else {
                if (fromX == 0) blackRookQMoved = true;
                if (fromX == 7) blackRookKMoved = true;
            }
        }

        if (piece instanceof Pawn && Math.abs(toY - fromY) == 2) {
            Pawn.setLastDoubleMovePawn((Pawn) piece);
        } else {
            Pawn.clearLastDoubleMovePawn();
        }

        currentTurn = currentTurn.opposite();
        return true;
    }

    public boolean makeMove(int fromX, int fromY, int toX, int toY) {
        return makeMove(fromX, fromY, toX, toY, "queen");
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return cells[x][y];
    }

    public Side getCurrentTurn() { return currentTurn; }
    public Board copy() { return new Board(this); }
}