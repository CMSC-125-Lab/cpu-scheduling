package com.cpuScheduler.ui;

import javax.swing.*;
import java.awt.*;

public class HelpScreen extends JPanel {
    public HelpScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));

        JButton backBtn = UIUtils.createNavButton("← Back", e -> frame.showScreen("WELCOME"));
        add(backBtn, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        JLabel title = new JLabel("How to Use", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(MainFrame.LIGHT_TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JEditorPane helpText = new JEditorPane();
        helpText.setContentType("text/html");
        helpText.setEditable(false);
        helpText.setOpaque(false);
        helpText.setText(
            "<html><body style='color:#DCA0DC; font-family:sans-serif; font-size:13px; background:transparent;'>" +
            "<div style='background:#1a0a2a; border-radius:8px; padding:16px; margin-bottom:12px;'>" +
            "<b style='font-size:16px;'>Step 1: Start the Application</b><br><br>" +
            "Click the <b>'Start'</b> button on the welcome screen to begin.<br>" +
            "</div>" +
            
            "<div style='background:#1a0a2a; border-radius:8px; padding:16px; margin-bottom:12px;'>" +
            "<b style='font-size:16px;'>Step 2: Choose Input Method</b><br><br>" +
            "Select how you want to provide process data:<br><br>" +
            "• <b>Random:</b> Automatically generates 3-20 processes with random values<br>" +
            "• <b>Manual Input:</b> Enter process details manually in a table<br>" +
            "• <b>From File:</b> Upload a CSV or TXT file with process data<br>" +
            "</div>" +
            
            "<div style='background:#1a0a2a; border-radius:8px; padding:16px; margin-bottom:12px;'>" +
            "<b style='font-size:16px;'>Step 3: Enter Process Data (Manual Input)</b><br><br>" +
            "If you chose manual input:<br><br>" +
            "1. Click <b>'+ Add Process'</b> to add a new process row<br>" +
            "2. Click on table cells to edit values:<br>" +
            "&nbsp;&nbsp;&nbsp;• PID: Process identifier (e.g., P1, P2)<br>" +
            "&nbsp;&nbsp;&nbsp;• Burst Time: 1-30 ms<br>" +
            "&nbsp;&nbsp;&nbsp;• Arrival Time: 0-30 ms<br>" +
            "&nbsp;&nbsp;&nbsp;• Priority: 1-20 (no duplicates)<br>" +
            "3. Use <b>'− Remove Selected'</b> to delete a process<br>" +
            "4. Use <b>'Clear All'</b> to start over<br>" +
            "</div>" +
            
            "<div style='background:#1a0a2a; border-radius:8px; padding:16px; margin-bottom:12px;'>" +
            "<b style='font-size:16px;'>Step 4: Select Scheduling Algorithm</b><br><br>" +
            "Click <b>'Select Algorithm'</b> and choose from:<br><br>" +
            "• <b>FCFS:</b> First Come, First Served<br>" +
            "• <b>Round Robin:</b> Requires time quantum (1-10 ms)<br>" +
            "• <b>SJF:</b> Shortest Job First (Preemptive or Non-Preemptive)<br>" +
            "• <b>Priority:</b> Priority-based (Preemptive or Non-Preemptive)<br>" +
            "</div>" +
            
            "<div style='background:#1a0a2a; border-radius:8px; padding:16px; margin-bottom:12px;'>" +
            "<b style='font-size:16px;'>Step 5: Run Simulation</b><br><br>" +
            "1. Click <b>'▶ Run Simulation'</b> to start<br>" +
            "2. Watch the Gantt chart animation showing process execution<br>" +
            "3. Use <b>'▶ Play'</b> to start/resume and <b>'⏸ Pause'</b> to pause<br>" +
            "4. Adjust <b>Speed</b> slider to control animation speed<br>" +
            "5. Click <b>'⟳ Reset'</b> to restart the simulation<br>" +
            "</div>" +
            
            "<div style='background:#1a0a2a; border-radius:8px; padding:16px;'>" +
            "<b style='font-size:16px;'>Step 6: View Results</b><br><br>" +
            "The results table shows for each process:<br><br>" +
            "• <b>WT:</b> Waiting Time<br>" +
            "• <b>TAT:</b> Turnaround Time<br>" +
            "• <b>Avg WT:</b> Average Waiting Time across all processes<br>" +
            "• <b>Avg TAT:</b> Average Turnaround Time across all processes<br>" +
            "</div>" +
            
            "</body></html>"
        );

        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        content.add(title, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }
}
