package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(MainFrame.PURPLE_BTN);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 40));
        return btn;
    }

    public static JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setForeground(MainFrame.LIGHT_TEXT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.RIGHT);
        btn.addActionListener(action);
        return btn;
    }

    public static JPanel wrap(JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.add(c);
        return p;
    }
}