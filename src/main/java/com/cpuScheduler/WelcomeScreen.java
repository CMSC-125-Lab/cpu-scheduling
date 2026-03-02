package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JPanel {
    public WelcomeScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new GridBagLayout()); // Centering content

        JLabel logo = new JLabel("APP LOGO HERE");
        logo.setFont(new Font("SansSerif", Font.BOLD, 50));
        logo.setForeground(MainFrame.LIGHT_TEXT);

        JButton startBtn = UIUtils.createStyledButton("Start");
        JButton helpBtn = UIUtils.createStyledButton("Help");

        startBtn.addActionListener(e -> frame.showScreen("INPUT_METHOD"));
        helpBtn.addActionListener(e -> frame.showScreen("HELP"));

        // Layout logic
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); // Add padding below logo
        add(logo, gbc);
        
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(startBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        btnPanel.add(helpBtn);
        
        gbc.gridy = 1;
        add(btnPanel, gbc);
    }
}
