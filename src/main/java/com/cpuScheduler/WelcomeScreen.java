package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JPanel {
    public WelcomeScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Logo + Title row
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        titleRow.setOpaque(false);
        JLabel logo = UIUtils.createLogoLabel(60, 60);
        JLabel title = new JLabel("CPU Scheduling Simulator");
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(MainFrame.LIGHT_TEXT);
        titleRow.add(logo);
        titleRow.add(title);

        JLabel subtitle = new JLabel("Visualize & Understand CPU Scheduling Algorithms");
        subtitle.setFont(new Font("SansSerif", Font.ITALIC, 15));
        subtitle.setForeground(new Color(180, 120, 180));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        JButton startBtn = UIUtils.createStyledButton("▶   Start Simulation");
        JButton helpBtn  = UIUtils.createStyledButton("?   Help");
        startBtn.setPreferredSize(new Dimension(240, 44));
        helpBtn.setPreferredSize(new Dimension(240, 44));

        startBtn.addActionListener(e -> frame.showScreen("INPUT_METHOD"));
        helpBtn.addActionListener(e -> frame.showScreen("HELP"));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setOpaque(false);
        btnRow.add(startBtn);
        btnRow.add(helpBtn);

        content.add(titleRow);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 36)));
        content.add(btnRow);

        add(content);
    }
}
