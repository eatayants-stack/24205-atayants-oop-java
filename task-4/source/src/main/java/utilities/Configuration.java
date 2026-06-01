package utilities;

import java.io.InputStream;
import java.util.Properties;


public class Configuration {
    public final int bodyStorageSize, motorStorageSize, accessoryStorageSize, carStorageSize;
    public final int bodySuppliers, motorSuppliers, accessorySuppliers;
    public final int workers, dealers, dealerDelay;
    public final boolean logSale;
    public final int bodySupplierDelay, motorSupplierDelay, accessorySupplierDelay;

    public Configuration(String resourcePath) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(
                resourcePath != null ? resourcePath : "default_config.txt")) {
            if (input == null && resourcePath != null) {
                System.err.println("Config file not found: " + resourcePath + ", using defaults.");
            } else if (input != null) {
                props.load(input);
            }
        } catch (Exception e) {
            System.err.println("Error loading config, using defaults.");
        }

        bodyStorageSize = getInt(props, "StorageBodySize", 100);
        motorStorageSize = getInt(props, "StorageMotorSize", 100);
        accessoryStorageSize = getInt(props, "StorageAccessorySize", 100);
        carStorageSize = getInt(props, "StorageCarSize", 100);
        bodySuppliers = getInt(props, "BodySuppliers", 3);
        motorSuppliers = getInt(props, "MotorSuppliers", 3);
        accessorySuppliers = getInt(props, "AccessorySuppliers", 5);
        workers = getInt(props, "Workers", 10);
        dealers = getInt(props, "Dealers", 20);
        dealerDelay = getInt(props, "DealerDelay", 1000);
        logSale = getBoolean(props, "LogSale", true);
        bodySupplierDelay = getInt(props, "BodySupplierDelay", 500);
        motorSupplierDelay = getInt(props, "MotorSupplierDelay", 500);
        accessorySupplierDelay = getInt(props, "AccessorySupplierDelay", 500);
    }

    private int getInt(Properties props, String key, int defaultValue) {
        String val = props.getProperty(key);
        return val != null ? Integer.parseInt(val) : defaultValue;
    }

    private boolean getBoolean(Properties props, String key, boolean defaultValue) {
        String val = props.getProperty(key);
        return val != null ? Boolean.parseBoolean(val) : defaultValue;
    }
}