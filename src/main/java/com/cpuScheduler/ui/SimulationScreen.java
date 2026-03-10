package com.cpuScheduler.ui;

import com.cpuScheduler.algorithm.FCFS;
import com.cpuScheduler.algorithm.PriorityNonPreemptive;
import com.cpuScheduler.algorithm.PriorityPreemptive;
import com.cpuScheduler.algorithm.RoundRobin;
import com.cpuScheduler.algorithm.SJFNonPreemptive;
import com.cpuScheduler.algorithm.SJFPreemptive;
import com.cpuScheduler.model.GanttEntry;
import com.cpuScheduler.model.Process;
import com.cpuScheduler.model.ScheduleResult;
import com.cpuScheduler.util.AudioStretch;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class SimulationScreen extends JPanel {

    private static final int GANTT_HEIGHT = 100;

    private static final Color[] PROCESS_COLORS = {
        new Color(152,  37, 152), new Color( 37, 130, 200),
        new Color( 37, 180,  80), new Color(200, 120,  37),
        new Color(200,  37,  80), new Color( 80,  37, 200),
        new Color( 37, 180, 180), new Color(180, 180,  37),
        new Color(200,  80, 140), new Color(100, 200,  80),
        new Color( 80, 140, 200), new Color(200, 160,  80),
        new Color(140,  80, 200), new Color( 80, 200, 160),
        new Color(200,  80,  80), new Color( 80, 200,  80),
        new Color(160,  80, 140), new Color(200, 140,  37),
        new Color( 37, 100, 200), new Color(200,  37, 140),
    };

    private ScheduleResult result;
    private Map<String, Color> pidColor = new LinkedHashMap<>();

    private float currentTimerTick = 0f;
    private float prevTimerTick    = 0f;
    private int   maxTime          = 0;
    private javax.swing.Timer animTimer;
    private boolean running = false;

    private GanttPanel ganttPanel;
    private JLabel     timerLabel;
    private JButton    playPauseBtn;
    private JSlider    speedSlider;
    private final MainFrame frame;

    // ---- Audio ----
    // Map from gantt entry index → pre-built stretched Clip (null for IDLE)
    private final Map<Integer, Clip> prebuiltClips = new HashMap<>();
    private int lastEntryIndex = -1;

    public SimulationScreen(MainFrame frame) {
        this.frame = frame;
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

        result = runAlgorithm(frame);
        assignColors(frame.getProcesses());
        maxTime = result.gantt.isEmpty() ? 0
            : result.gantt.get(result.gantt.size() - 1).endTime;

        // ---- HEADER ----
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel(frame.getSelectedAlgorithm() + " Simulation", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        header.add(UIUtils.createNavButton("← Back", e -> {
            stopCurrentClip();
            disposeAllClips();
            if (animTimer != null) animTimer.stop();
            frame.showScreen("MANUAL_INPUT");
        }), BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ---- CONTENT ----
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx   = 0;
        gbc.weightx = 1.0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // Row 0: Timer
        JPanel timerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        timerRow.setOpaque(false);

        JLabel timerTitle = new JLabel("TIMER:");
        timerTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
        timerTitle.setForeground(MainFrame.LIGHT_TEXT);

        timerLabel = new JLabel("000 ms");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.PURPLE_BTN, 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)));

        timerRow.add(timerTitle);
        timerRow.add(timerLabel);

        gbc.gridy = 0; gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        content.add(timerRow, gbc);

        // Row 1: Gantt
        ganttPanel = new GanttPanel();
        
        // Wrap in scroll pane for horizontal scrolling
        JScrollPane ganttScrollPane = new JScrollPane(ganttPanel);
        ganttScrollPane.setOpaque(false);
        ganttScrollPane.getViewport().setOpaque(false);
        ganttScrollPane.getViewport().setBackground(new Color(25, 10, 40));
        ganttScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        ganttScrollPane.setBorder(BorderFactory.createLineBorder(MainFrame.PURPLE_BTN, 1));
        ganttScrollPane.setPreferredSize(new Dimension(10, GANTT_HEIGHT + 20)); // Extra for scrollbar
        ganttScrollPane.setMinimumSize(new Dimension(10, GANTT_HEIGHT + 20));
        ganttScrollPane.getHorizontalScrollBar().setBackground(new Color(30, 15, 50));
        ganttScrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MainFrame.PURPLE_BTN;
                this.trackColor = new Color(30, 15, 50);
            }
        });

        gbc.gridy = 1; gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        content.add(ganttScrollPane, gbc);

        // Row 2: Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 15));
        controls.setOpaque(false);

        playPauseBtn = UIUtils.createStyledButton("▶ Play");
        JButton resetBtn = UIUtils.createStyledButton("⟳ Reset");
        playPauseBtn.setPreferredSize(new Dimension(180, 45));
        resetBtn.setPreferredSize(new Dimension(180, 45));

        JLabel speedLbl = new JLabel("Speed:");
        speedLbl.setForeground(MainFrame.LIGHT_TEXT);
        speedSlider = new JSlider(1, 10, 5);
        speedSlider.setOpaque(false);
        speedSlider.setForeground(MainFrame.LIGHT_TEXT);
        speedSlider.setPreferredSize(new Dimension(140, 30));

        playPauseBtn.addActionListener(e -> togglePlay());
        resetBtn.addActionListener(e -> resetSimulation());

        controls.add(playPauseBtn);
        controls.add(resetBtn);
        controls.add(speedLbl);
        controls.add(speedSlider);

        gbc.gridy = 2; gbc.weighty = 0;
        gbc.insets = new Insets(10, 0, 20, 0);
        content.add(controls, gbc);

        // Row 3: Stats table
        String[] cols = {"PID", "Burst (ms)", "Arrival (ms)", "Priority", "WT (ms)", "TAT (ms)", "Avg WT (ms)", "Avg TAT (ms)"};
        Object[][] rows = buildTableData();
        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable statsTable = new JTable(model);
        styleTable(statsTable);

        JScrollPane tableScroll = new JScrollPane(statsTable);
        tableScroll.setBackground(MainFrame.BG_COLOR);
        tableScroll.getViewport().setBackground(new Color(30, 15, 50));
        tableScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MainFrame.PURPLE_BTN),
            "Process Statistics",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            MainFrame.LIGHT_TEXT));

        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(tableScroll, gbc);

        add(content, BorderLayout.CENTER);

        // Pre-build all clips BEFORE the timer starts, on a background thread.
        // Show the Play button only once clips are ready.
        playPauseBtn.setEnabled(false);
        playPauseBtn.setText("Loading…");
        new Thread(this::prebuildAllClips, "audio-prebuild").start();
    }

    // ================================================================
    //  Audio — pre-build all clips at construction time
    // ================================================================

    /**
     * Build one stretched Clip per non-IDLE gantt entry and store them
     * in prebuiltClips keyed by gantt index.
     * Called once on a background thread so the UI stays responsive.
     * Enables the Play button when done.
     */
    private void prebuildAllClips() {
        for (int i = 0; i < result.gantt.size(); i++) {
            GanttEntry entry = result.gantt.get(i);
            if ("IDLE".equals(entry.pid)) continue;

            int    blockUnits = entry.endTime - entry.startTime;
            double targetSec  = blockToRealMs(blockUnits) / 1000.0;

            Clip clip = AudioStretch.createStretched("/sounds/progressBar.wav", targetSec);
            if (clip != null) {
                prebuiltClips.put(i, clip);
            }
        }

        // Back on EDT: enable Play button
        javax.swing.SwingUtilities.invokeLater(() -> {
            playPauseBtn.setEnabled(true);
            playPauseBtn.setText("▶ Play");
            setupTimer(); // safe to set up now that clips are ready
        });
    }

    /**
     * How many real milliseconds does a block of `blockUnits` take to animate
     * at the current speed slider setting?
     */
    private double blockToRealMs(int blockUnits) {
        float unitsPerFrame = getStep();       // units advanced per 16 ms frame
        float framesNeeded  = blockUnits / unitsPerFrame;
        return framesNeeded * 16.0;
    }

    /** Stop whatever clip is currently playing (don't dispose it — we reuse it on reset). */
    private void stopCurrentClip() {
        if (lastEntryIndex >= 0) {
            Clip c = prebuiltClips.get(lastEntryIndex);
            if (c != null && c.isRunning()) c.stop();
        }
        lastEntryIndex = -1;
    }

    /** Close and release all pre-built clips (called on Back / screen teardown). */
    private void disposeAllClips() {
        for (Clip c : prebuiltClips.values()) {
            try { c.close(); } catch (Exception ignored) {}
        }
        prebuiltClips.clear();
    }

    /**
     * Called every animation frame.
     * Detects entry into a new gantt block and starts its pre-built clip.
     */
    private void updateAudio() {
        // Find which gantt entry the cursor is inside right now
        int currentEntry = -1;
        for (int i = 0; i < result.gantt.size(); i++) {
            GanttEntry e = result.gantt.get(i);
            if (prevTimerTick < e.endTime && currentTimerTick >= e.startTime) {
                currentEntry = i;
                break;
            }
        }

        // No active entry or IDLE → stop sound
        if (currentEntry == -1 || "IDLE".equals(result.gantt.get(currentEntry).pid)) {
            if (lastEntryIndex != -1) stopCurrentClip();
            return;
        }

        // Same block as last frame → already playing, nothing to do
        if (currentEntry == lastEntryIndex) return;

        // New block → stop old clip, start new pre-built clip immediately
        stopCurrentClip();
        lastEntryIndex = currentEntry;

        Clip clip = prebuiltClips.get(currentEntry);
        if (clip != null) {
            UIUtils.applyVolume(clip, MainFrame.sfxVolume);
            clip.setFramePosition(0); // rewind in case of reset/replay
            clip.start();
        }
    }

    // ================================================================
    //  Algorithm dispatch
    // ================================================================
    private ScheduleResult runAlgorithm(MainFrame frame) {
        List<Process> procs = frame.getProcesses();
        String algo = frame.getSelectedAlgorithm();
        switch (algo) {
            case "Round Robin":               return RoundRobin.run(procs, frame.getTimeQuantum());
            case "SJF (Non-Preemptive)":      return SJFNonPreemptive.run(procs);
            case "SJF (Preemptive)":          return SJFPreemptive.run(procs);
            case "Priority (Non-Preemptive)": return PriorityNonPreemptive.run(procs, frame.isHigherPriorityFirst());
            case "Priority (Preemptive)":     return PriorityPreemptive.run(procs, frame.isHigherPriorityFirst());
            default:                          return FCFS.run(procs);
        }
    }

    private void assignColors(List<Process> procs) {
        int idx = 0;
        for (Process p : procs) {
            pidColor.put(p.pid, PROCESS_COLORS[idx % PROCESS_COLORS.length]);
            idx++;
        }
        pidColor.put("IDLE", new Color(60, 60, 80));
    }

    private Object[][] buildTableData() {
        List<Process> procs = result.processes;
        double[] avgs = computeAverages();
        Object[][] rows = new Object[procs.size()][8];
        for (int i = 0; i < procs.size(); i++) {
            Process p = procs.get(i);
            rows[i] = new Object[]{p.pid, p.burstTime, p.arrivalTime, p.priority,
                                   p.waitingTime, p.turnaroundTime,
                                   String.format("%.2f", avgs[0]), String.format("%.2f", avgs[1])};
        }
        return rows;
    }

    private double[] computeAverages() {
        List<Process> procs = result.processes;
        double sumWT = 0, sumTAT = 0;
        for (Process p : procs) { sumWT += p.waitingTime; sumTAT += p.turnaroundTime; }
        return new double[]{sumWT / procs.size(), sumTAT / procs.size()};
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(30, 15, 50));
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.PLAIN, 15));
        t.setRowHeight(30);
        t.setSelectionBackground(MainFrame.PURPLE_BTN);
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(80, 40, 100));
        t.getTableHeader().setBackground(new Color(60, 20, 80));
        t.getTableHeader().setForeground(MainFrame.LIGHT_TEXT);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    setBackground(MainFrame.PURPLE_BTN);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(30, 15, 50));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        };
        
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // ================================================================
    //  Animation timer
    // ================================================================
    private void setupTimer() {
        animTimer = new javax.swing.Timer(16, e -> {
            if (currentTimerTick < maxTime) {
                prevTimerTick    = currentTimerTick;
                currentTimerTick = Math.min(maxTime, currentTimerTick + getStep());
                timerLabel.setText(String.format("%03d ms", (int) currentTimerTick));
                ganttPanel.repaint();
                updateAudio();
            } else {
                animTimer.stop();
                running = false;
                playPauseBtn.setText("▶ Play");
                stopCurrentClip();
            }
        });
    }

    private float getStep() {
        int speed = speedSlider != null ? speedSlider.getValue() : 5;
        return 0.01f + (speed - 1) * 0.027f;
    }

    private void togglePlay() {
        if (running) {
            animTimer.stop();
            running = false;
            playPauseBtn.setText("▶ Play");
            stopCurrentClip();
        } else {
            if (currentTimerTick >= maxTime) resetSimulation();
            animTimer.setDelay(16);
            animTimer.start();
            running = true;
            playPauseBtn.setText("⏸ Pause");
        }
    }

    private void resetSimulation() {
        if (animTimer != null) animTimer.stop();
        stopCurrentClip();
        running = false;
        playPauseBtn.setText("▶ Play");
        currentTimerTick = 0f;
        prevTimerTick    = 0f;
        lastEntryIndex   = -1;
        timerLabel.setText("000 ms");
        ganttPanel.repaint();
    }

    // ================================================================
    //  Gantt Chart Panel
    // ================================================================
    class GanttPanel extends JPanel {
        private static final int BAR_Y   = 12;
        private static final int BAR_H   = 44;
        private static final int LABEL_Y = BAR_Y + BAR_H + 18;
        private static final int H_PAD   = 10;
        
        // Minimum pixels per millisecond to ensure labels are readable
        private static final float MIN_SCALE = 30.0f;

        GanttPanel() {
            setOpaque(false);
            updatePreferredSize();
        }
        
        private void updatePreferredSize() {
            // Calculate width needed with minimum scale
            int neededWidth = H_PAD * 2 + Math.round(maxTime * MIN_SCALE);
            setPreferredSize(new Dimension(neededWidth, GANTT_HEIGHT));
            revalidate();
        }

        private float getScale() {
            if (maxTime <= 0) return MIN_SCALE;
            
            int usableWidth = getWidth() - H_PAD * 2;
            if (usableWidth <= 0) return MIN_SCALE;
            
            // Use the larger of: minimum scale or fit-to-width scale
            float fitScale = (float) usableWidth / maxTime;
            return Math.max(MIN_SCALE, fitScale);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            float scale = getScale();

            g2.setColor(new Color(25, 10, 40));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Track last time label position to avoid overlaps
            int lastTimeLabelEnd = -100;
            Font timeFont = new Font("Monospaced", Font.PLAIN, UIUtils.scaleSize(frame, 10));
            FontMetrics timeFm = g2.getFontMetrics(timeFont);

            for (GanttEntry entry : result.gantt) {
                if (entry.startTime >= currentTimerTick) break;

                float visibleEnd = Math.min(entry.endTime, currentTimerTick);
                int x     = H_PAD + Math.round(entry.startTime * scale);
                int fullW = Math.round((entry.endTime - entry.startTime) * scale);
                int visW  = Math.round((visibleEnd - entry.startTime) * scale);

                fullW = Math.max(fullW, 1);
                visW  = Math.max(Math.min(visW, fullW), 0);

                Color c = pidColor.getOrDefault(entry.pid, Color.GRAY);

                g2.setColor(c);
                g2.fillRect(x, BAR_Y, visW, BAR_H);

                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 45));
                g2.fillRect(x + visW, BAR_Y, fullW - visW, BAR_H);

                g2.setColor(c.darker());
                g2.drawRect(x, BAR_Y, fullW, BAR_H);

                // Draw PID label if block is fully visible
                if (visibleEnd >= entry.endTime) {
                    g2.setColor(Color.WHITE);
                    
                    // Try different font sizes to fit the label
                    Font pidFont = null;
                    FontMetrics pidFm = null;
                    
                    int[] candidateSizes = {
                        UIUtils.scaleSize(frame, 12),
                        UIUtils.scaleSize(frame, 10),
                        UIUtils.scaleSize(frame, 8)
                    };

                    for (int fontSize : candidateSizes) {
                        pidFont = new Font("SansSerif", Font.BOLD, fontSize);
                        pidFm = g2.getFontMetrics(pidFont);
                        if (pidFm.stringWidth(entry.pid) <= fullW - 4) {
                            break;
                        }
                    }
                    
                    if (pidFm != null && pidFm.stringWidth(entry.pid) <= fullW - 4) {
                        g2.setFont(pidFont);
                        g2.drawString(entry.pid,
                            x + (fullW - pidFm.stringWidth(entry.pid)) / 2,
                            BAR_Y + BAR_H / 2 + pidFm.getHeight() / 3);
                    }
                }

                // Draw time label only if it won't overlap with previous label
                String timeLabel = entry.startTime + "ms";
                int labelWidth = timeFm.stringWidth(timeLabel);
                
                if (x > lastTimeLabelEnd + 5) {
                    g2.setFont(timeFont);
                    g2.setColor(new Color(200, 180, 210));
                    g2.drawString(timeLabel, x, LABEL_Y);
                    lastTimeLabelEnd = x + labelWidth;
                }
            }

            // Draw final time label
            if (currentTimerTick >= maxTime && !result.gantt.isEmpty()) {
                g2.setFont(timeFont);
                g2.setColor(new Color(200, 180, 210));
                int endX = H_PAD + Math.round(maxTime * scale);
                String endLabel = maxTime + "ms";
                int labelWidth = timeFm.stringWidth(endLabel);
                
                // Only draw if it won't overlap with last label
                if (endX > lastTimeLabelEnd + 5) {
                    g2.drawString(endLabel, endX, LABEL_Y);
                }
            }

            // Draw cursor
            int cursorX = H_PAD + Math.round(currentTimerTick * scale);
            g2.setColor(new Color(255, 220, 255, 200));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1f, new float[]{4, 3}, 0));
            g2.drawLine(cursorX, BAR_Y - 4, cursorX, BAR_Y + BAR_H + 4);

            g2.dispose();
        }
    }
}