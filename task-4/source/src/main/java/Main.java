import factory.Factory;
import GUI.FactoryWindow;
import utilities.Configuration;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        String configPath = args.length > 0 ? args[0] : null;
        Configuration config = new Configuration(configPath);
        Factory factory = new Factory(config);
        SwingUtilities.invokeLater(() -> new FactoryWindow(factory, config));
    }
}