import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChessGUI extends JFrame {
    private Board board;
    private final JButton[][] buttons = new JButton[8][8];
    private int selectedX = -1;
    private int selectedY = -1;
    private boolean gameActive = true;
    private boolean botThinking = false;

    public ChessGUI() {
        board = new Board();
        setTitle("Java Chess Project - Play vs Bot");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        initializeButtons(boardPanel);
        add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> resetGame());
        controlPanel.add(newGameBtn);
        add(controlPanel, BorderLayout.SOUTH);

        updateBoardIcons();
        setVisible(true);
    }

    private void initializeButtons(JPanel boardPanel) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                JButton button = new JButton();
                if ((x + y) % 2 == 0) {
                    button.setBackground(new Color(235, 235, 208));
                } else {
                    button.setBackground(new Color(119, 148, 85));
                }
                button.setOpaque(true);
                button.setBorderPainted(false);
                final int finalX = x;
                final int finalY = y;
                button.addActionListener(e -> handleCellClick(finalX, finalY));
                buttons[x][y] = button;
                boardPanel.add(button);
            }
        }
    }

    private void resetGame() {
        board = new Board();
        selectedX = -1;
        selectedY = -1;
        gameActive = true;
        botThinking = false;
        updateBoardIcons();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                buttons[x][y].setBorder(null);
            }
        }

        if (gameActive && board.getState().getCurrentTurn() == Side.BLACK) {
            startBotMove();
        }
    }

    private void handleCellClick(int x, int y) {
        if (!gameActive || botThinking) return;
        BoardState state = board.getState();

        if (selectedX == -1) {
            Piece p = state.getPiece(x, y);
            if (p != null && p.getColor() == state.getCurrentTurn()) {
                selectedX = x;
                selectedY = y;
                buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            }
        } else {
            Piece movingPiece = state.getPiece(selectedX, selectedY);
            boolean success;

            if (movingPiece instanceof Pawn) {
                int promoRank = (movingPiece.getColor() == Side.WHITE) ? 0 : 7;
                if (y == promoRank) {
                    if (!movingPiece.isValidMove(state.cells, state, selectedX, selectedY, x, y)) {
                        buttons[selectedX][selectedY].setBorder(null);
                        selectedX = -1;
                        return;
                    }
                    String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                    String choice = (String) JOptionPane.showInputDialog(this,
                            "Choose promotion piece:",
                            "Pawn Promotion",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            options,
                            "Queen");
                    if (choice == null) {
                        buttons[selectedX][selectedY].setBorder(null);
                        selectedX = -1;
                        return;
                    }
                    success = board.makeMove(selectedX, selectedY, x, y, choice.toLowerCase());
                } else {
                    success = board.makeMove(selectedX, selectedY, x, y);
                }
            } else {
                success = board.makeMove(selectedX, selectedY, x, y);
            }

            buttons[selectedX][selectedY].setBorder(null);
            selectedX = -1;

            if (success) {
                updateBoardIcons();
                state = board.getState();

                if (board.isCheckmate()) {
                    JOptionPane.showMessageDialog(this, "Checkmate! " + state.getCurrentTurn().opposite() + " wins!");
                    gameActive = false;
                } else if (isStalemate()) {
                    JOptionPane.showMessageDialog(this, "Stalemate! Game is a draw.");
                    gameActive = false;
                } else if (board.isCheck()) {
                    JOptionPane.showMessageDialog(this, "Check!");
                }


                if (gameActive && state.getCurrentTurn() == Side.BLACK) {
                    startBotMove();
                }
            }
        }
    }

    private void startBotMove() {
        if (!gameActive || botThinking) return;
        botThinking = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        setButtonsEnabled(false);


        SwingWorker<Move, Void> worker = new SwingWorker<Move, Void>() {
            @Override
            protected Move doInBackground() throws Exception {

                Thread.sleep(300);
                return ChessBot.getBestMove(board);
            }

            @Override
            protected void done() {
                try {
                    Move bestMove = get();
                    if (bestMove != null && gameActive) {
                        // Применяем ход в EDT
                        boolean success = board.makeMove(bestMove.fromX, bestMove.fromY,
                                bestMove.toX, bestMove.toY,
                                bestMove.promotionPiece);
                        if (success) {
                            updateBoardIcons();
                            BoardState state = board.getState();

                            if (board.isCheckmate()) {
                                JOptionPane.showMessageDialog(ChessGUI.this,
                                        "Checkmate! " + state.getCurrentTurn().opposite() + " wins!");
                                gameActive = false;
                            } else if (isStalemate()) {
                                JOptionPane.showMessageDialog(ChessGUI.this,
                                        "Stalemate! Game is a draw.");
                                gameActive = false;
                            } else if (board.isCheck()) {
                                JOptionPane.showMessageDialog(ChessGUI.this, "Check!");
                            }
                        } else {

                            System.err.println("Bot move failed!");
                        }
                    } else {

                        BoardState state = board.getState();
                        if (MoveEngine.isCheck(state)) {
                            JOptionPane.showMessageDialog(ChessGUI.this,
                                    "Checkmate! " + state.getCurrentTurn().opposite() + " wins!");
                        } else {
                            JOptionPane.showMessageDialog(ChessGUI.this,
                                    "Stalemate! Game is a draw.");
                        }
                        gameActive = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    setCursor(Cursor.getDefaultCursor());
                    setButtonsEnabled(true);
                    botThinking = false;

                    if (gameActive && board.getState().getCurrentTurn() == Side.BLACK) {
                        startBotMove();
                    }
                }
            }
        };
        worker.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                buttons[x][y].setEnabled(enabled);
            }
        }
    }

    private boolean isStalemate() {
        if (board.isCheck()) return false;
        List<Move> moves = MoveEngine.generateMoves(board.getState());
        return moves.isEmpty();
    }

    private void updateBoardIcons() {
        BoardState state = board.getState();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = state.getPiece(x, y);
                if (p != null) {
                    String colorName = (p.getColor() == Side.WHITE) ? "White" : "Black";
                    String typeName = p.getClass().getSimpleName();
                    String fileName = colorName + "_" + typeName + ".png";
                    java.net.URL imgURL = getClass().getClassLoader().getResource(fileName);
                    if (imgURL != null) {
                        ImageIcon icon = new ImageIcon(imgURL);
                        Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        buttons[x][y].setIcon(new ImageIcon(img));
                        buttons[x][y].setText("");
                    } else {
                        buttons[x][y].setText(colorName.substring(0,1) + typeName.substring(0,1));
                        buttons[x][y].setIcon(null);
                    }
                } else {
                    buttons[x][y].setIcon(null);
                    buttons[x][y].setText("");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}