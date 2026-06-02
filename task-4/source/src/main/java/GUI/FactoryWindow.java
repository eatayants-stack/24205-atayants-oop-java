package GUI;

import factory.FactoryStatistics;
import utilities.Configuration;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FactoryWindow extends JFrame implements ActionListener {
    private final FactoryController controller;
    private final Configuration config;
    private int currentRow = 1;

    private final SupplierControlPanel bodyPanel;
    private final SupplierControlPanel motorPanel;
    private final SupplierControlPanel accessoryPanel;
    private final SupplierControlPanel dealerPanel;


    private final JLabel bodyStockLabel = new JLabel("—");
    private final JLabel bodyProducedLabel = new JLabel("—");
    private final JLabel motorStockLabel = new JLabel("—");
    private final JLabel motorProducedLabel = new JLabel("—");
    private final JLabel accessoryStockLabel = new JLabel("—");
    private final JLabel accessoryProducedLabel = new JLabel("—");
    private final JLabel carStockLabel = new JLabel("—");
    private final JLabel carProducedLabel = new JLabel("—");
    private final JLabel queueLabel = new JLabel("—");

    public FactoryWindow(FactoryController controller, Configuration config) {
        this.controller = controller;
        this.config = config;
        setTitle("Car Factory");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1000, 750));
        setMinimumSize(new Dimension(900, 650));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JPanel configPanel = createConfigPanel();
        gbc.gridy = currentRow++;
        add(configPanel, gbc);


        JPanel statsPanel = createStatsTable();
        gbc.gridy = currentRow++;
        add(statsPanel, gbc);


        bodyPanel = new SupplierControlPanel(gbc, this, "Body Supplier delay (ms)");
        bodyPanel.addChangeListener(e -> controller.setBodySupplierDelay(bodyPanel.getValue()));

        motorPanel = new SupplierControlPanel(gbc, this, "Motor Supplier delay (ms)");
        motorPanel.addChangeListener(e -> controller.setMotorSupplierDelay(motorPanel.getValue()));

        accessoryPanel = new SupplierControlPanel(gbc, this, "Accessory Supplier delay (ms)");
        accessoryPanel.addChangeListener(e -> controller.setAccessorySupplierDelay(accessoryPanel.getValue()));

        dealerPanel = new SupplierControlPanel(gbc, this, "Dealer delay (ms)");
        dealerPanel.addChangeListener(e -> controller.setDealerDelay(dealerPanel.getValue()));

        Timer timer = new Timer(100, this);
        timer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.stop();
                dispose();
                System.exit(0);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        new Thread(controller::start, "Factory-Startup").start();
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Factory Configuration",
                TitledBorder.LEFT, TitledBorder.TOP));

        String htmlText = String.format(
                "<html>" +
                        "<b>Suppliers:</b> Body=%d, Motor=%d, Accessory=%d<br>" +
                        "<b>Workers:</b> %d<br>" +
                        "<b>Dealers:</b> %d<br>" +
                        "<b>Storage capacity:</b> Body=%d, Motor=%d, Accessory=%d, Car=%d" +
                        "</html>",
                config.bodySuppliers, config.motorSuppliers, config.accessorySuppliers,
                config.workers,
                config.dealers,
                config.bodyStorageSize, config.motorStorageSize,
                config.accessoryStorageSize, config.carStorageSize
        );

        JLabel label = new JLabel(htmlText);
        label.setFont(label.getFont().deriveFont(13f));
        panel.add(label);
        return panel;
    }

    private JPanel createStatsTable() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] titles = {"Component", "In Storage", "Total produced"};
        Font titleFont = new Font("SansSerif", Font.BOLD, 12);
        for (int i = 0; i < titles.length; i++) {
            gbc.gridx = i;
            JLabel titleLabel = new JLabel(titles[i], SwingConstants.CENTER);
            titleLabel.setFont(titleFont);
            panel.add(titleLabel, gbc);
        }

        // Строка Body
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Body", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        panel.add(bodyStockLabel, gbc);
        gbc.gridx = 2;
        panel.add(bodyProducedLabel, gbc);

        // Строка Motor
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Motor", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        panel.add(motorStockLabel, gbc);
        gbc.gridx = 2;
        panel.add(motorProducedLabel, gbc);

        // Строка Accessory
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Accessory", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        panel.add(accessoryStockLabel, gbc);
        gbc.gridx = 2;
        panel.add(accessoryProducedLabel, gbc);

        // Строка Car
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Car", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        panel.add(carStockLabel, gbc);
        gbc.gridx = 2;
        panel.add(carProducedLabel, gbc);


        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Assembly queue", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(queueLabel, gbc);
        gbc.gridwidth = 1;

        return panel;
    }

    public int getCurrentRow() { return currentRow; }
    public void setCurrentRow(int row) { this.currentRow = row; }

    @Override
    public void actionPerformed(ActionEvent e) {
        FactoryStatistics stat = controller.getFactoryStatistics();
        bodyStockLabel.setText(String.valueOf(stat.bodiesInStock()));
        bodyProducedLabel.setText(String.valueOf(stat.bodiesProduced()));
        motorStockLabel.setText(String.valueOf(stat.motorsInStock()));
        motorProducedLabel.setText(String.valueOf(stat.motorsProduced()));
        accessoryStockLabel.setText(String.valueOf(stat.accessoriesInStock()));
        accessoryProducedLabel.setText(String.valueOf(stat.accessoriesProduced()));
        carStockLabel.setText(String.valueOf(stat.carsInStock()));
        carProducedLabel.setText(String.valueOf(stat.carsProduced()));
        queueLabel.setText(String.valueOf(stat.queueSize()));
    }
}