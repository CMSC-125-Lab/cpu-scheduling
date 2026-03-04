package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Shared state
    private List<Process> processes = new ArrayList<>();
    private String selectedAlgorithm = "FCFS";
    private int timeQuantum = 3;
    private boolean higherPriorityFirst = true;

    // Global Styles
    public static final Color BG_COLOR    = new Color(20, 20, 45);
    public static final Color PURPLE_BTN  = new Color(152, 37, 152);
    public static final Color LIGHT_TEXT  = new Color(228, 145, 201);
    public static final Color ACCENT_DARK = new Color(40, 10, 60);

    private ManualInputScreen manualInputScreen;
    private SimulationScreen  simulationScreen;

    public MainFrame() {
        setTitle("CPU Scheduler Simulation");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);

        manualInputScreen = new ManualInputScreen(this);
        simulationScreen  = new SimulationScreen(this);

        // Note: no SPLASH card — splash is a separate JWindow now
        cardPanel.add(new WelcomeScreen(this),     "WELCOME");
        cardPanel.add(new InputMethodScreen(this), "INPUT_METHOD");
        cardPanel.add(manualInputScreen,           "MANUAL_INPUT");
        cardPanel.add(simulationScreen,            "SIMULATION");
        cardPanel.add(new HelpScreen(this),        "HELP");

        add(cardPanel);

        // Hide the main frame while the splash is showing
        setVisible(false);

        // Show splash as an undecorated JWindow; reveal MainFrame when done
        SplashScreen splash = new SplashScreen(() -> {
            // This runs on the EDT after the splash finishes
            showScreen("WELCOME");
            setVisible(true);
        });
        splash.showSplash();
    }

    public void showScreen(String name) {
        if ("SIMULATION".equals(name)) {
            cardPanel.remove(simulationScreen);
            simulationScreen = new SimulationScreen(this);
            cardPanel.add(simulationScreen, "SIMULATION");
        }
        if ("INPUT_METHOD".equals(name)) {
            processes.clear();
        }
        cardLayout.show(cardPanel, name);
    }

    // ---------- Shared data getters/setters ----------
    public List<Process> getProcesses()           { return processes; }
    public void setProcesses(List<Process> p)     { this.processes = p; }
    public String getSelectedAlgorithm()          { return selectedAlgorithm; }
    public void setSelectedAlgorithm(String a)    { this.selectedAlgorithm = a; }
    public int getTimeQuantum()                   { return timeQuantum; }
    public void setTimeQuantum(int q)             { this.timeQuantum = q; }
    public boolean isHigherPriorityFirst()        { return higherPriorityFirst; }
    public void setHigherPriorityFirst(boolean h) { this.higherPriorityFirst = h; }
}