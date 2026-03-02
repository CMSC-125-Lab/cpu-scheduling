package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

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
        runBtn.addActionListener(e -> frame.showScreen("SIMULATION"));

        sideBar.add(UIUtils.createStyledButton("Add Process"));
        sideBar.add(UIUtils.createStyledButton("Select Algorithm"));
        sideBar.add(UIUtils.createStyledButton("Help"));
        sideBar.add(new JLabel("")); // Spacer
        sideBar.add(runBtn);

        add(tablePanel, BorderLayout.CENTER);
        add(sideBar, BorderLayout.EAST);
    }
}
