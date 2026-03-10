package com.cpuScheduler.ui;

import com.cpuScheduler.util.AudioStretch;

import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class SettingsScreen extends JPanel {

    private Clip testClip;

    public SettingsScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        header.add(UIUtils.createNavButton("← Back", e -> {
            stopTestClip();
            frame.showScreen("WELCOME");
        }), BorderLayout.WEST);

        JLabel title = new JLabel("Settings", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(MainFrame.LIGHT_TEXT);
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ---- Center: card vertically centered ----
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(buildSoundCard());

        add(center, BorderLayout.CENTER);
    }

    // ----------------------------------------------------------------
    //  Sound card
    // ----------------------------------------------------------------
    private JPanel buildSoundCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainFrame.ACCENT_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.PURPLE_BTN.darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(460, 220));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx   = 0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Section label
        JLabel sectionLabel = new JLabel("SOUND EFFECTS");
        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        sectionLabel.setForeground(new Color(160, 90, 160));
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(sectionLabel, gbc);

        // "SFX Volume" label + percentage counter
        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);
        labelRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel volLabel = new JLabel("SFX Volume");
        volLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        volLabel.setForeground(Color.WHITE);

        JLabel pctLabel = new JLabel(MainFrame.sfxVolume + "%");
        pctLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        pctLabel.setForeground(MainFrame.LIGHT_TEXT);

        labelRow.add(volLabel, BorderLayout.WEST);
        labelRow.add(pctLabel, BorderLayout.EAST);
        gbc.gridy  = 1;
        card.add(labelRow, gbc);

        // Slider
        JSlider slider = new JSlider(0, 100, MainFrame.sfxVolume);
        slider.setOpaque(false);
        slider.setForeground(MainFrame.LIGHT_TEXT);
        slider.setMajorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(false);
        slider.setFocusable(true);
        slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        // Level hint label below slider (MUTED / LOW / MEDIUM / HIGH)
        JLabel levelLabel = new JLabel(levelText(MainFrame.sfxVolume));
        levelLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        levelLabel.setForeground(levelColor(MainFrame.sfxVolume));
        levelLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 16, 0));

        slider.addChangeListener((ChangeEvent e) -> {
            int vol = slider.getValue();
            MainFrame.sfxVolume = vol;
            pctLabel.setText(vol + "%");
            levelLabel.setText(levelText(vol));
            levelLabel.setForeground(levelColor(vol));
        });

        gbc.gridy  = 2;
        card.add(slider, gbc);

        gbc.gridy  = 3;
        card.add(levelLabel, gbc);

        // Test SFX button
        JButton testBtn = UIUtils.createStyledButton("Test SFX");
        testBtn.setPreferredSize(new Dimension(140, 38));
        testBtn.setToolTipText("Play a preview clip at the current volume");
        testBtn.addActionListener(e -> playTestClip(testBtn));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(testBtn);
        gbc.gridy  = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, gbc);

        return card;
    }

    // ----------------------------------------------------------------
    //  Helpers
    // ----------------------------------------------------------------
    private String levelText(int vol) {
        if (vol == 0)   return "MUTED";
        if (vol <= 33)  return "LOW";
        if (vol <= 66)  return "MEDIUM";
        return "HIGH";
    }

    private Color levelColor(int vol) {
        if (vol == 0)   return new Color(180, 80, 80);
        if (vol <= 33)  return new Color(180, 140, 80);
        if (vol <= 66)  return new Color(160, 160, 80);
        return new Color(80, 180, 100);
    }

    // ----------------------------------------------------------------
    //  Test audio
    // ----------------------------------------------------------------
    private void playTestClip(JButton testBtn) {
        stopTestClip();
        Clip clip = AudioStretch.createStretched("/sounds/progressBar.wav", 2.0);
        if (clip == null) return;

        UIUtils.applyVolume(clip, MainFrame.sfxVolume);
        testClip = clip;

        testBtn.setEnabled(false);
        testBtn.setText("Playing...");

        clip.addLineListener(event -> {
            if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                SwingUtilities.invokeLater(() -> {
                    testBtn.setEnabled(true);
                    testBtn.setText("Test SFX");
                    stopTestClip();
                });
            }
        });

        clip.start();
    }

    private void stopTestClip() {
        if (testClip != null) {
            if (testClip.isRunning()) testClip.stop();
            testClip.close();
            testClip = null;
        }
    }
}
