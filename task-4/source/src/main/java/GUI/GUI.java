package GUI;


import threadpool.ThreadPool;
import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private final ControlPanel controlPanel;

    public GUI(ThreadPool workerPool,
               int bodyDelay, int motorDelay, int accessoryDelay) {
        super("Car Factory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        controlPanel = new ControlPanel( bodyDelay, motorDelay, accessoryDelay);
        add(controlPanel, BorderLayout.CENTER);

        Timer timer = new Timer(500, e -> {
            controlPanel.updateQueue(workerPool.getQueueSize());
        });
        timer.start();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }
}