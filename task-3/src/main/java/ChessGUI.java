import javax.swing.*;
import java.awt.*;

public class ChessGUI extends JFrame {
    private Board board;
    private final JButton[][] buttons = new JButton[8][8];
    private int selectedX = -1;
    private int selectedY = -1;
    private boolean gameActive = true;
    private boolean botThinking = false;



    private Side playerColor;
    private boolean vsBotMode;
    private boolean boardFlipped;

    public ChessGUI() {
        if (!showConfigDialog()) {
            System.exit(0);
        }

        board = new Board();
        setTitle("Java Chess Project");
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

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.addActionListener(e -> {
            if (showConfigDialog()) {
                resetGame();
            }
        });
        controlPanel.add(settingsBtn);

        add(controlPanel, BorderLayout.SOUTH);

        updateBoardIcons();
        setVisible(true);


        if (vsBotMode && board.getState().getCurrentTurn() == playerColor.opposite()) {
            startBotMove();
        }
    }


    private boolean showConfigDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.add(new JLabel("Choose your color:"));
        JRadioButton whiteBtn = new JRadioButton("Play as White", true);
        JRadioButton blackBtn = new JRadioButton("Play as Black");
        ButtonGroup colorGroup = new ButtonGroup();
        colorGroup.add(whiteBtn);
        colorGroup.add(blackBtn);
        colorPanel.add(whiteBtn);
        colorPanel.add(blackBtn);
        panel.add(colorPanel);


        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.add(new JLabel("Game mode:"));
        JRadioButton vsBotBtn = new JRadioButton("vs Bot", true);
        JRadioButton twoPlayerBtn = new JRadioButton("Two Player");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(vsBotBtn);
        modeGroup.add(twoPlayerBtn);
        modePanel.add(vsBotBtn);
        modePanel.add(twoPlayerBtn);
        panel.add(modePanel);


        twoPlayerBtn.addActionListener(e -> {
            whiteBtn.setEnabled(!twoPlayerBtn.isSelected());
            blackBtn.setEnabled(!twoPlayerBtn.isSelected());
        });
        vsBotBtn.addActionListener(e -> {
            whiteBtn.setEnabled(true);
            blackBtn.setEnabled(true);
        });


        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(this, "Game Settings");
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        Object result = optionPane.getValue();
        if (result != null && (int) result == JOptionPane.OK_OPTION) {
            playerColor = whiteBtn.isSelected() ? Side.WHITE : Side.BLACK;
            vsBotMode = vsBotBtn.isSelected();
            boardFlipped = vsBotMode && playerColor == Side.BLACK;
            return true;
        }
        return false;
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
                final int screenX = x;
                final int screenY = y;
                button.addActionListener(e -> handleCellClick(screenX, screenY));
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


        if (vsBotMode && board.getState().getCurrentTurn() == playerColor.opposite()) {
            startBotMove();
        }
    }

    private void selectPiece(int internalX, int internalY) {
        BoardState state = board.getState();
        Piece p = state.getPiece(internalX, internalY);
        if (p != null && p.getColor() == state.getCurrentTurn()) {

            clearSelection();
            selectedX = internalX;
            selectedY = internalY;

            int selScreenX = selectedX;
            int selScreenY = selectedY;
            if (boardFlipped) {
                selScreenX = 7 - selectedX;
                selScreenY = 7 - selectedY;
            }
            buttons[selScreenX][selScreenY].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }
    }

    private void handleCellClick(int screenX, int screenY) {
        if (!gameActive || botThinking) return;

        int internalX = screenX;
        int internalY = screenY;
        if (boardFlipped) {
            internalX = 7 - screenX;
            internalY = 7 - screenY;
        }

        BoardState state = board.getState();


        if (selectedX == -1) {
            selectPiece(internalX, internalY);
            return;
        }



        Piece targetPiece = state.getPiece(internalX, internalY);
        if (targetPiece != null && targetPiece.getColor() == state.getCurrentTurn()) {
            clearSelection();
            selectPiece(internalX, internalY);
            return;
        }


        Piece movingPiece = state.getPiece(selectedX, selectedY);
        boolean success;

        if (movingPiece instanceof Pawn) {
            int promoRank = (movingPiece.getColor() == Side.WHITE) ? 0 : 7;
            if (internalY == promoRank) {
                if (!movingPiece.isValidMove(state.cells, state, selectedX, selectedY, internalX, internalY)) {
                    clearSelection();
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
                    clearSelection();
                    return;
                }
                success = board.makeMove(selectedX, selectedY, internalX, internalY, choice.toLowerCase());
            } else {
                success = board.makeMove(selectedX, selectedY, internalX, internalY);
            }
        } else {
            success = board.makeMove(selectedX, selectedY, internalX, internalY);
        }

        clearSelection();

        if (success) {
            updateBoardIcons();
            state = board.getState();

            if (board.isCheckmate()) {
                JOptionPane.showMessageDialog(this, "Checkmate! " + state.getCurrentTurn().opposite() + " wins!");
                gameActive = false;
            } else if (MoveEngine.isStalemate(board)) {
                JOptionPane.showMessageDialog(this, "Stalemate! Game is a draw.");
                gameActive = false;
            } else if (board.isThreefoldRepetition()) {
                JOptionPane.showMessageDialog(this, "Threefold repetition! Game is a draw.");
                gameActive = false;
            } else if (board.isCheck()) {
                JOptionPane.showMessageDialog(this, "Check!");
            }

            if (gameActive && vsBotMode && state.getCurrentTurn() == playerColor.opposite()) {
                startBotMove();
            }
        }
    }

    private void clearSelection() {
        if (selectedX != -1) {
            int selScreenX = selectedX;
            int selScreenY = selectedY;
            if (boardFlipped) {
                selScreenX = 7 - selectedX;
                selScreenY = 7 - selectedY;
            }
            buttons[selScreenX][selScreenY].setBorder(null);
            selectedX = -1;
            selectedY = -1;
        }
    }

    private void startBotMove() {
        if (!gameActive || botThinking) return;
        botThinking = true;
        setButtonsEnabled(false);

        SwingWorker<Move, Void> worker = new SwingWorker<Move, Void>() {
            @Override
            protected Move doInBackground() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ignored) {}
                return ChessBot.getBestMove(board, 4);
            }

            @Override
            protected void done() {
                try {
                    Move bestMove = get();
                    if (bestMove != null && gameActive) {
                        boolean success = board.makeMove(bestMove.fromX, bestMove.fromY,
                                bestMove.toX, bestMove.toY, bestMove.promotionPiece);
                        if (success) {
                            updateBoardIcons();
                            BoardState state = board.getState();

                            if (board.isCheckmate()) {
                                JOptionPane.showMessageDialog(ChessGUI.this,
                                        "Checkmate! " + state.getCurrentTurn().opposite() + " wins!");
                                gameActive = false;
                            } else if (MoveEngine.isStalemate(board)) {
                                JOptionPane.showMessageDialog(ChessGUI.this,
                                        "Stalemate! Game is a draw.");
                                gameActive = false;
                            } else if (board.isCheck()) {
                                JOptionPane.showMessageDialog(ChessGUI.this, "Check!");
                            }
                        }
                    } else {

                        BoardState state = board.getState();
                        if (MoveEngine.isCheck(state, state.getCurrentTurn())) {
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
                    setButtonsEnabled(true);
                    botThinking = false;

                    if (gameActive && vsBotMode && board.getState().getCurrentTurn() == playerColor.opposite()) {
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

    private void updateBoardIcons() {
        BoardState state = board.getState();
        for (int screenX = 0; screenX < 8; screenX++) {
            for (int screenY = 0; screenY < 8; screenY++) {
                int internalX = screenX;
                int internalY = screenY;
                if (boardFlipped) {
                    internalX = 7 - screenX;
                    internalY = 7 - screenY;
                }
                Piece p = state.getPiece(internalX, internalY);
                if (p != null) {
                    String colorName = (p.getColor() == Side.WHITE) ? "White" : "Black";
                    String typeName = p.getClass().getSimpleName();
                    String fileName = colorName + "_" + typeName + ".png";
                    java.net.URL imgURL = getClass().getClassLoader().getResource(fileName);
                    if (imgURL != null) {
                        ImageIcon icon = new ImageIcon(imgURL);
                        Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        buttons[screenX][screenY].setIcon(new ImageIcon(img));
                        buttons[screenX][screenY].setText("");
                    } else {
                        buttons[screenX][screenY].setText(colorName.substring(0,1) + typeName.substring(0,1));
                        buttons[screenX][screenY].setIcon(null);
                    }
                } else {
                    buttons[screenX][screenY].setIcon(null);
                    buttons[screenX][screenY].setText("");
                }
            }
        }
    }
}