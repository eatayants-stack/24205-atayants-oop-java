package engine;

import model.Side;
import javax.swing.*;
import java.awt.*;

public class SettingsDialog {
    public static Configuration showAndGet(JFrame parent) {
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

        twoPlayerBtn.addActionListener(event -> {
            whiteBtn.setEnabled(!twoPlayerBtn.isSelected());
            blackBtn.setEnabled(!twoPlayerBtn.isSelected());
        });
        vsBotBtn.addActionListener(event -> {
            whiteBtn.setEnabled(true);
            blackBtn.setEnabled(true);
        });

        int result = JOptionPane.showConfirmDialog(parent, panel, "Game Settings",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Side playerColor = whiteBtn.isSelected() ? Side.WHITE : Side.BLACK;
            boolean vsBot = vsBotBtn.isSelected();

            boolean flipped;
            if (vsBot) {
                flipped = (playerColor == Side.BLACK);
            } else {
                flipped = false;
            }
            return new Configuration(playerColor, vsBot, flipped);
        }
        return null;
    }
}