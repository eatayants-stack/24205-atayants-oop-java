import java.util.List;

public class Perft {
    public static long perft(Board board, int depth) {
        if (depth == 0) return 1;
        List<Move> moves = MoveEngine.generateMoves(board.getState());
        long total = 0;
        for (Move move : moves) {
            Board copy = board.copy();
            if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                total += perft(copy, depth - 1);
            }
        }
        return total;
    }
    public static void perftDivide(Board board, int depth) {
        List<Move> moves = MoveEngine.generateMoves(board.getState());
        for (Move move : moves) {
            Board copy = board.copy();
            if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                long nodes = perft(copy, depth - 1);
                System.out.println(moveToString(move) + ": " + nodes);
            }
        }
    }

    private static String moveToString(Move move) {
        char fromFile = (char) ('a' + move.fromX);
        char fromRank = (char) ('1' + move.fromY);
        char toFile = (char) ('a' + move.toX);
        char toRank = (char) ('1' + move.toY);
        return "" + fromFile + fromRank + toFile + toRank;
    }
}