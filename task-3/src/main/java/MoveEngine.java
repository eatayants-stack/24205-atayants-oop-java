import java.util.ArrayList;
import java.util.List;

public class MoveEngine {


    public static List<Move> generateMoves(BoardState state) {
        List<Move> moves = new ArrayList<>();
        Side side = state.getCurrentTurn();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = state.getPiece(x, y);
                if (piece == null || piece.getColor() != side) continue;

                for (int tx = 0; tx < 8; tx++) {
                    for (int ty = 0; ty < 8; ty++) {
                        if (piece instanceof King && Math.abs(tx - x) == 2 && ty == y) continue;
                        if (!piece.isValidMove(state.cells, state, x, y, tx, ty)) continue;
                        if (wouldMoveCauseSelfCheck(state, x, y, tx, ty)) continue;

                        Piece captured = state.getPiece(tx, ty);
                        boolean isEnPassant = false;
                        int epPawnX = -1;


                        if (piece instanceof Pawn && Math.abs(tx - x) == 1 && captured == null) {
                            Pawn last = state.getLastDoubleMovePawn();
                            if (last != null && last.getColor() != side && last.getY() == y && last.getX() == tx) {
                                isEnPassant = true;
                                epPawnX = tx;
                            }
                        }


                        int promotionRank = (piece.getColor() == Side.WHITE) ? 0 : 7;
                        if (piece instanceof Pawn && ty == promotionRank) {

                            moves.add(new Move(x, y, tx, ty, captured, false, isEnPassant, epPawnX, "queen"));
                            moves.add(new Move(x, y, tx, ty, captured, false, isEnPassant, epPawnX, "rook"));
                            moves.add(new Move(x, y, tx, ty, captured, false, isEnPassant, epPawnX, "bishop"));
                            moves.add(new Move(x, y, tx, ty, captured, false, isEnPassant, epPawnX, "knight"));
                        } else {
                            moves.add(new Move(x, y, tx, ty, captured, false, isEnPassant, epPawnX, "queen"));
                        }
                    }
                }


                if (piece instanceof King) {
                    King king = (King) piece;
                    int ky = king.getY();

                    if (canCastle(state, king, 7, ky, 6, ky) && !wouldMoveCauseSelfCheck(state, x, y, 6, ky)) {
                        moves.add(new Move(x, y, 6, ky, null, true, false, -1, "queen"));
                    }

                    if (canCastle(state, king, 0, ky, 2, ky) && !wouldMoveCauseSelfCheck(state, x, y, 2, ky)) {
                        moves.add(new Move(x, y, 2, ky, null, true, false, -1, "queen"));
                    }
                }
            }
        }
        return moves;
    }


    public static boolean isSquareAttacked(BoardState state, int x, int y, Side attackerSide) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = state.getPiece(i, j);
                if (p != null && p.getColor() == attackerSide) {

                    if (p instanceof Pawn) {
                        int direction = (attackerSide == Side.WHITE) ? -1 : 1;
                        if (Math.abs(x - i) == 1 && (y - j) == direction) {
                            return true;
                        }
                    } else if (p.isValidMove(state.cells, state, i, j, x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isCheck(BoardState state, Side side) {
        King king = (side == Side.WHITE) ? state.whiteKing : state.blackKing;
        return king != null && isSquareAttacked(state, king.getX(), king.getY(), side.opposite());
    }

    public static boolean isStalemate(Board board) {
        if (board.isCheck()) return false;
        List<Move> moves = MoveEngine.generateMoves(board.getState());
        return moves.isEmpty();
    }

    public static boolean wouldMoveCauseSelfCheck(BoardState state, int fromX, int fromY, int toX, int toY) {
        Piece moving = state.getPiece(fromX, fromY);
        Piece target = state.getPiece(toX, toY);
        if (moving == null) return false;


        state.setPiece(toX, toY, moving);
        state.setPiece(fromX, fromY, null);
        int oldX = moving.getX(), oldY = moving.getY();
        moving.setX(toX);
        moving.setY(toY);

        boolean inCheck = isCheck(state, moving.getColor());


        moving.setX(oldX);
        moving.setY(oldY);
        state.setPiece(fromX, fromY, moving);
        state.setPiece(toX, toY, target);

        return inCheck;
    }


    private static Piece createPromotionPiece(String promotionType, int x, int y, Side side) {
        switch (promotionType.toLowerCase()) {
            case "queen":  return new Queen(x, y, side);
            case "rook":   return new Rook(x, y, side);
            case "bishop": return new Bishop(x, y, side);
            case "knight": return new Knight(x, y, side);
            default:       return new Queen(x, y, side);
        }
    }


    public static void applyPieceMove(BoardState state, int fromX, int fromY, int toX, int toY) {
        Piece piece = state.getPiece(fromX, fromY);
        state.setPiece(toX, toY, piece);
        state.setPiece(fromX, fromY, null);
        piece.setX(toX);
        piece.setY(toY);
    }

    public static void finalizeMove(BoardState state, Piece piece, int fromX, int fromY, int toX, int toY, String promotionType) {

        if (piece instanceof Pawn) {
            int promotionRank = (piece.getColor() == Side.WHITE) ? 0 : 7;
            if (toY == promotionRank) {
                Piece newPiece = createPromotionPiece(promotionType, toX, toY, piece.getColor());
                state.setPiece(toX, toY, newPiece);
            }
        }

        if (piece instanceof Pawn && Math.abs(piece.getY() - fromY) == 2) {
            state.setLastDoubleMovePawn((Pawn) piece);
        } else {
            state.setLastDoubleMovePawn(null);
        }

        if (piece instanceof King) {
            if (piece.getColor() == Side.WHITE) state.setWhiteKingMoved(true);
            else state.setBlackKingMoved(true);
        }

        if (piece instanceof Rook) {
            if (piece.getColor() == Side.WHITE) {
                if (fromX == 0) state.setWhiteRookQMoved(true);
                if (fromX == 7) state.setWhiteRookKMoved(true);
            } else {
                if (fromX == 0) state.setBlackRookQMoved(true);
                if (fromX == 7) state.setBlackRookKMoved(true);
            }
        }

        state.setCurrentTurn(state.getCurrentTurn().opposite());
    }


    public static boolean canCastle(BoardState state, King king, int rookX, int rookY, int toX, int toY) {
        Side side = king.getColor();
        boolean kingMoved = (side == Side.WHITE) ? state.isWhiteKingMoved() : state.isBlackKingMoved();
        if (kingMoved) return false;

        Piece rook = state.getPiece(rookX, rookY);
        if (!(rook instanceof Rook) || rook.getColor() != side) return false;

        boolean rookMoved;
        if (rookX == 0) {
            rookMoved = (side == Side.WHITE) ? state.isWhiteRookQMoved() : state.isBlackRookQMoved();
        } else if (rookX == 7) {
            rookMoved = (side == Side.WHITE) ? state.isWhiteRookKMoved() : state.isBlackRookKMoved();
        } else {
            return false;
        }
        if (rookMoved) return false;

        if (isCheck(state, side)) return false;
        int step = (toX > king.getX()) ? 1 : -1;
        int x = king.getX() + step;
        while (x != toX) {
            if (isSquareAttacked(state, x, king.getY(), side.opposite())) return false;
            x += step;
        }


        BoardState copy = new BoardState(state);

        King kingCopy = (side == Side.WHITE) ? copy.whiteKing : copy.blackKing;
        if (kingCopy == null) return false;
        Piece rookCopy = copy.getPiece(rookX, rookY);
        if (!(rookCopy instanceof Rook)) return false;

        int rookToX = (toX > king.getX()) ? 5 : 3;
        copy.setPiece(toX, toY, kingCopy);
        copy.setPiece(kingCopy.getX(), kingCopy.getY(), null);
        kingCopy.setX(toX);
        kingCopy.setY(toY);

        copy.setPiece(rookToX, rookY, rookCopy);
        copy.setPiece(rookX, rookY, null);
        rookCopy.setX(rookToX);
        rookCopy.setY(rookY);

        return !isCheck(copy, side);
    }

    public static void executeCastle(BoardState state, King king, int fromX, int fromY, int toX, int toY) {
        int rookFromX, rookToX;
        if (toX > fromX) {
            rookFromX = 7;
            rookToX = 5;
        } else {
            rookFromX = 0;
            rookToX = 3;
        }
        Piece rook = state.getPiece(rookFromX, fromY);

        state.setPiece(toX, toY, king);
        state.setPiece(fromX, fromY, null);
        king.setX(toX);
        king.setY(toY);

        state.setPiece(rookToX, fromY, rook);
        state.setPiece(rookFromX, fromY, null);
        rook.setX(rookToX);
        rook.setY(fromY);
    }

    public static boolean tryHandleCastling(BoardState state, Piece piece, int fromX, int fromY, int toX, int toY) {
        if (!(piece instanceof King) || Math.abs(toX - fromX) != 2 || fromY != toY) return false;
        int rookX = (toX > fromX) ? 7 : 0;
        if (canCastle(state, (King) piece, rookX, fromY, toX, toY)) {
            executeCastle(state, (King) piece, fromX, fromY, toX, toY);

            if (piece.getColor() == Side.WHITE) state.setWhiteKingMoved(true);
            else state.setBlackKingMoved(true);

            Piece rook = state.getPiece((toX > fromX) ? 5 : 3, fromY);
            if (rook instanceof Rook) {
                if (rook.getColor() == Side.WHITE) {
                    if (rook.getX() == 5) state.setWhiteRookKMoved(true);
                    else if (rook.getX() == 3) state.setWhiteRookQMoved(true);
                } else {
                    if (rook.getX() == 5) state.setBlackRookKMoved(true);
                    else if (rook.getX() == 3) state.setBlackRookQMoved(true);
                }
            }
            state.setCurrentTurn(state.getCurrentTurn().opposite());
            return true;
        }
        return false;
    }

    public static boolean tryEnPassant(BoardState state, Piece piece, int fromX, int fromY, int toX, int toY) {
        if (!(piece instanceof Pawn)) return false;
        if (Math.abs(toX - fromX) == 1 && state.getPiece(toX, toY) == null) {
            Pawn last = state.getLastDoubleMovePawn();
            if (last != null && last.getColor() != state.getCurrentTurn() &&
                    last.getY() == fromY && last.getX() == toX) {
                state.setPiece(toX, fromY, null);
                return true;
            }
        }
        return false;
    }
}