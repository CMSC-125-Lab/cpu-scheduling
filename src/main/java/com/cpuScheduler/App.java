 package com.cpuScheduler;

import com.cpuScheduler.ui.MainFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        System.out.println("CPU Scheduling project initialized.");
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
