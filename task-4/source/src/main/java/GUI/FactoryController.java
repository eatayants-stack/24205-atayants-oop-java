package GUI;

import factory.FactoryStatistics;

/**
 * Интерфейс для связи графического интерфейса с фабрикой.
 * Реализуется классом factory.Factory.
 */
public interface FactoryController {
    void start();
    FactoryStatistics getFactoryStatistics();
    void setBodySupplierDelay(int delay);
    void setMotorSupplierDelay(int delay);
    void setAccessorySupplierDelay(int delay);
    void setDealerDelay(int delay);
    void stop();
}