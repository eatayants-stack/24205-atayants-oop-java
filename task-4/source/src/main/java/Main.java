import factory.Factory;
import GUI.FactoryWindow;
import utilities.Configuration;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Если передан аргумент командной строки – используем его как путь к конфигу,
        // иначе загружаем default_config.txt из ресурсов.
        String configPath = args.length > 0 ? args[0] : null;
        Configuration config = new Configuration(configPath);
        Factory factory = new Factory(config);

        // GUI должен создаваться в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> new FactoryWindow(factory));
    }
}