package GUI;

import staff.Supplier;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ControlPanel extends JPanel {
    private final JLabel carsLabel = new JLabel("0");
    private final JLabel queueLabel = new JLabel("0");
    private final JLabel bodyStockLabel = new JLabel("0");
    private final JLabel motorStockLabel = new JLabel("0");
    private final JLabel accessoryStockLabel = new JLabel("0");
    private final JLabel carStockLabel = new JLabel("0");
    private final JSlider bodySlider, motorSlider, accessorySlider;

    public ControlPanel(int bodyDelay, int motorDelay, int accessoryDelay) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Секция статистики -----
        JPanel statsPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Factory Statistics"));
        statsPanel.add(new JLabel("Total cars produced:"));
        statsPanel.add(carsLabel);
        statsPanel.add(new JLabel("Tasks in queue:"));
        statsPanel.add(queueLabel);
        statsPanel.add(new JLabel("Bodies in stock:"));
        statsPanel.add(bodyStockLabel);
        statsPanel.add(new JLabel("Motors in stock:"));
        statsPanel.add(motorStockLabel);
        statsPanel.add(new JLabel("Accessories in stock:"));
        statsPanel.add(accessoryStockLabel);
        statsPanel.add(new JLabel("Cars ready for sale:"));
        statsPanel.add(carStockLabel);
        add(statsPanel);
        add(Box.createVerticalStrut(15));

        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Body slider
        JPanel bodyPanel = new JPanel(new BorderLayout(5, 5));
        JLabel bodyLabel = new JLabel("Body supplier:");
        bodyLabel.setHorizontalAlignment(SwingConstants.RIGHT); // выравнивание текста вправо
        bodyPanel.add(bodyLabel, BorderLayout.WEST);
        bodySlider = new JSlider(100, 5000, bodyDelay);
        bodySlider.setMajorTickSpacing(1000);
        bodySlider.setPaintTicks(true);
        bodySlider.setPaintLabels(true);
        bodyPanel.add(bodySlider, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        controlsPanel.add(bodyLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        controlsPanel.add(bodySlider, gbc);

        // Motor slider
        JLabel motorLabel = new JLabel("Motor supplier:");
        motorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        motorSlider = new JSlider(100, 5000, motorDelay);
        motorSlider.setMajorTickSpacing(1000);
        motorSlider.setPaintTicks(true);
        motorSlider.setPaintLabels(true);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        controlsPanel.add(motorLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        controlsPanel.add(motorSlider, gbc);

        // Accessory slider
        JLabel accessoryLabel = new JLabel("Accessory supplier:");
        accessoryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        accessorySlider = new JSlider(100, 5000, accessoryDelay);
        accessorySlider.setMajorTickSpacing(1000);
        accessorySlider.setPaintTicks(true);
        accessorySlider.setPaintLabels(true);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        controlsPanel.add(accessoryLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        controlsPanel.add(accessorySlider, gbc);


        add(controlsPanel);
    }

    public void bindSuppliers(List<Supplier> bodySuppliers,
                              List<Supplier> motorSuppliers,
                              List<Supplier> accessorySuppliers) {
        bodySlider.addChangeListener(e -> {
            if (!bodySlider.getValueIsAdjusting())
                bodySuppliers.forEach(s -> s.setDelay(bodySlider.getValue()));
        });
        motorSlider.addChangeListener(e -> {
            if (!motorSlider.getValueIsAdjusting())
                motorSuppliers.forEach(s -> s.setDelay(motorSlider.getValue()));
        });
        accessorySlider.addChangeListener(e -> {
            if (!accessorySlider.getValueIsAdjusting())
                accessorySuppliers.forEach(s -> s.setDelay(accessorySlider.getValue()));
        });
    }

    public void updateCars(int totalCars) {
        SwingUtilities.invokeLater(() -> carsLabel.setText(String.valueOf(totalCars)));
    }

    public void updateQueue(int queueSize) {
        SwingUtilities.invokeLater(() -> queueLabel.setText(String.valueOf(queueSize)));
    }

    public void updateStorages(int bodyCount, int motorCount, int accessoryCount, int carCount) {
        SwingUtilities.invokeLater(() -> {
            bodyStockLabel.setText(String.valueOf(bodyCount));
            motorStockLabel.setText(String.valueOf(motorCount));
            accessoryStockLabel.setText(String.valueOf(accessoryCount));
            carStockLabel.setText(String.valueOf(carCount));
        });
    }
}