error id: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/SimulationScreen.java
file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/SimulationScreen.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[2,13]

error in qdox parser
file content:
```java
offset: 87
uri: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/SimulationScreen.java
text:
```scala
    // Returns the timer delay in milliseconds based on speedSlider value
    private i@@nt getTimerDelay() {
        int speed = speedSlider != null ? speedSlider.getValue() : 5;
        // Example: speed 1 = 60ms, speed 10 = 10ms
        return 60 - (speed - 1) * 5;
    }
package com.cpuScheduler;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.cpuScheduler.schedulingAlgorithms.FCFS;
import com.cpuScheduler.schedulingAlgorithms.PriorityNonPreemptive;
import com.cpuScheduler.schedulingAlgorithms.PriorityPreemptive;
import com.cpuScheduler.schedulingAlgorithms.RoundRobin;
import com.cpuScheduler.schedulingAlgorithms.SJFNonPreemptive;
import com.cpuScheduler.schedulingAlgorithms.SJFPreemptive;

public class SimulationScreen extends JPanel {

    private static final Color[] PROCESS_COLORS = {
        new Color(152,  37, 152),
        new Color( 37, 130, 200),
        new Color( 37, 180,  80),
        new Color(200, 120,  37),
        new Color(200,  37,  80),
        new Color( 80,  37, 200),
        new Color( 37, 180, 180),
        new Color(180, 180,  37),
        new Color(200,  80, 140),
        new Color(100, 200,  80),
        new Color( 80, 140, 200),
        new Color(200, 160,  80),
        new Color(140,  80, 200),
        new Color( 80, 200, 160),
        new Color(200,  80,  80),
        new Color( 80, 200,  80),
        new Color(160,  80, 140),
        new Color(200, 140,  37),
        new Color( 37, 100, 200),
        new Color(200,  37, 140),
    };

    private ScheduleResult result;
    private Map<String, Color> pidColor = new LinkedHashMap<>();

    private float currentTimerTick = 0f;
    private int maxTime = 0;
    private javax.swing.Timer animTimer;
    private boolean running = false;

    private GanttPanel ganttPanel;
    private JLabel timerLabel;
    private JButton playPauseBtn;
    private JSlider speedSlider;

    public SimulationScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

        // Run the algorithm
        result = runAlgorithm(frame);
        assignColors(frame.getProcesses());
        maxTime = result.gantt.isEmpty() ? 0
            : result.gantt.get(result.gantt.size() - 1).endTime;

        // ---- HEADER ----
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel(frame.getSelectedAlgorithm() + " Simulation", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(UIUtils.createNavButton("← Back", e -> {
            if (animTimer != null) animTimer.stop();
            frame.showScreen("MANUAL_INPUT");
        }), BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ---- CENTER ----
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Timer display
        JPanel timerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        timerRow.setOpaque(false);
        JLabel timerTitle = new JLabel("TIMER:");
        timerTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
        timerTitle.setForeground(MainFrame.LIGHT_TEXT);
        timerLabel = new JLabel("000");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.PURPLE_BTN, 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)));
        timerRow.add(timerTitle);
        timerRow.add(timerLabel);
        center.add(timerRow);
        center.add(Box.createRigidArea(new Dimension(0, 6)));

        // Gantt Chart
        ganttPanel = new GanttPanel();
        JScrollPane ganttScroll = new JScrollPane(ganttPanel,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScroll.setBackground(MainFrame.BG_COLOR);
        ganttScroll.getViewport().setBackground(MainFrame.BG_COLOR);
        ganttScroll.setBorder(BorderFactory.createLineBorder(MainFrame.PURPLE_BTN, 1));
        ganttScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        ganttScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 130));
        center.add(ganttScroll);
        center.add(Box.createRigidArea(new Dimension(0, 12)));

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 2));
        controls.setOpaque(false);
        playPauseBtn = UIUtils.createStyledButton("▶ Play");
        JButton resetBtn = UIUtils.createStyledButton("⟳ Reset");
        playPauseBtn.setPreferredSize(new Dimension(120, 36));
        resetBtn.setPreferredSize(new Dimension(120, 36));

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
        center.add(controls);
        center.add(Box.createRigidArea(new Dimension(0, 10)));

        // Statistics Table
        String[] cols = {"PID", "Burst", "Arrival", "Priority", "Waiting Time", "Turnaround Time"};
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
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        center.add(tableScroll);
        center.add(Box.createRigidArea(new Dimension(0, 8)));

        // Averages
        double[] avgs = computeAverages();
        JLabel avgLabel = new JLabel(String.format(
            "Average Waiting Time: %.2f  |  Average Turnaround Time: %.2f", avgs[0], avgs[1]));
        avgLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        avgLabel.setForeground(MainFrame.LIGHT_TEXT);
        avgLabel.setAlignmentX(CENTER_ALIGNMENT);
        center.add(avgLabel);

        add(center, BorderLayout.CENTER);

        // Setup animation timer (default 200ms per tick)
        setupTimer();
    }

    // -------- Algorithm dispatch --------
    private ScheduleResult runAlgorithm(MainFrame frame) {
        List<Process> procs = frame.getProcesses();
        String algo = frame.getSelectedAlgorithm();
        switch (algo) {
            case "Round Robin":
                return RoundRobin.run(procs, frame.getTimeQuantum());
            case "SJF (Non-Preemptive)":
                return SJFNonPreemptive.run(procs);
            case "SJF (Preemptive)":
                return SJFPreemptive.run(procs);
            case "Priority (Non-Preemptive)":
                return PriorityNonPreemptive.run(procs, frame.isHigherPriorityFirst());
            case "Priority (Preemptive)":
                return PriorityPreemptive.run(procs, frame.isHigherPriorityFirst());
            default: // FCFS
                return FCFS.run(procs);
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
        Object[][] rows = new Object[procs.size()][6];
        for (int i = 0; i < procs.size(); i++) {
            Process p = procs.get(i);
            rows[i] = new Object[]{p.pid, p.burstTime, p.arrivalTime, p.priority, p.waitingTime, p.turnaroundTime};
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
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(26);
        t.setSelectionBackground(MainFrame.PURPLE_BTN);
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(80, 40, 100));
        t.getTableHeader().setBackground(new Color(60, 20, 80));
        t.getTableHeader().setForeground(MainFrame.LIGHT_TEXT);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    // -------- Animation --------
    private void setupTimer() {
        animTimer = new javax.swing.Timer(getTimerDelay(), e -> { // ~60fps
            if (currentTimerTick < maxTime) {
                currentTimerTick = Math.min(maxTime, currentTimerTick + getStep());
                timerLabel.setText(String.format("%03d", (int) currentTimerTick));
                ganttPanel.repaint();
            } else {
                animTimer.stop();
                running = false;
                playPauseBtn.setText("▶ Play");
            }
        });
    }

    private float getStep() {
        // speed 1 = 0.01 units/frame (very slow), speed 10 = 0.25 units/frame (fast)
        int speed = speedSlider != null ? speedSlider.getValue() : 5;
        return 0.01f + (speed - 1) * 0.027f;
    }

    private void togglePlay() {
        if (running) {
            animTimer.stop();
            running = false;
            playPauseBtn.setText("▶ Play");
        } else {
            if (currentTimerTick >= maxTime) resetSimulation();
            animTimer.setDelay(getTimerDelay());
            animTimer.start();
            running = true;
            playPauseBtn.setText("⏸ Pause");
        }
    }

    private void resetSimulation() {
        animTimer.stop();
        running = false;
        playPauseBtn.setText("▶ Play");
        currentTimerTick = 0f;   // was 0
        timerLabel.setText("000");
        ganttPanel.repaint();
    }

    // -------- Gantt Chart Panel --------
    class GanttPanel extends JPanel {
        private static final int BAR_H   = 44;
        private static final int BAR_Y   = 20;
        private static final int LABEL_Y = BAR_Y + BAR_H + 16;
        private static final int H       = LABEL_Y + 16;
        private static final int H_PAD   = 10; // horizontal padding each side

        GanttPanel() {
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(0, H + 10); // width = 0 so it stretches to parent
        }

        private float getScale() {
            int usableWidth = getWidth() - H_PAD * 2;
            if (maxTime <= 0 || usableWidth <= 0) return 1f;
            return (float) usableWidth / maxTime;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            float scale = getScale();

            // Background
            g2.setColor(new Color(25, 10, 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            for (GanttEntry entry : result.gantt) {
                if (entry.startTime >= currentTimerTick) break;  // float comparison

                float visibleEnd = Math.min(entry.endTime, currentTimerTick); // float
                int x      = H_PAD + Math.round(entry.startTime * scale);
                int fullW  = Math.round((entry.endTime  - entry.startTime) * scale);
                int visW   = Math.round((visibleEnd - entry.startTime) * scale); // fractional!

                Color c = pidColor.getOrDefault(entry.pid, Color.GRAY);

                // Animated fill
                g2.setColor(c);
                g2.fillRect(x, BAR_Y, visW, BAR_H);

                // Ghost (upcoming portion)
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
                g2.fillRect(x + visW, BAR_Y, fullW - visW, BAR_H);

                // Border
                g2.setColor(c.darker());
                g2.drawRect(x, BAR_Y, fullW, BAR_H);

                // PID label (only when block is fully revealed)
                if (visibleEnd >= entry.endTime) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    if (fm.stringWidth(entry.pid) < fullW - 4) {
                        g2.drawString(entry.pid,
                            x + (fullW - fm.stringWidth(entry.pid)) / 2,
                            BAR_Y + BAR_H / 2 + 5);
                    }
                }

                // Time label at start of segment
                g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
                g2.setColor(new Color(200, 180, 210));
                g2.drawString(String.valueOf(entry.startTime), x, LABEL_Y);
            }

            // End time label
            if (currentTimerTick >= maxTime && !result.gantt.isEmpty()) {
                g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
                g2.setColor(new Color(200, 180, 210));
                int endX = H_PAD + Math.round(maxTime * scale);
                g2.drawString(String.valueOf(maxTime), endX, LABEL_Y);
            }

            // Smooth cursor line — moves continuously
            int cursorX = H_PAD + Math.round(currentTimerTick * scale);
            g2.setColor(new Color(255, 220, 255, 200));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1f, new float[]{4, 3}, 0));
            g2.drawLine(cursorX, BAR_Y - 4, cursorX, BAR_Y + BAR_H + 4);

            g2.dispose();
        }
    }
}

```

```



#### Error stacktrace:

```
com.thoughtworks.qdox.parser.impl.Parser.yyerror(Parser.java:2025)
	com.thoughtworks.qdox.parser.impl.Parser.yyparse(Parser.java:2147)
	com.thoughtworks.qdox.parser.impl.Parser.parse(Parser.java:2006)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:232)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:190)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:94)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:89)
	com.thoughtworks.qdox.library.SortedClassLibraryBuilder.addSource(SortedClassLibraryBuilder.java:162)
	com.thoughtworks.qdox.JavaProjectBuilder.addSource(JavaProjectBuilder.java:174)
	scala.meta.internal.mtags.JavaMtags.indexRoot(JavaMtags.scala:49)
	scala.meta.internal.metals.SemanticdbDefinition$.foreachWithReturnMtags(SemanticdbDefinition.scala:99)
	scala.meta.internal.metals.Indexer.indexSourceFile(Indexer.scala:560)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3(Indexer.scala:691)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3$adapted(Indexer.scala:688)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:630)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:628)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1313)
	scala.meta.internal.metals.Indexer.reindexWorkspaceSources(Indexer.scala:688)
	scala.meta.internal.metals.MetalsLspService.$anonfun$onChange$2(MetalsLspService.scala:940)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:691)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:500)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1583)
```
#### Short summary: 

QDox parse error in file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/SimulationScreen.java