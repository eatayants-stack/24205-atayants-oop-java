package engine;

import model.BoardState;
import model.Side;
import move.MoveCommand;
import piece.Piece;
import java.util.Random;

public class TranspositionTable {
    private static final int ENTRY_COUNT = 1 << 20; // ~1 млн записей
    private final Entry[] table = new Entry[ENTRY_COUNT];
    private final ZobristHasher hasher = new ZobristHasher();

    public enum Flag { EXACT, LOWER, UPPER }

    public record Entry(long hash, int depth, int score, Flag flag, MoveCommand bestMove) {}

    public void store(long hash, int depth, int score, Flag flag, MoveCommand bestMove) {
        int idx = (int) (hash & (ENTRY_COUNT - 1));
        table[idx] = new Entry(hash, depth, score, flag, bestMove);
    }

    public Entry probe(long hash) {
        int idx = (int) (hash & (ENTRY_COUNT - 1));
        Entry entry = table[idx];
        return (entry != null && entry.hash() == hash) ? entry : null;
    }

    public long computeHash(BoardState state) {
        return hasher.computeHash(state);
    }

    private static class ZobristHasher {
        private final long[][][] pieceKeys = new long[12][8][8];
        private final long sideKey;
        private final long[] castleKeys = new long[64];
        private final long enPassantKey;

        ZobristHasher() {
            Random rnd = new Random(123456789L);
            for (int i = 0; i < 12; i++)
                for (int x = 0; x < 8; x++)
                    for (int y = 0; y < 8; y++)
                        pieceKeys[i][x][y] = rnd.nextLong();
            sideKey = rnd.nextLong();
            for (int i = 0; i < 64; i++)
                castleKeys[i] = rnd.nextLong();
            enPassantKey = rnd.nextLong();
        }

        long computeHash(BoardState state) {
            long hash = 0;
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    Piece p = state.getPiece(x, y);
                    if (p != null) {
                        int idx = pieceIndex(p);
                        hash ^= pieceKeys[idx][x][y];
                    }
                }
            }
            if (state.getCurrentTurn() == Side.BLACK)
                hash ^= sideKey;
            int flags = state.getCastleFlags();
            if (flags >= 0 && flags < 64)
                hash ^= castleKeys[flags];
            if (state.getLastDoubleMovePawn() != null) {
                hash ^= enPassantKey;
            }
            return hash;
        }

        private int pieceIndex(Piece p) {
            int typeIdx = switch (p.getType()) {
                case PAWN -> 0;
                case KNIGHT -> 1;
                case BISHOP -> 2;
                case ROOK -> 3;
                case QUEEN -> 4;
                case KING -> 5;
            };
            return typeIdx * 2 + (p.getColor() == Side.WHITE ? 0 : 1);
        }
    }
}