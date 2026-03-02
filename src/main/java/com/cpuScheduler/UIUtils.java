package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? MainFrame.PURPLE_BTN.darker() : MainFrame.PURPLE_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBackground(MainFrame.PURPLE_BTN);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setOpaque(false);

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

    public static JLabel createLogoLabel(int width, int height) {
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        java.net.URL logoUrl = UIUtils.class.getResource("/cpu-logo.png");
        if (logoUrl != null) {
            ImageIcon rawIcon = new ImageIcon(logoUrl);
            Image scaled = rawIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        } else {
            logoLabel.setText("APP LOGO");
            logoLabel.setForeground(MainFrame.LIGHT_TEXT);
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        }

        return logoLabel;
    }

    public static JPanel wrap(JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.add(c);
        return p;
    }
}