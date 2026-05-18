package engine;

import model.BoardState;
import model.Side;
import piece.Piece;
import piece.PieceType;

import static piece.PieceValues.getValue;


public class DefaultPositionEvaluator implements PositionEvaluator {
    private static final int[][] PAWN_BONUS = {
            {0,0,0,0,0,0,0,0},
            {5,10,10,-20,-20,10,10,5},
            {5,-4,-10,0,0,-10,-4,5},
            {0,0,0,20,20,0,0,0},
            {0,0,0,25,25,0,0,0},
            {0,0,0,30,30,0,0,0},
            {0,0,0,35,35,0,0,0},
            {0,0,0,0,0,0,0,0}
    };

    private static final int[][] KNIGHT_BONUS = {
            {-10,-5,0,0,0,0,-5,-10},
            {-5,0,5,5,5,5,0,-5},
            {0,5,10,10,10,10,5,0},
            {0,5,10,15,15,10,5,0},
            {0,5,10,15,15,10,5,0},
            {0,5,10,10,10,10,5,0},
            {-5,0,5,5,5,5,0,-5},
            {-10,-5,0,0,0,0,-5,-10}
    };

    private static final int[][] BISHOP_BONUS = {
            {-10,-5,0,0,0,0,-5,-10},
            {-5,0,5,5,5,5,0,-5},
            {0,5,10,10,10,10,5,0},
            {0,5,10,15,15,10,5,0},
            {0,5,10,15,15,10,5,0},
            {0,5,10,10,10,10,5,0},
            {-5,0,5,5,5,5,0,-5},
            {-10,-5,0,0,0,0,-5,-10}
    };

    private static final int[][] ROOK_BONUS = {
            {0,0,5,10,10,5,0,0},
            {0,0,5,10,10,5,0,0},
            {0,0,5,10,10,5,0,0},
            {0,0,5,10,10,5,0,0},
            {0,0,5,10,10,5,0,0},
            {0,0,5,10,10,5,0,0},
            {25,25,25,25,25,25,25,25},
            {0,0,5,10,10,5,0,0}
    };

    private static final int[][] QUEEN_BONUS = {
            {-10,-5,0,0,0,0,-5,-10},
            {-5,0,0,0,0,0,0,-5},
            {0,0,5,5,5,5,0,0},
            {0,0,5,10,10,5,0,0},
            {0,0,5,10,10,5,0,0},
            {0,0,5,5,5,5,0,0},
            {-5,0,0,0,0,0,0,-5},
            {-10,-5,0,0,0,0,-5,-10}
    };

    private static final int[][] KING_MIDDLE = {
            {-30,-20,-10,0,0,-10,-20,-30},
            {-20,-10,0,5,5,0,-10,-20},
            {-10,0,10,15,15,10,0,-10},
            {0,5,15,20,20,15,5,0},
            {0,5,15,20,20,15,5,0},
            {-10,0,10,15,15,10,0,-10},
            {-20,-10,0,5,5,0,-10,-20},
            {-30,-20,-10,0,0,-10,-20,-30}
    };

    private static final int[][] KING_ENDGAME = {
            {20,30,10,0,0,10,30,20},
            {20,20,0,0,0,0,20,20},
            {-10,-20,0,0,0,0,-20,-10},
            {-20,-30,0,0,0,0,-30,-20},
            {-30,-40,0,0,0,0,-40,-30},
            {-40,-50,0,0,0,0,-50,-40},
            {-50,-60,0,0,0,0,-60,-50},
            {-60,-70,0,0,0,0,-70,-60}
    };

    @Override
    public int evaluate(BoardState state, Side perspective) {
        int whiteScore = 0, blackScore = 0;
        int whiteMobility = 0, blackMobility = 0;
        int totalPieces = 0;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = state.getPiece(x, y);
                if (p == null) continue;
                totalPieces++;

                int value = getValue(p);
                int yEff = (p.getColor() == Side.WHITE) ? y : 7 - y;
                int posBonus = 0;

                switch (p.getType()) {
                    case PAWN -> posBonus = PAWN_BONUS[yEff][x];
                    case KNIGHT -> posBonus = KNIGHT_BONUS[yEff][x];
                    case BISHOP -> posBonus = BISHOP_BONUS[yEff][x];
                    case ROOK -> posBonus = ROOK_BONUS[yEff][x];
                    case QUEEN -> posBonus = QUEEN_BONUS[yEff][x];
                    case KING -> {
                        boolean endgame = totalPieces <= 12;
                        posBonus = endgame ? KING_ENDGAME[yEff][x] : KING_MIDDLE[yEff][x];
                    }
                }
                value += posBonus;

                if (p.getColor() == Side.WHITE) {
                    whiteScore += value;
                    whiteMobility += p.getPossibleMoves(state).size();
                } else {
                    blackScore += value;
                    blackMobility += p.getPossibleMoves(state).size();
                }
            }
        }

        whiteScore += whiteMobility * 5;
        blackScore += blackMobility * 5;

        whiteScore += evaluatePawnStructure(state, Side.WHITE);
        blackScore += evaluatePawnStructure(state, Side.BLACK);

        int[] center = evaluateCenterControl(state);
        whiteScore += center[0];
        blackScore += center[1];

        return (perspective == Side.WHITE) ? (whiteScore - blackScore) : (blackScore - whiteScore);
    }

    private int evaluatePawnStructure(BoardState state, Side side) {
        int score = 0;
        int[] pawnsOnFile = new int[8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = state.getPiece(x, y);
                if (p != null && p.getType() == PieceType.PAWN && p.getColor() == side) {
                    pawnsOnFile[x]++;
                    boolean blocked = false;
                    int dy = (side == Side.WHITE) ? 1 : -1;
                    for (int ty = y + dy; ty >= 0 && ty < 8; ty += dy) {
                        Piece front = state.getPiece(x, ty);
                        if (front != null && front.getColor() != side && front.getType() == PieceType.PAWN) {
                            blocked = true;
                            break;
                        }
                    }
                    if (!blocked) {
                        int bonus = (side == Side.WHITE) ? (y * y) : ((7 - y) * (7 - y));
                        score += 20 + bonus;
                    }
                }
            }
        }
        for (int x = 0; x < 8; x++) {
            if (pawnsOnFile[x] > 1) score -= 15 * (pawnsOnFile[x] - 1);
            boolean isolated = (pawnsOnFile[x] > 0 &&
                    ((x > 0 && pawnsOnFile[x-1] == 0) && (x < 7 && pawnsOnFile[x+1] == 0)));
            if (isolated) score -= 20;
        }
        return score;
    }

    private int[] evaluateCenterControl(BoardState state) {
        int white = 0, black = 0;
        for (int x = 2; x <= 5; x++) {
            for (int y = 2; y <= 5; y++) {
                Piece p = state.getPiece(x, y);
                if (p == null) continue;
                int bonus = (x == 3 || x == 4) && (y == 3 || y == 4) ? 15 : 5;
                if (p.getColor() == Side.WHITE) white += bonus;
                else black += bonus;
            }
        }
        return new int[]{white, black};
    }
}