package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class InputMethodScreen extends JPanel {
    public InputMethodScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout());

        JButton backBtn = UIUtils.createNavButton("Back", e -> frame.showScreen("WELCOME"));
        add(backBtn, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("INPUT METHOD");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(MainFrame.LIGHT_TEXT);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JButton manual = UIUtils.createStyledButton("Manual Input");
        JButton file = UIUtils.createStyledButton("Input from File");
        JButton random = UIUtils.createStyledButton("Random Input");

        manual.addActionListener(e -> frame.showScreen("MANUAL_INPUT"));

        center.add(Box.createVerticalGlue());
        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 30)));
        center.add(UIUtils.wrap(manual));
        center.add(UIUtils.wrap(file));
        center.add(UIUtils.wrap(random));
        center.add(Box.createVerticalGlue());

        add(center, BorderLayout.CENTER);
    }
}
