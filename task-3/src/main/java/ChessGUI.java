import javax.swing.*;
import java.awt.*;

public class ChessGUI extends JFrame {
    private Board board;
    private JButton[][] buttons = new JButton[8][8];
    private int selectedX = -1;
    private int selectedY = -1;

    public ChessGUI() {
        board = new Board();
        setTitle("Java Chess Project");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 8));
        initializeButtons();
        updateBoardIcons();
        setVisible(true);
    }

    private void initializeButtons() {
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
                add(button);
            }
        }
    }

    private void handleCellClick(int x, int y) {
        if (selectedX == -1) {

            Piece p = board.getPiece(x, y);
            if (p != null && p.getColor() == board.getCurrentTurn()) {
                selectedX = x;
                selectedY = y;
                buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            }
        } else {

            Piece movingPiece = board.getPiece(selectedX, selectedY);
            boolean success = false;


            if (movingPiece instanceof Pawn) {
                int targetY = y;
                int promoRank = (movingPiece.getColor() == Side.WHITE) ? 0 : 7;
                if (targetY == promoRank) {
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
                    String promoType = choice.toLowerCase();
                    success = board.makeMove(selectedX, selectedY, x, y, promoType);
                } else {
                    success = board.makeMove(selectedX, selectedY, x, y);
                }
            } else {
                success = board.makeMove(selectedX, selectedY, x, y);
            }

            // Снимаем выделение
            buttons[selectedX][selectedY].setBorder(null);
            selectedX = -1;

            if (success) {
                updateBoardIcons();
                if (board.isCheckmate()) {
                    JOptionPane.showMessageDialog(this, "Checkmate! " + board.getCurrentTurn().opposite() + " wins!");
                    System.exit(0);
                } else if (board.isCheck()) {
                    JOptionPane.showMessageDialog(this, "Check!");
                }
            }
        }
    }

    private void updateBoardIcons() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = board.getPiece(x, y);
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