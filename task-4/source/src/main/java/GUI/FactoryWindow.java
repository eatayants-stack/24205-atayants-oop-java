package GUI;

import factory.FactoryStatistics;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Главное окно фабрики. Отображает слайдеры для настройки задержек
 * и обновляемую статистику (таймер каждые 100 мс).
 */
public class FactoryWindow extends JFrame implements ActionListener {
    private final FactoryController controller;
    private int currentRow = 1;   // для размещения компонентов по строкам GridBagLayout

    private final SupplierControlPanel bodyPanel;
    private final SupplierControlPanel motorPanel;
    private final SupplierControlPanel accessoryPanel;
    private final SupplierControlPanel dealerPanel;

    private final JLabel carsInStockLabel = new JLabel("Cars in stock: ");
    private final JLabel carsProducedLabel = new JLabel("Cars produced: ");
    private final JLabel queueLabel = new JLabel("Tasks in queue: ");

    public FactoryWindow(FactoryController controller) {
        this.controller = controller;
        setTitle("Car Factory");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Панели управления
        bodyPanel = new SupplierControlPanel(gbc, this, "Body Supplier");
        bodyPanel.addChangeListener(e -> controller.setBodySupplierDelay(bodyPanel.getValue()));

        motorPanel = new SupplierControlPanel(gbc, this, "Motor Supplier");
        motorPanel.addChangeListener(e -> controller.setMotorSupplierDelay(motorPanel.getValue()));

        accessoryPanel = new SupplierControlPanel(gbc, this, "Accessory Supplier");
        accessoryPanel.addChangeListener(e -> controller.setAccessorySupplierDelay(accessoryPanel.getValue()));

        dealerPanel = new SupplierControlPanel(gbc, this, "Dealers");
        dealerPanel.addChangeListener(e -> controller.setDealerDelay(dealerPanel.getValue()));

        // Нижняя информационная панель
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 5));
        infoPanel.add(carsInStockLabel);
        infoPanel.add(carsProducedLabel);
        infoPanel.add(queueLabel);
        gbc.gridy = currentRow;
        add(infoPanel, gbc);

        // Таймер обновления статистики
        Timer timer = new Timer(100, this);
        timer.start();

        // Обработка закрытия окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.stop();
                dispose();
                System.exit(0);
            }
        });

        pack();
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setVisible(true);

        // Запуск фабрики (потоки поставщиков, дилеров, сборочной линии)
        controller.start();
    }

    /** Для GridBagLayout: текущая строка, на которую добавлять следующий компонент. */
    public int getCurrentRow() { return currentRow; }
    public void setCurrentRow(int row) { this.currentRow = row; }

    /** Таймер вызывает этот метод для обновления GUI. */
    @Override
    public void actionPerformed(ActionEvent e) {
        FactoryStatistics stat = controller.getFactoryStatistics();
        bodyPanel.setInStock(stat.bodiesInStock());
        bodyPanel.setProduced(stat.bodiesProduced());
        motorPanel.setInStock(stat.motorsInStock());
        motorPanel.setProduced(stat.motorsProduced());
        accessoryPanel.setInStock(stat.accessoriesInStock());
        accessoryPanel.setProduced(stat.accessoriesProduced());
        carsInStockLabel.setText("Cars in stock: " + stat.carsInStock());
        carsProducedLabel.setText("Cars produced: " + stat.carsProduced());
        queueLabel.setText("Tasks in queue: " + stat.queueSize());
    }
}