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

        JLabel title = new JLabel("Getting Started", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(MainFrame.LIGHT_TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JEditorPane helpText = new JEditorPane();
        helpText.setContentType("text/html");
        helpText.setEditable(false);
        helpText.setOpaque(false);
        helpText.setText(
            "<html><body style='color:#DCA0DC; font-family:sans-serif; font-size:13px; background:transparent;'>" +
            "<table width='100%' cellpadding='10'><tr>" +
            "<td width='50%' valign='top' style='background:#1a0a2a; border-radius:8px; padding:14px;'>" +
            "<b style='font-size:14px;'>📥 Data Input</b><br><br>" +
            "Provide process data (PID, Burst Time, Arrival Time, Priority):<br><br>" +
            "<b>Random:</b> Generates 3–20 processes automatically.<br><br>" +
            "<b>Manual Input:</b> Enter values directly into the table.<br><br>" +
            "<b>From File:</b> Upload a <code>.csv</code> or <code>.txt</code> file.<br>" +
            "<i>Format per line: PID, BurstTime, ArrivalTime, Priority</i>" +
            "</td>" +
            "<td width='50%' valign='top' style='background:#1a0a2a; border-radius:8px; padding:14px;'>" +
            "<b style='font-size:14px;'>⚙ Algorithms</b><br><br>" +
            "<b>FCFS:</b> First Come, First Serve.<br><br>" +
            "<b>Round Robin:</b> Time-sliced. Requires a quantum (1–10).<br><br>" +
            "<b>SJF:</b> Shortest Job First — Preemptive (SRTF) or Non-Preemptive.<br><br>" +
            "<b>Priority:</b> Preemptive or Non-Preemptive.<br>" +
            "<i>Choose whether higher or lower number means higher priority.</i>" +
            "</td></tr>" +
            "<tr><td colspan='2' style='background:#1a0a2a; border-radius:8px; padding:14px; margin-top:10px;'>" +
            "<b style='font-size:14px;'>📊 Constraints</b><br><br>" +
            "Processes: 3–20 &nbsp;|&nbsp; Burst Time: 1–30 &nbsp;|&nbsp; Arrival Time: 0–30 &nbsp;|&nbsp; " +
            "Priority: 1–20 (no duplicates) &nbsp;|&nbsp; Quantum: 1–10" +
            "</td></tr></table></body></html>"
        );

        content.add(title, BorderLayout.NORTH);
        content.add(helpText, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }
}
