import java.util.*;
import java.util.stream.Collectors;

public class ChessBot {

    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000;


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


    private static final int[][] KNIGHT_DEVELOP_BONUS = {
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,5,5,5,5,0,0},
            {0,0,5,10,10,5,0,0},
            {0,0,5,10,10,5,0,0},
            {0,0,5,5,5,5,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0}
    };

    private static final int[][] BISHOP_DEVELOP_BONUS = {
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,5,5,5,5,0,0},
            {0,0,5,8,8,5,0,0},
            {0,0,5,8,8,5,0,0},
            {0,0,5,5,5,5,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0}
    };


    private static final int[][] PAWN_CENTER_BONUS = {
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,20,20,0,0,0},
            {0,0,0,20,20,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
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
        int bonus;
        if (piece instanceof Pawn) {
            bonus = CENTER_BONUS[y][x] + PAWN_CENTER_BONUS[y][x];
            return (piece.getColor() == Side.WHITE) ? bonus : CENTER_BONUS[7 - y][x] + PAWN_CENTER_BONUS[7 - y][x];
        }
        if (piece instanceof Knight) {
            bonus = CENTER_BONUS[y][x] + KNIGHT_DEVELOP_BONUS[y][x];
            return (piece.getColor() == Side.WHITE) ? bonus : CENTER_BONUS[7 - y][x] + KNIGHT_DEVELOP_BONUS[7 - y][x];
        }
        if (piece instanceof Bishop) {
            bonus = CENTER_BONUS[y][x] + BISHOP_DEVELOP_BONUS[y][x];
            return (piece.getColor() == Side.WHITE) ? bonus : CENTER_BONUS[7 - y][x] + BISHOP_DEVELOP_BONUS[7 - y][x];
        }
        return 0;
    }


    private static int evaluate(BoardState state, Side botSide) {
        int score = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = state.getPiece(x, y);
                if (p == null) continue;
                int value = getPieceValue(p) + getPositionBonus(p, x, y);
                if (p.getColor() == botSide) score += value;
                else score -= value;
            }
        }
        return score;
    }


    private static int getMoveScore(Move move, BoardState state) {
        Piece moving = state.getPiece(move.fromX, move.fromY);
        Piece captured = move.capturedPiece;
        if (captured != null) {
            return 10 * getPieceValue(captured) - getPieceValue(moving);
        }
        if (moving instanceof Pawn && (move.toY == 0 || move.toY == 7)) {
            return 300;
        }
        return 0;
    }

    private static List<Move> sortMoves(List<Move> moves, BoardState state) {
        return moves.stream()
                .sorted(Comparator.comparingInt(m -> -getMoveScore(m, state)))
                .collect(Collectors.toList());
    }

    private static int quiescence(Board board, int alpha, int beta, Side botSide, int depth) {
        if (depth >= 2) return evaluate(board.getState(), botSide);
        int standPat = evaluate(board.getState(), botSide);
        if (standPat >= beta) return beta;
        if (alpha < standPat) alpha = standPat;

        List<Move> captures = MoveEngine.generateMoves(board.getState()).stream()
                .filter(m -> m.capturedPiece != null)
                .collect(Collectors.toList());
        captures = sortMoves(captures, board.getState());

        for (Move move : captures) {
            Board copy = board.copy();
            if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                int score = -quiescence(copy, -beta, -alpha, botSide, depth + 1);
                if (score >= beta) return beta;
                if (score > alpha) alpha = score;
            }
        }
        return alpha;
    }

    private static int alphabeta(Board board, int depth, int alpha, int beta, boolean isMaximizing, Side botSide) {
        if (depth == 0) {
            return quiescence(board, alpha, beta, botSide, 0);
        }

        List<Move> moves = MoveEngine.generateMoves(board.getState());
        if (moves.isEmpty()) {
            if (MoveEngine.isCheck(board.getState(), board.getState().getCurrentTurn()))
                return isMaximizing ? -100000 : 100000;
            else
                return 0;
        }

        moves = sortMoves(moves, board.getState());

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board copy = board.copy();
                if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                    int eval = alphabeta(copy, depth - 1, alpha, beta, false, botSide);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break;
                }
            }
            return maxEval == Integer.MIN_VALUE ? 0 : maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board copy = board.copy();
                if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                    int eval = alphabeta(copy, depth - 1, alpha, beta, true, botSide);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break;
                }
            }
            return minEval == Integer.MAX_VALUE ? 0 : minEval;
        }
    }


    public static Move getBestMove(Board board, int maxDepth) {
        Side botSide = board.getState().getCurrentTurn();
        List<Move> moves = MoveEngine.generateMoves(board.getState());
        if (moves.isEmpty()) return null;

        Move bestMove = moves.get(0);
        long startTime = System.currentTimeMillis();

        for (int depth = 1; depth <= maxDepth; depth++) {
            Move currentBestMove = null;
            int currentBestScore = Integer.MIN_VALUE;

            for (Move move : moves) {
                Board copy = board.copy();
                if (copy.makeMove(move.fromX, move.fromY, move.toX, move.toY, move.promotionPiece)) {
                    int score = alphabeta(copy, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, botSide);
                    if (score > currentBestScore) {
                        currentBestScore = score;
                        currentBestMove = move;
                    }
                }
            }
            if (currentBestMove != null) {
                bestMove = currentBestMove;
                long elapsed = System.currentTimeMillis() - startTime;
                System.out.println("Глубина " + depth + " за " + elapsed + " мс, оценка: " + currentBestScore);
            }
        }
        return bestMove;
    }
}