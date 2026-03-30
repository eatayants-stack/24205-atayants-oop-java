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
            // ШАГ 1: Выбираем фигуру
            Piece p = board.getPiece(x, y);
            if (p != null) {
                selectedX = x;
                selectedY = y;
                buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            }
        } else {
            // ШАГ 2: Пытаемся сделать ход
            boolean success = board.makeMove(selectedX, selectedY, x, y);

            // Снимаем выделение
            buttons[selectedX][selectedY].setBorder(null);

            if (success) {
                updateBoardIcons();
            }

            // Сбрасываем выбор для следующего хода
            selectedX = -1;
            selectedY = -1;
        }
    }

    private void updateBoardIcons() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = board.getPiece(x, y);

                if (p != null) {
                    String colorName = (p.getColor() == Piece.Color.WHITE) ? "White" : "Black";
                    String typeName = p.getClass().getSimpleName();
                    String fileName = colorName + "_" + typeName + ".png";

                    // ЗАГРУЗКА ЧЕРЕЗ CLASSLOADER (для папки resources)
                    // Путь указывается от корня ресурсов (без src/main/resources)
                    java.net.URL imgURL = getClass().getClassLoader().getResource(fileName);

                    if (imgURL != null) {
                        ImageIcon icon = new ImageIcon(imgURL);
                        Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        buttons[x][y].setIcon(new ImageIcon(img));
                        buttons[x][y].setText("");
                    } else {
                        // Если URL пустой, значит файл не найден внутри ресурсов
                        buttons[x][y].setText(colorName.substring(0,1) + typeName.substring(0,1));
                        System.out.println("Ресурс не найден: " + fileName);
                    }
                } else {
                    buttons[x][y].setIcon(null);
                    buttons[x][y].setText("");
                }
            }
        }
    }
}