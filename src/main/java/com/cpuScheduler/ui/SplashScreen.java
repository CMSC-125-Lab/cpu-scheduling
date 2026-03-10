package com.cpuScheduler.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.net.URL;

/**
 * A true splash screen — shown as an undecorated JWindow (no title bar,
 * no borders, no OS chrome). Plays splashScreen.wav for ~7 seconds,
 * then calls onFinished.run() on the EDT so MainFrame can appear.
 */
public class SplashScreen extends JWindow {

    private float    alpha      = 0f;
    private float    dotPhase   = 0f;
    private Timer    fadeTimer;
    private Clip     splashClip;
    private Runnable onFinished;

    /**
     * @param onFinished called on the EDT when the splash is done
     */
    public SplashScreen(Runnable onFinished) {
        this.onFinished = onFinished;
        // Undecorated by default for JWindow — no title bar, no borders
        setSize(460, 280);
        setLocationRelativeTo(null); // center on screen
        setBackground(new Color(0, 0, 0, 0)); // transparent window bg
        getRootPane().setOpaque(false);
        getRootPane().setBackground(new Color(0, 0, 0, 0));

        // ---- Content panel ----
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                paintSplash(g);
            }
        };
        panel.setOpaque(false);
        setContentPane(panel);

        // ---- Fade + pulse animation ----
        fadeTimer = new Timer(30, e -> {
            if (alpha < 1f) alpha = Math.min(1f, alpha + 0.04f);
            dotPhase += 0.08f;
            repaint();
        });
    }

    /** Show and start animating. */
    public void showSplash() {
        // Start sound and timer here, after the window is about to become visible,
        // so the close timer countdown and audio are in sync with what the user sees.
        long soundDurationMs = playSplashSound();
        long displayMs = soundDurationMs > 0 ? soundDurationMs : 3000;

        Timer closeTimer = new Timer((int) displayMs, e -> {
            fadeTimer.stop();
            stopSplashSound();
            setVisible(false);
            dispose();
            onFinished.run();
        });
        closeTimer.setRepeats(false);
        closeTimer.start();

        setVisible(true);
        fadeTimer.start();
    }

    // ----------------------------------------------------------------
    //  Painting
    // ----------------------------------------------------------------
    private void paintSplash(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w  = getWidth();
        int h  = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        // Rounded dark background
        g2.setColor(new Color(14, 12, 36, 245));
        g2.fillRoundRect(0, 0, w, h, 24, 24);

        // Purple border
        g2.setColor(MainFrame.PURPLE_BTN);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, w - 2, h - 2, 24, 24);

        // Fade-in everything below
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // CPU icon
        drawCpuIcon(g2, cx, cy - 50, 70);

        // Title
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.setColor(MainFrame.LIGHT_TEXT);
        String title = "CPU Scheduling Simulator";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, cx - fm.stringWidth(title) / 2, cy + 36);

        // "Loading" text
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(180, 100, 180, 180));
        String loading = "Loading";
        FontMetrics fm2 = g2.getFontMetrics();
        int lx = cx - fm2.stringWidth(loading) / 2 - 16;
        g2.drawString(loading, lx, cy + 60);

        // Animated dots
        for (int i = 0; i < 3; i++) {
            float dot = (float)(0.4 + 0.6 * Math.abs(Math.sin(dotPhase - i * 0.7f)));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * dot));
            g2.setColor(MainFrame.PURPLE_BTN);
            g2.fillOval(cx + 24 + i * 13, cy + 52, 7, 7);
        }

        g2.dispose();
    }

    private void drawCpuIcon(Graphics2D g2, int cx, int cy, int size) {
        int s2 = size / 2;

        // Chip body
        g2.setColor(new Color(60, 15, 80));
        g2.fillRoundRect(cx - s2, cy - s2, size, size, 10, 10);
        g2.setColor(MainFrame.PURPLE_BTN);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(cx - s2, cy - s2, size, size, 10, 10);

        // Inner circuit lines
        g2.setColor(new Color(200, 80, 200, 100));
        g2.setStroke(new BasicStroke(1f));
        int inner = s2 - 12;
        g2.drawRoundRect(cx - inner, cy - inner, inner * 2, inner * 2, 5, 5);
        g2.drawLine(cx, cy - inner, cx, cy + inner);
        g2.drawLine(cx - inner, cy, cx + inner, cy);

        // Pins
        g2.setColor(MainFrame.LIGHT_TEXT);
        g2.setStroke(new BasicStroke(1.5f));
        int pinSpacing = size / 4;
        int pinLen = 10;
        for (int i = 1; i <= 3; i++) {
            int off = -s2 + i * pinSpacing;
            g2.drawLine(cx + off, cy - s2, cx + off, cy - s2 - pinLen);
            g2.drawLine(cx + off, cy + s2, cx + off, cy + s2 + pinLen);
            g2.drawLine(cx - s2, cy + off, cx - s2 - pinLen, cy + off);
            g2.drawLine(cx + s2, cy + off, cx + s2 + pinLen, cy + off);
        }

        // Pulsing center glow
        float pulse = (float)(0.3 + 0.4 * Math.abs(Math.sin(dotPhase)));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * pulse));
        g2.setColor(new Color(200, 100, 255));
        g2.fillOval(cx - 7, cy - 7, 14, 14);
    }

    // ----------------------------------------------------------------
    //  Audio
    // ----------------------------------------------------------------

    /**
     * Plays /sounds/splashScreen.wav and returns its duration in ms.
     * Returns 0 if the file is not found or audio fails.
     */
    private long playSplashSound() {
        try {
            URL url = getClass().getResource("/sounds/splashScreen.wav");
            if (url == null) return 0;

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            splashClip = AudioSystem.getClip();
            splashClip.open(ais);
            UIUtils.applyVolume(splashClip, MainFrame.sfxVolume);
            splashClip.start();

            // Return duration in milliseconds
            return splashClip.getMicrosecondLength() / 1000L;

        } catch (Exception e) {
            return 0;
        }
    }

    private void stopSplashSound() {
        if (splashClip != null) {
            if (splashClip.isRunning()) splashClip.stop();
            splashClip.close();
            splashClip = null;
        }
    }
}