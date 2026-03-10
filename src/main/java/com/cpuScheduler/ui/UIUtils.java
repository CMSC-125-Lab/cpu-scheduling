package com.cpuScheduler.ui;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.border.TitledBorder;

public class UIUtils {

    private static final String BASE_FONT_SIZE_KEY = "dynamicBaseFontSize";
    private static final String BASE_TITLE_FONT_SIZE_KEY = "dynamicBaseTitleFontSize";
    private static final String DYNAMIC_LISTENER_KEY = "dynamicFontListenerInstalled";
    private static final int DESIGN_WIDTH = 1050;
    private static final int DESIGN_HEIGHT = 680;

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
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static int scaleSize(JFrame frame, int baseSize) {
        float factor = frameScaleFactor(frame);
        return Math.max(8, Math.round(baseSize * factor));
    }

    public static float frameScaleFactor(JFrame frame) {
        if (frame == null) return 1.0f;
        float widthFactor = frame.getWidth() / (float) DESIGN_WIDTH;
        float heightFactor = frame.getHeight() / (float) DESIGN_HEIGHT;
        float factor = Math.min(widthFactor, heightFactor);
        return Math.max(0.75f, Math.min(1.8f, factor));
    }

    public static void installDynamicFontScaling(MainFrame frame) {
        if (Boolean.TRUE.equals(frame.getRootPane().getClientProperty(DYNAMIC_LISTENER_KEY))) return;

        frame.getRootPane().putClientProperty(DYNAMIC_LISTENER_KEY, Boolean.TRUE);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyDynamicFonts(frame);
            }
        });

        SwingUtilities.invokeLater(() -> applyDynamicFonts(frame));
    }

    public static void applyDynamicFonts(MainFrame frame) {
        if (frame == null) return;
        applyDynamicFontsRecursive(frame.getContentPane(), frame);
        frame.revalidate();
        frame.repaint();
    }

    private static void applyDynamicFontsRecursive(Component component, MainFrame frame) {
        if (component instanceof JComponent jc) {
            Font currentFont = jc.getFont();
            if (currentFont != null) {
                Float baseSize = (Float) jc.getClientProperty(BASE_FONT_SIZE_KEY);
                if (baseSize == null) {
                    baseSize = currentFont.getSize2D();
                    jc.putClientProperty(BASE_FONT_SIZE_KEY, baseSize);
                }
                float scaledSize = Math.max(8f, baseSize * frameScaleFactor(frame));
                if (Math.abs(currentFont.getSize2D() - scaledSize) > 0.2f) {
                    jc.setFont(currentFont.deriveFont(scaledSize));
                }
            }

            if (jc.getBorder() instanceof TitledBorder titledBorder) {
                Font titleFont = titledBorder.getTitleFont();
                if (titleFont == null) titleFont = UIManager.getFont("TitledBorder.font");
                if (titleFont != null) {
                    Float baseTitleSize = (Float) jc.getClientProperty(BASE_TITLE_FONT_SIZE_KEY);
                    if (baseTitleSize == null) {
                        baseTitleSize = titleFont.getSize2D();
                        jc.putClientProperty(BASE_TITLE_FONT_SIZE_KEY, baseTitleSize);
                    }
                    float scaledTitleSize = Math.max(8f, baseTitleSize * frameScaleFactor(frame));
                    titledBorder.setTitleFont(titleFont.deriveFont(scaledTitleSize));
                }
            }

            if (jc instanceof JTable table) {
                Integer baseRowHeight = (Integer) jc.getClientProperty("dynamicBaseRowHeight");
                if (baseRowHeight == null) {
                    baseRowHeight = table.getRowHeight();
                    jc.putClientProperty("dynamicBaseRowHeight", baseRowHeight);
                }
                table.setRowHeight(Math.max(18, Math.round(baseRowHeight * frameScaleFactor(frame))));
            }
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyDynamicFontsRecursive(child, frame);
            }
        }
    }

    public static JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setForeground(MainFrame.LIGHT_TEXT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
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
