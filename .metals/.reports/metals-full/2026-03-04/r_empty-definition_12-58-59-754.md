error id: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/SimulationScreen.java:javax/swing/JComponent#setBackground().
file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/SimulationScreen.java
empty definition using pc, found symbol in pc: javax/swing/JComponent#setBackground().
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1790
uri: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/SimulationScreen.java
text:
```scala
package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class SimulationScreen extends JPanel {
    public SimulationScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Header with Title and Back Button
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Algorithm Name Simulation", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 24));
        title.setForeground(Color.WHITE);
        
        JButton backBtn = UIUtils.createNavButton("Back", e -> frame.showScreen("MANUAL_INPUT"));
        
        header.add(title, BorderLayout.CENTER);
        header.add(backBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Content: Gantt Chart and Table
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Gantt Chart Area
        GanttChartPanel ganttChart = new GanttChartPanel();
        centerPanel.add(ganttChart);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Statistics Table (Mockup)
        String[] columns = {"PID", "Burst", "Arrival", "Waiting", "Turnaround"};
        Object[][] data = { {"P1", "3", "1", "0", "3"}, {"P2", "5", "4", "0", "5"} };
        JTable table = new JTable(data, columns);
        table.setBackground(MainFrame.BG_COLOR);
        table.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground@@(MainFrame.BG_COLOR);
        
        centerPanel.add(scrollPane);
        add(centerPanel, BorderLayout.CENTER);

        JLabel footerLogo = UIUtils.createLogoLabel(140, 44);
        add(footerLogo, BorderLayout.SOUTH);
    }

    // Custom Component for Drawing the Gantt Chart
    class GanttChartPanel extends JPanel {
        public GanttChartPanel() {
            setPreferredSize(new Dimension(800, 120));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw P1 (Purple)
            g2.setColor(MainFrame.PURPLE_BTN);
            g2.fillRect(50, 20, 150, 50);
            g2.setColor(Color.WHITE);
            g2.drawString("P1", 115, 50);
            g2.drawString("1", 50, 85); // Start time

            // Draw P2 (Lighter Purple)
            g2.setColor(MainFrame.LIGHT_TEXT);
            g2.fillRect(200, 20, 250, 50);
            g2.setColor(Color.BLACK);
            g2.drawString("P2", 310, 50);
            g2.setColor(Color.WHITE);
            g2.drawString("4", 200, 85); // Transition time
            g2.drawString("8", 450, 85); // End time
        }
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: javax/swing/JComponent#setBackground().