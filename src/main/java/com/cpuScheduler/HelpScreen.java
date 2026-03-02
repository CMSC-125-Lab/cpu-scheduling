package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class HelpScreen extends JPanel {
    public HelpScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton backBtn = UIUtils.createNavButton("Back", e -> frame.showScreen("WELCOME"));
        add(backBtn, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        JLabel title = new JLabel("Getting Started", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(MainFrame.LIGHT_TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Using JEditorPane for HTML-like formatting
        JEditorPane helpText = new JEditorPane();
        helpText.setContentType("text/html");
        helpText.setEditable(false);
        helpText.setOpaque(false);
        helpText.setText(
            "<html><body style='color: #DCA0DC; font-family: sans-serif; font-size: 12px;'>" +
            "<table width='100%'><tr>" +
            "<td width='50%' valign='top'>" +
            "<b>Data Input Selection</b><br>" +
            "Provide process data (PID, Burst, Arrival, Priority):" +
            "<ul>" +
            "<li><b>Random Generation:</b> Generate 3-20 processes.</li>" +
            "<li><b>Manual Input:</b> Enter values directly into the table.</li>" +
            "<li><b>Input from File:</b> Upload a .csv or .txt file.</li>" +
            "</ul></td>" +
            "<td width='50%' valign='top'>" +
            "<b>Configure Algorithm Settings</b><br>" +
            "Select your desired scheduling algorithm:" +
            "<ul>" +
            "<li><b>FCFS:</b> First Come, First Serve.</li>" +
            "<li><b>SJF:</b> Shortest Job First.</li>" +
            "<li><b>Round Robin:</b> Requires a Time Quantum (1-10).</li>" +
            "</ul></td>" +
            "</tr></table></body></html>"
        );

        content.add(title, BorderLayout.NORTH);
        content.add(helpText, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        JLabel footerLogo = UIUtils.createLogoLabel(100, 100);
        add(footerLogo, BorderLayout.SOUTH);
    }
}