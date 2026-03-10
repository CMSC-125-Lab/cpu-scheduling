package com.cpuScheduler.ui;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getBackground();
                g2.setColor(getModel().isPressed()
                    ? base.darker()
                    : getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
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
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setForeground(MainFrame.LIGHT_TEXT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
            logoLabel.setText("⚙");
            logoLabel.setFont(new Font("SansSerif", Font.PLAIN, Math.min(width, height)));
            logoLabel.setForeground(MainFrame.PURPLE_BTN);
        }
        return logoLabel;
    }

    /**
     * Applies a 0–100 volume level to the given Clip via its MASTER_GAIN control.
     * 0 = muted, 100 = full volume (0 dB). Uses a logarithmic (dB) scale.
     */
    public static void applyVolume(Clip clip, int volumePercent) {
        if (clip == null || !clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        if (volumePercent <= 0) {
            gain.setValue(gain.getMinimum());
        } else {
            float dB = 20f * (float) Math.log10(volumePercent / 100.0);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
        }
    }

    public static JPanel wrap(JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.add(c);
        return p;
    }
}
