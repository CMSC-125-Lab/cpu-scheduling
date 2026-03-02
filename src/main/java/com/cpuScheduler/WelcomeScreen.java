package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JPanel {
    public WelcomeScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new GridBagLayout());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        titlePanel.setOpaque(false);

        JLabel logo = UIUtils.createLogoLabel(64, 64);
        JLabel title = new JLabel("CPU Scheduling Simulator");
        title.setFont(new Font("SansSerif", Font.BOLD, 44));
        title.setForeground(MainFrame.LIGHT_TEXT);
        titlePanel.add(logo);
        titlePanel.add(title);

        JButton startBtn = UIUtils.createStyledButton("Start");
        JButton helpBtn = UIUtils.createStyledButton("Help");

        startBtn.addActionListener(e -> frame.showScreen("INPUT_METHOD"));
        helpBtn.addActionListener(e -> frame.showScreen("HELP"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(titlePanel, gbc);
        
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(startBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        btnPanel.add(helpBtn);
        
        gbc.gridy = 1;
        add(btnPanel, gbc);
    }
}
