import java.util.ArrayList;
import java.util.List;
public class MoveGenerator {
    public static List<Move> generateMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        Side side = board.getCurrentTurn();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getColor() == side) {

                    for (int tx = 0; tx < 8; tx++) {
                        for (int ty = 0; ty < 8; ty++) {
                            if (piece instanceof King && Math.abs(tx - x) == 2 && ty == y)
                                continue;
                            if (piece.isValidMove(board.cells, x, y, tx, ty)) {
                                if (!board.doesMoveCauseCheck(x, y, tx, ty)) {
                                    Piece captured = board.getPiece(tx, ty);
                                    boolean isEnPassant = false;
                                    int epPawnX = -1;
                                    if (piece instanceof Pawn && Math.abs(tx - x) == 1 && captured == null) {
                                        Pawn last = Pawn.getLastDoubleMovePawn();
                                        if (last != null && last.getColor() != side && last.getY() == y && last.getX() == tx) {
                                            isEnPassant = true;
                                            epPawnX = tx;
                                        }
                                    }
                                    moves.add(new Move(x, y, tx, ty, captured, false, isEnPassant, epPawnX));
                                }
                            }
                        }
                    }
                    // Рокировка
                    if (piece instanceof King) {
                        King king = (King) piece;
                        int yk = king.getY();
                        if (board.canCastle(king, 7, yk, 6, yk) && !board.doesMoveCauseCheck(x, y, 6, yk))
                            moves.add(new Move(x, y, 6, yk, null, true, false, -1));
                        if (board.canCastle(king, 0, yk, 2, yk) && !board.doesMoveCauseCheck(x, y, 2, yk))
                            moves.add(new Move(x, y, 2, yk, null, true, false, -1));
                    }
                }
            }
        }
        return moves;
    }
}