package GUI;

import threadpool.ThreadPool;
import factory.Storage;
import detail.*;
import factory.AutoStorage;
import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private final ControlPanel controlPanel;

    public GUI(ThreadPool workerPool,
               Storage<Body> bodyStorage,
               Storage<Motor> motorStorage,
               Storage<Accessory> accessoryStorage,
               AutoStorage autoStorage,
               int bodyDelay, int motorDelay, int accessoryDelay) {
        super("Car Factory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        controlPanel = new ControlPanel(bodyDelay, motorDelay, accessoryDelay);
        add(new JScrollPane(controlPanel), BorderLayout.CENTER);

        Timer timer = new Timer(100, e -> {
            controlPanel.updateQueue(workerPool.getQueueSize());
            controlPanel.updateStorages(
                    bodyStorage.getCurrentSize(),
                    motorStorage.getCurrentSize(),
                    accessoryStorage.getCurrentSize(),
                    autoStorage.getCurrentSize()
            );
        });
        timer.start();

        pack();
        setMinimumSize(new Dimension(500, 500));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }
}