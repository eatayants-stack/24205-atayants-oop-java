package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Компонент панели управления для одного типа поставщиков (или дилеров).
 * Содержит слайдер задержки и метки "На складе" / "Произведено".
 */
public class SupplierControlPanel extends JSlider {
    private JLabel inStockLabel;
    private JLabel producedLabel;

    /**
     * @param gbc    ограничения GridBagLayout (передаётся из окна)
     * @param window родительское окно (для добавления в нужную строку)
     * @param title  заголовок (например, "Body Supplier")
     */
    public SupplierControlPanel(GridBagConstraints gbc, FactoryWindow window, String title) {
        super(JSlider.HORIZONTAL, 0, 5000, 2500);
        // Увеличиваем строку в родительском окне и добавляем текстовую панель
        gbc.gridy = window.getCurrentRow();
        window.setCurrentRow(gbc.gridy + 1);

        JPanel textPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        textPanel.add(new JLabel(title + " delay (ms)"));
        if (!title.equals("Dealers")) {   // для дилеров не показываем "на складе" и "произведено"
            inStockLabel = new JLabel("In stock: ");
            producedLabel = new JLabel("Produced: ");
            textPanel.add(inStockLabel);
            textPanel.add(producedLabel);
        }
        window.add(textPanel, gbc);

        // Добавляем сам слайдер на следующей строке
        gbc.gridy = window.getCurrentRow();
        window.setCurrentRow(gbc.gridy + 1);
        setMajorTickSpacing(1000);
        setMinorTickSpacing(500);
        setPaintTicks(true);
        setPaintLabels(true);
        window.add(this, gbc);
    }

    public void setInStock(int count) {
        if (inStockLabel != null) inStockLabel.setText("In stock: " + count);
    }

    public void setProduced(int count) {
        if (producedLabel != null) producedLabel.setText("Produced: " + count);
    }
}