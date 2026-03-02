package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Global Styles
    public static final Color BG_COLOR = new Color(20, 20, 45);
    public static final Color PURPLE_BTN = new Color(142, 45, 142);
    public static final Color LIGHT_TEXT = new Color(220, 160, 220);

    public MainFrame() {
        setTitle("CPU Scheduler Simulation");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize Screens
        cardPanel.add(new WelcomeScreen(this), "WELCOME");
        cardPanel.add(new InputMethodScreen(this), "INPUT_METHOD");
        cardPanel.add(new ManualInputScreen(this), "MANUAL_INPUT");
        cardPanel.add(new SimulationScreen(this), "SIMULATION");
        cardPanel.add(new HelpScreen(this), "HELP");

        add(cardPanel);
        setVisible(true);
    }

    public void showScreen(String name) {
        cardLayout.show(cardPanel, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}