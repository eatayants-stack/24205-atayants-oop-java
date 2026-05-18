package engine;

import model.BoardState;
import piece.Piece;
import model.Side;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BoardRender {
    private final JButton[][] buttons = new JButton[8][8];
    private final Map<String, ImageIcon> iconCache = new HashMap<>();
    private boolean boardFlipped = false;
    private GameController controller;
    private JPanel boardPanel;

    public void createBoardPanel(JPanel boardPanel) {
        this.boardPanel = boardPanel;
        boardPanel.setLayout(new GridLayout(8, 8));
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                JButton button = getJButton(x, y);

                buttons[x][y] = button;
            }
        }
        rebuildBoardOrder();
    }

    private JButton getJButton(int x, int y) {
        JButton button = new JButton();
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.putClientProperty("x", x);
        button.putClientProperty("y", y);

        button.addActionListener(_ -> {
            if (controller != null) {
                int bx = (int) button.getClientProperty("x");
                int by = (int) button.getClientProperty("y");
                controller.handleCellClick(bx, by);
            }
        });
        return button;
    }

    private void rebuildBoardOrder() {
        if (boardPanel == null) return;

        boardPanel.removeAll();

        for (int screenY = 0; screenY < 8; screenY++) {
            for (int screenX = 0; screenX < 8; screenX++) {
                int x;
                int y;

                if (boardFlipped) {
                    y = screenY;
                    x = 7 - screenX;
                } else {

                    y = 7 - screenY;
                    x = screenX;
                }

                boardPanel.add(buttons[x][y]);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public void setBoardFlipped(boolean flipped) {
        if (this.boardFlipped != flipped) {
            this.boardFlipped = flipped;
            rebuildBoardOrder();
        }
    }


    public JButton getButton(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) return null;
        return buttons[x][y];
    }

    public void updateIcons(BoardState state) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                JButton button = buttons[x][y];
                if ((x + y) % 2 == 0) {
                    button.setBackground(new Color(119, 148, 85));
                } else {
                    button.setBackground(new Color(235, 235, 208));
                }

                Piece p = state.getPiece(x, y);
                if (p != null) {
                    String colorName = (p.getColor() == Side.WHITE) ? "White" : "Black";
                    String typeName = p.getClass().getSimpleName();
                    String fileName = colorName + "_" + typeName + ".png";

                    ImageIcon cachedIcon = getCachedIcon(fileName);
                    if (cachedIcon != null) {
                        button.setIcon(cachedIcon);
                        button.setText("");
                    } else {
                        button.setText(colorName.charAt(0) + typeName.substring(0, 1));
                        button.setIcon(null);
                    }
                } else {
                    button.setIcon(null);
                    button.setText("");
                }
            }
        }
    }

    private ImageIcon getCachedIcon(String fileName) {
        if (iconCache.containsKey(fileName)) {
            return iconCache.get(fileName);
        }

        URL imgURL = getClass().getClassLoader().getResource(fileName);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(img);
            iconCache.put(fileName, scaledIcon);
            return scaledIcon;
        }

        return null;
    }

    public void clearSelection(int selectedX, int selectedY) {
        JButton btn = getButton(selectedX, selectedY);
        if (btn != null) btn.setBorder(null);
    }

    public void highlightCell(int x, int y) {
        JButton btn = getButton(x, y);
        if (btn != null) {
            btn.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }
    }

    public void setButtonsEnabled(boolean enabled) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                buttons[x][y].setEnabled(enabled);
            }
        }
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }
}