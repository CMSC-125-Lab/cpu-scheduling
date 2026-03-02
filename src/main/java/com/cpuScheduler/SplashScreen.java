package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JPanel {
    public SplashScreen() {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel logo = UIUtils.createLogoLabel(200, 200);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appTitle = new JLabel("CPU Scheduling Simulator");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        appTitle.setForeground(MainFrame.LIGHT_TEXT);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(logo);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(appTitle);

        add(content);
    }
}
