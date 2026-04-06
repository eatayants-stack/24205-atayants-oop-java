import java.util.List;
import java.util.Random;

public class ChessBot {
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000;

    private static final Random random = new Random();

    // Бонусы за центральные поля (для пешек, коней, слонов)
    private static final int[][] CENTER_BONUS = {
            {0,0,0,0,0,0,0,0},
            {0,1,2,3,3,2,1,0},
            {0,2,4,6,6,4,2,0},
            {0,3,6,9,9,6,3,0},
            {0,3,6,9,9,6,3,0},
            {0,2,4,6,6,4,2,0},
            {0,1,2,3,3,2,1,0},
            {0,0,0,0,0,0,0,0}
    };

    private static int getPieceValue(Piece piece) {
        if (piece instanceof Pawn)   return PAWN_VALUE;
        if (piece instanceof Knight) return KNIGHT_VALUE;
        if (piece instanceof Bishop) return BISHOP_VALUE;
        if (piece instanceof Rook)   return ROOK_VALUE;
        if (piece instanceof Queen)  return QUEEN_VALUE;
        if (piece instanceof King)   return KING_VALUE;
        return 0;
    }

    private static int getPositionBonus(Piece piece, int x, int y) {
        if (piece instanceof Pawn || piece instanceof Knight || piece instanceof Bishop) {
            int bonus = CENTER_BONUS[y][x]; // y - rank, x - file
            return (piece.getColor() == Side.WHITE) ? bonus : CENTER_BONUS[7-y][x];
        }
        return 0;
    }

    private static int evaluateBoard(BoardState state, Side botSide) {
        int score = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = state.getPiece(x, y);
                if (p != null) {
                    int val = getPieceValue(p) + getPositionBonus(p, x, y);
                    if (p.getColor() == botSide) score += val;
                    else score -= val;
                }
            }
        }
        return score;
    }

    // Минимакс с заданной глубиной (бот максимизирует, противник минимизирует)
    private static int minimax(Board board, int depth, boolean isMaximizing, Side botSide) {
        BoardState state = board.getState();
        if (depth == 0) {
            return evaluateBoard(state, botSide);
        }

        List<Move> moves = MoveEngine.generateMoves(state);
        if (moves.isEmpty()) {
            // Нет ходов – мат или пат
            if (MoveEngine.isCheck(state)) {
                // Если шах и нет ходов – проигрыш текущей стороны
                return isMaximizing ? -100000 : 100000;
            } else {
                return 0; // пат
            }
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board copy = board.copy();
                if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                    int eval = minimax(copy, depth - 1, false, botSide);
                    if (eval > maxEval) {
                        maxEval = eval;
                    }
                }
            }
            return maxEval == Integer.MIN_VALUE ? 0 : maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board copy = board.copy();
                if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                    int eval = minimax(copy, depth - 1, true, botSide);
                    if (eval < minEval) {
                        minEval = eval;
                    }
                }
            }
            return minEval == Integer.MAX_VALUE ? 0 : minEval;
        }
    }

    public static Move getBestMove(Board board) {
        BoardState state = board.getState();
        Side botSide = state.getCurrentTurn();
        List<Move> moves = MoveEngine.generateMoves(state);
        if (moves.isEmpty()) return null;

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;


        for (Move move : moves) {
            Board copy = board.copy();
            if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {

                int score = minimax(copy, 3, false, botSide);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                } else if (score == bestScore && bestMove != null && random.nextBoolean()) {
                    bestMove = move;
                }
            }
        }

        return bestMove != null ? bestMove : moves.get(0);
    }
}