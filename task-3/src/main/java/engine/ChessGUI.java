package engine;

import model.Board;
import javax.swing.*;
import java.awt.*;

public class ChessGUI extends JFrame {
    private  Board board;
    private final BoardRender render;
    private GameController controller;
    private JPanel boardPanel;

    public ChessGUI() {
        Configuration config = SettingsDialog.showAndGet(this);
        if (config == null) System.exit(0);

        board = new Board();
        render = new BoardRender();
        render.setBoardFlipped(config.isBoardFlipped());

        controller = new GameController(board, render, config, this);
        render.setController(controller);

        setTitle("Java Chess Project");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        boardPanel = new JPanel();
        render.createBoardPanel(boardPanel);
        add(boardPanel, BorderLayout.CENTER);


        JPanel controlPanel = getJPanel();

        add(controlPanel, BorderLayout.SOUTH);

        render.updateIcons(board.getState());
        setVisible(true);

        if (config.isVsBotMode() && board.getState().getCurrentTurn() == config.getPlayerColor().opposite()) {
            controller.startBotMove();
        }
    }

    private JPanel getJPanel() {
        JPanel controlPanel = new JPanel();

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(event -> controller.resetGame());
        controlPanel.add(newGameBtn);

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.addActionListener(event -> {
            Configuration newConfig = SettingsDialog.showAndGet(this);
            if (newConfig != null) {
                render.setBoardFlipped(newConfig.isBoardFlipped());
                board = new Board();
                refreshBoardPanel();
                controller = new GameController(board, render, newConfig, this);
                render.setController(controller);
                controller.resetGame();
                this.getContentPane().revalidate();
                this.getContentPane().repaint();
            }
        });
        controlPanel.add(settingsBtn);

        return controlPanel;
    }
    private void refreshBoardPanel() {
        if (boardPanel != null) {
            remove(boardPanel);
        }

        boardPanel = new JPanel();
        render.createBoardPanel(boardPanel);
        add(boardPanel, BorderLayout.CENTER);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}