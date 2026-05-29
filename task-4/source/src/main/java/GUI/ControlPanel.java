package GUI;

import staff.Supplier;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ControlPanel extends JPanel {
    private final JLabel carsLabel = new JLabel("0");
    private final JLabel queueLabel = new JLabel("0");
    private final JSlider bodySlider, motorSlider, accessorySlider;

    public ControlPanel(int bodyDelay, int motorDelay, int accessoryDelay) {
        setLayout(new GridLayout(5, 2, 10, 10));
        add(new JLabel("Total cars produced:"));
        add(carsLabel);
        add(new JLabel("Tasks in queue:"));
        add(queueLabel);

        add(new JLabel("Body supplier delay (ms):"));
        bodySlider = new JSlider(100, 5000, bodyDelay);
        bodySlider.setPaintTicks(true);
        bodySlider.setMajorTickSpacing(1000);
        bodySlider.setPaintLabels(true);
        add(bodySlider);

        add(new JLabel("Motor supplier delay (ms):"));
        motorSlider = new JSlider(100, 5000, motorDelay);
        motorSlider.setPaintTicks(true);
        motorSlider.setMajorTickSpacing(1000);
        motorSlider.setPaintLabels(true);
        add(motorSlider);

        add(new JLabel("Accessory supplier delay (ms):"));
        accessorySlider = new JSlider(100, 5000, accessoryDelay);
        accessorySlider.setPaintTicks(true);
        accessorySlider.setMajorTickSpacing(1000);
        accessorySlider.setPaintLabels(true);
        add(accessorySlider);
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
}