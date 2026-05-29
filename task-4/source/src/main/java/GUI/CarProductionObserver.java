package GUI;

import factory.Event;
import factory.Observer;
import factory.FactoryStat;

public class CarProductionObserver implements Observer {
    private final FactoryStat stat;
    private final ControlPanel panel;

    public CarProductionObserver(FactoryStat stat, ControlPanel panel) {
        this.stat = stat;
        this.panel = panel;
    }

    @Override
    public void update(Event event) {
        if ("STORAGE_CHANGED".equals(event.getType())) {
            panel.updateCars(stat.getTotalCarsProduced());
        }
    }
}