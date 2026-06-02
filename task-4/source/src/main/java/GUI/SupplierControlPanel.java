package GUI;

import javax.swing.*;
import java.awt.*;


public class SupplierControlPanel extends JPanel {
    private final JSlider slider;
    private final JLabel valueLabel;

    public SupplierControlPanel(GridBagConstraints gbc, FactoryWindow window, String title) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        slider = new JSlider(JSlider.HORIZONTAL, 0, 5000, 2500);
        slider.setMajorTickSpacing(1000);
        slider.setMinorTickSpacing(500);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        add(slider, BorderLayout.CENTER);
        valueLabel = new JLabel("Current: " + slider.getValue() + " ms", SwingConstants.CENTER);
        add(valueLabel, BorderLayout.SOUTH);
        slider.addChangeListener(e -> valueLabel.setText("Current: " + slider.getValue() + " ms"));
        gbc.gridy = window.getCurrentRow();
        window.setCurrentRow(gbc.gridy + 1);
        window.add(this, gbc);
    }

    public void addChangeListener(javax.swing.event.ChangeListener listener) {
        slider.addChangeListener(listener);
    }

    public int getValue() {
        return slider.getValue();
    }
}