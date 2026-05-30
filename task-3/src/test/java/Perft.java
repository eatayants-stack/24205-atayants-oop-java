import model.Board;
import move.MoveCommand;

public class Perft {
    public static long perft(Board board, int depth) {
        if (depth == 0) return 1;
        long total = 0;
        for (MoveCommand move : board.generateLegalMoves()) {
            Board copy = board.copy();
            if (move.execute(copy.getState())) {
                total += perft(copy, depth - 1);
                move.undo(copy.getState());
            }
        }
        return total;
    }
}