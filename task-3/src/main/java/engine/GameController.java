package engine;

import model.*;
import move.MoveCommand;
import piece.Piece;
import piece.PieceType;
import javax.swing.*;

public class GameController {
    private Board board;
    private final BoardRender render;
    private final Configuration config;
    private final ChessBot bot;
    private final JFrame parentFrame;

    private int selectedX = -1, selectedY = -1;
    private boolean gameActive = true;
    private boolean botThinking = false;


    public GameController(Board board, BoardRender render, Configuration config, JFrame parentFrame) {
        this.board = board;
        this.render = render;
        this.config = config;
        this.parentFrame = parentFrame;
        this.bot = new ChessBot(new DefaultPositionEvaluator());
    }

    public void resetGame() {
        board = new Board();
        selectedX = -1;
        selectedY = -1;
        gameActive = true;
        botThinking = false;
        render.updateIcons(board.getState());
        render.clearSelection(selectedX, selectedY);
        if (config.isVsBotMode() && board.getState().getCurrentTurn() == config.getPlayerColor().opposite()) {
            startBotMove();
        }
    }

    public void handleCellClick(int X, int Y) {
        if (!gameActive || botThinking) return;
        BoardState state = board.getState();
        if (selectedX == -1) {
            selectPiece(X, Y);
            return;
        }

        Piece target = state.getPiece(X, Y);
        if (target != null && target.getColor() == state.getCurrentTurn()) {
            clearSelection();
            selectPiece(X, Y);
            return;
        }

        Position from = new Position(selectedX, selectedY);
        Position to = new Position(X, Y);
        boolean success;

        Piece movingPiece = state.getPiece(selectedX, selectedY);
        if (movingPiece.getType() == PieceType.PAWN && (Y == 0 || Y == 7)) {
            String[] options = {"Queen", "Rook", "Bishop", "Knight"};
            String choice = (String) JOptionPane.showInputDialog(parentFrame,
                    "Choose promotion piece:", "Pawn Promotion",
                    JOptionPane.PLAIN_MESSAGE, null, options, "Queen");
            if (choice == null) {
                clearSelection();
                return;
            }
            success = board.makeMove(from, to, choice.toLowerCase());
        } else {
            success = board.makeMove(from, to, "queen");
        }

        clearSelection();

        if (success) {
            render.updateIcons(board.getState());
            if (checkGameOver()) return;
            if (config.isVsBotMode() && board.getState().getCurrentTurn() == config.getPlayerColor().opposite()) {
                startBotMove();
            }
        }
    }

    private void selectPiece(int x, int y) {
        BoardState state = board.getState();
        Piece p = state.getPiece(x, y);
        if (p != null && p.getColor() == state.getCurrentTurn()) {
            clearSelection();
            selectedX = x;
            selectedY = y;
            render.highlightCell(x, y);
        }
    }

    private void clearSelection() {
        render.clearSelection(selectedX, selectedY);
        selectedX = -1;
        selectedY = -1;
    }

    private boolean checkGameOver() {
        BoardState state = board.getState();
        if (board.isCheckmate()) {
            JOptionPane.showMessageDialog(parentFrame, "Checkmate! " + state.getCurrentTurn().opposite() + " wins!");
            gameActive = false;
            return true;
        }
        if (board.isStalemate()) {
            JOptionPane.showMessageDialog(parentFrame, "Stalemate! Game is a draw.");
            gameActive = false;
            return true;
        }
        if (board.isThreefoldRepetition()) {
            JOptionPane.showMessageDialog(parentFrame, "Threefold repetition! Game is a draw.");
            gameActive = false;
            return true;
        }
        return false;
    }

    public void startBotMove() {
        if (!gameActive || botThinking) return;
        botThinking = true;
        render.setButtonsEnabled(false);

        SwingWorker<MoveCommand, Void> worker = new SwingWorker<>() {
            @Override
            protected MoveCommand doInBackground() {
                return bot.getBestMove(board, 4);
            }

            @Override
            protected void done() {
                try {
                    MoveCommand bestMove = get();
                    if (bestMove != null && gameActive) {
                        if (bestMove.execute(board.getState())) {
                            board.pushState();
                            render.updateIcons(board.getState());
                            checkGameOver();
                        }
                    } else {
                        if (board.isCheck()) {
                            JOptionPane.showMessageDialog(parentFrame, "Checkmate! " + board.getState().getCurrentTurn().opposite() + " wins!");
                        } else {
                            JOptionPane.showMessageDialog(parentFrame, "Stalemate! Game is a draw.");
                        }
                        gameActive = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    render.setButtonsEnabled(true);
                    botThinking = false;
                    if (gameActive && config.isVsBotMode() && board.getState().getCurrentTurn() == config.getPlayerColor().opposite()) {
                        startBotMove();
                    }
                }
            }
        };
        worker.execute();
    }

}