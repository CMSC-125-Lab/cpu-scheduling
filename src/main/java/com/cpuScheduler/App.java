package com.cpuScheduler;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        System.out.println("CPU Scheduling project initialized.");
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
