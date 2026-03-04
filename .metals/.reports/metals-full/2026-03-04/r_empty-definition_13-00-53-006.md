error id: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/ManualInputScreen.java:java/awt/List#
file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/ManualInputScreen.java
empty definition using pc, found symbol in pc: java/awt/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 129
uri: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/ManualInputScreen.java
text:
```scala
package com.cpuScheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.@@List;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cpuScheduler.schedulingAlgorithms.CPUScheduler;
import com.cpuScheduler.schedulingAlgorithms.FCFS;

public class ManualInputScreen extends JPanel {
    public ManualInputScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(UIUtils.createNavButton("Back", e -> frame.showScreen("INPUT_METHOD")), BorderLayout.NORTH);

        // Left Side: Process Table Area
        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(new Color(180, 100, 180, 50)); 
        tablePanel.setBorder(BorderFactory.createTitledBorder(null, "Process List", 0, 0, null, Color.WHITE));

        // Right Side: Action Buttons
        JPanel sideBar = new JPanel();
        sideBar.setOpaque(false);
        sideBar.setLayout(new GridLayout(5, 1, 0, 15));

        JButton runBtn = UIUtils.createStyledButton("Run Simulation");
        // Inside ManualInputScreen.java
        runBtn.addActionListener(e -> {
            // Example: Reading processes (you would read these from your JTable/File)
            List<Process> processes = new ArrayList<>();
            processes.add(new Process("P1", 5, 0, 1));
            processes.add(new Process("P2", 3, 2, 2));
            processes.add(new Process("P3", 8, 4, 3));
            
            // Instantiate algorithm based on selected ComboBox (Assuming FCFS for example)
            CPUScheduler scheduler = new FCFS(processes);
            
            // Get simulation screen and start
            SimulationScreen simScreen = (SimulationScreen) frame.getContentPane().getComponent(4); // Adjust index or keep reference
            simScreen.startSimulation(scheduler);
            
            frame.showScreen("SIMULATION");
        });

        sideBar.add(UIUtils.createStyledButton("Add Process"));
        sideBar.add(UIUtils.createStyledButton("Select Algorithm"));
        sideBar.add(UIUtils.createStyledButton("Help"));
        sideBar.add(new JLabel("")); // Spacer
        sideBar.add(runBtn);

        add(tablePanel, BorderLayout.CENTER);
        add(sideBar, BorderLayout.EAST);
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/awt/List#