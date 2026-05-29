package utilities;

import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    public final int bodyStorageSize, motorStorageSize, accessoryStorageSize, autoStorageSize;
    public final int bodySuppliers, motorSuppliers, accessorySuppliers;
    public final int workers, dealers, dealerDelay;
    public final boolean logSale;
    public final int bodySupplierDelay, motorSupplierDelay, accessorySupplierDelay;

    public Configuration(String resourcePath) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) throw new RuntimeException("Config file not found: " + resourcePath);
            props.load(input);
        } catch (Exception e) {
            System.err.println("Using default config.");
        }
        bodyStorageSize = Integer.parseInt(props.getProperty("StorageBodySize", "100"));
        motorStorageSize = Integer.parseInt(props.getProperty("StorageMotorSize", "100"));
        accessoryStorageSize = Integer.parseInt(props.getProperty("StorageAccessorySize", "100"));
        autoStorageSize = Integer.parseInt(props.getProperty("StorageAutoSize", "100"));
        bodySuppliers = Integer.parseInt(props.getProperty("BodySuppliers", "3"));
        motorSuppliers = Integer.parseInt(props.getProperty("MotorSuppliers", "3"));
        accessorySuppliers = Integer.parseInt(props.getProperty("AccessorySuppliers", "5"));
        workers = Integer.parseInt(props.getProperty("Workers", "10"));
        dealers = Integer.parseInt(props.getProperty("Dealers", "20"));
        dealerDelay = Integer.parseInt(props.getProperty("DealerDelay", "1000"));
        logSale = Boolean.parseBoolean(props.getProperty("LogSale", "true"));
        bodySupplierDelay = Integer.parseInt(props.getProperty("BodySupplierDelay", "500"));
        motorSupplierDelay = Integer.parseInt(props.getProperty("MotorSupplierDelay", "500"));
        accessorySupplierDelay = Integer.parseInt(props.getProperty("AccessorySupplierDelay", "500"));
    }
}