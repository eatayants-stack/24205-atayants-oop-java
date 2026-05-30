package engine;

import model.*;
import move.MoveCommand;
import piece.Piece;


import java.util.*;
import java.util.stream.Collectors;

public class ChessBot {
    private final PositionEvaluator evaluator;
    private final TranspositionTable tt = new TranspositionTable();
    private final int[][] history = new int[64][64];  // fromIdx, toIdx
    private static final int MATE_SCORE = 100000;
    private static final int MAX_QUIESCENCE_DEPTH = 4;
    private static final int NULL_MOVE_REDUCTION = 2;

    public ChessBot(PositionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public MoveCommand getBestMove(Board board, int maxDepth) {
        Side botSide = board.getState().getCurrentTurn();
        MoveCommand bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int depth = 1; depth <= maxDepth; depth++) {
            MoveCommand currentBest = null;
            int currentBestScore = Integer.MIN_VALUE;

            List<MoveCommand> moves = board.generateLegalMoves();
            moves = orderMoves(moves, board.getState());

            for (MoveCommand move : moves) {
                Board copy = board.copy();
                if (move.execute(copy.getState())) {
                    long hash = tt.computeHash(copy.getState());
                    int score = alphaBeta(copy, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
                            false, botSide, hash, 0);
                    if (score > currentBestScore) {
                        currentBestScore = score;
                        currentBest = move;
                    }
                }
            }
            if (currentBest != null) {
                bestMove = currentBest;
                bestScore = currentBestScore;
            }
            if (Math.abs(bestScore) > MATE_SCORE / 2) break;
        }
        return bestMove;
    }

    private int alphaBeta(Board board, int depth, int alpha, int beta,
                          boolean maximizing, Side botSide, long hash, int ply) {
        // Транспозиция
        TranspositionTable.Entry entry = tt.probe(hash);
        if (entry != null && entry.depth() >= depth) {
            if (entry.flag() == TranspositionTable.Flag.EXACT)
                return entry.score();
            if (entry.flag() == TranspositionTable.Flag.LOWER && entry.score() >= beta)
                return entry.score();
            if (entry.flag() == TranspositionTable.Flag.UPPER && entry.score() <= alpha)
                return entry.score();
        }

        if (depth == 0) {
            return quiescence(board, alpha, beta, botSide, 0);
        }

        List<MoveCommand> moves = board.generateLegalMoves();
        if (moves.isEmpty()) {
            if (board.isCheck())
                return maximizing ? MATE_SCORE - ply : -MATE_SCORE + ply;
            else
                return 0;
        }

        if (depth >= 3 && !board.isCheck()) {
            BoardState state = board.getState();
            state.setCurrentTurn(state.getCurrentTurn().opposite());
            long nullHash = tt.computeHash(state);
            int nullScore = -alphaBeta(board, depth - 1 - NULL_MOVE_REDUCTION,
                    -beta, -beta + 1, true, botSide, nullHash, ply);
            state.setCurrentTurn(state.getCurrentTurn().opposite());
            if (nullScore >= beta) return beta;
        }

        moves = orderMoves(moves, board.getState());
        TranspositionTable.Flag flag = TranspositionTable.Flag.UPPER;
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        MoveCommand bestMove = null;

        for (MoveCommand move : moves) {
            Board copy = board.copy();
            if (!move.execute(copy.getState())) continue;
            long childHash = tt.computeHash(copy.getState());
            int score;
            if (maximizing) {
                score = alphaBeta(copy, depth - 1, alpha, beta, false, botSide, childHash, ply + 1);
                if (score > best) {
                    best = score;
                    bestMove = move;
                }
                alpha = Math.max(alpha, score);
            } else {
                score = alphaBeta(copy, depth - 1, alpha, beta, true, botSide, childHash, ply + 1);
                if (score < best) {
                    best = score;
                    bestMove = move;
                }
                beta = Math.min(beta, score);
            }
            if (beta <= alpha) {
                if (!move.isCapture() && bestMove != null) {
                    int fromIdx = bestMove.getFrom().x() * 8 + bestMove.getFrom().y();
                    int toIdx   = bestMove.getTo().x() * 8 + bestMove.getTo().y();
                    history[fromIdx][toIdx] += depth * depth;
                }
                break;
            }
        }

        if (bestMove != null) {
            flag = TranspositionTable.Flag.LOWER;
        }
        tt.store(hash, depth, best, flag, bestMove);
        return best;
    }

    private List<MoveCommand> orderMoves(List<MoveCommand> moves, BoardState state) {
        return moves.stream()
                .sorted((m1, m2) -> {
                    int s1 = getMoveScore(m1, state);
                    int s2 = getMoveScore(m2, state);
                    if (s1 != s2) return s2 - s1;
                    int fromIdx1 = m1.getFrom().x() * 8 + m1.getFrom().y();
                    int toIdx1   = m1.getTo().x() * 8 + m1.getTo().y();
                    int fromIdx2 = m2.getFrom().x() * 8 + m2.getFrom().y();
                    int toIdx2   = m2.getTo().x() * 8 + m2.getTo().y();
                    return Integer.compare(history[fromIdx2][toIdx2], history[fromIdx1][toIdx1]);
                })
                .collect(Collectors.toList());
    }

    private int getMoveScore(MoveCommand move, BoardState state) {
        if (move.isCapture()) {
            Piece captured = state.getPiece(move.getTo());
            Piece moving = state.getPiece(move.getFrom());
            if (captured != null && moving != null) {
                return getPieceValue(captured) * 10 - getPieceValue(moving);
            }
            return 0;
        }
        return 0;
    }

    private int getPieceValue(Piece p) {
        if (p == null) return 0;
        return switch (p.getType()) {
            case PAWN -> 100;
            case KNIGHT -> 320;
            case BISHOP -> 330;
            case ROOK -> 500;
            case QUEEN -> 900;
            case KING -> 20000;
        };
    }

    private int quiescence(Board board, int alpha, int beta, Side botSide, int depth) {
        if (depth >= MAX_QUIESCENCE_DEPTH)
            return evaluator.evaluate(board.getState(), botSide);

        int standPat = evaluator.evaluate(board.getState(), botSide);
        if (standPat >= beta) return beta;
        if (alpha < standPat) alpha = standPat;

        List<MoveCommand> captures = board.generateLegalMoves().stream()
                .filter(MoveCommand::isCapture)
                .sorted((m1, m2) -> {
                    int v1 = getPieceValue(board.getState().getPiece(m1.getTo()));
                    int v2 = getPieceValue(board.getState().getPiece(m2.getTo()));
                    return v2 - v1;
                })
                .toList();

        for (MoveCommand move : captures) {
            Board copy = board.copy();
            if (move.execute(copy.getState())) {
                int score = -quiescence(copy, -beta, -alpha, botSide, depth + 1);
                if (score >= beta) return beta;
                if (score > alpha) alpha = score;
            }
        }
        return alpha;
    }
}