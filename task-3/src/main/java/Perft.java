import java.util.List;
public class Perft {
    public static long perft(Board board, int depth) {
        if (depth == 0) return 1;
        List<Move> moves = MoveGenerator.generateMoves(board);
        long total = 0;
        for (Move move : moves) {
            Board copy = board.copy();
            if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                total += perft(copy, depth - 1);
            }
        }
        return total;
    }
}