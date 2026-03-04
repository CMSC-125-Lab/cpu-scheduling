package com.cpuScheduler;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class InputMethodScreen extends JPanel {

    public InputMethodScreen(MainFrame frame) {
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        add(UIUtils.createNavButton("← Back", e -> frame.showScreen("WELCOME")), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Select Input Method");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(MainFrame.LIGHT_TEXT);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("How would you like to provide process data?");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(new Color(180, 120, 180));
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JButton manual = UIUtils.createStyledButton("✎  Manual Input");
        JButton file   = UIUtils.createStyledButton("📂  Input from File");
        JButton random = UIUtils.createStyledButton("⚄  Random Input");
        manual.setPreferredSize(new Dimension(260, 48));
        file.setPreferredSize(new Dimension(260, 48));
        random.setPreferredSize(new Dimension(260, 48));

        // ---- Manual ----
        manual.addActionListener(e -> frame.showScreen("MANUAL_INPUT"));

        // ---- From File ----
        file.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open Process Data File (.csv or .txt)");
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                java.io.File f = chooser.getSelectedFile();
                List<Process> loaded = FileInputUtil.loadFromFile(f, frame);
                if (loaded != null && !loaded.isEmpty()) {
                    frame.setProcesses(loaded);
                    frame.showScreen("MANUAL_INPUT");
                }
            }
        });

        // ---- Random ----
        random.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame,
                "Number of processes to generate (3–20):",
                "Random Generation", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return;

            int count;
            try {
                count = Integer.parseInt(input.trim());
                if (count < 3 || count > 20) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Please enter a number between 3 and 20.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate random processes
            frame.setProcesses(generateRandomProcesses(count));

            // Randomize algorithm + its settings
            randomizeAlgorithm(frame);

            // Show a summary dialog so the user knows what was generated
            showRandomSummary(frame);

            frame.showScreen("MANUAL_INPUT");
        });

        center.add(Box.createVerticalGlue());
        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(sub);
        center.add(Box.createRigidArea(new Dimension(0, 40)));
        center.add(UIUtils.wrap(manual));
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(UIUtils.wrap(file));
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(UIUtils.wrap(random));
        center.add(Box.createVerticalGlue());

        add(center, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------
    // Generate random processes
    // ---------------------------------------------------------------
    private List<Process> generateRandomProcesses(int count) {
        Random rnd = new Random();

        // Unique priorities drawn from 1–20
        List<Integer> priorities = new ArrayList<>();
        for (int i = 1; i <= 20; i++) priorities.add(i);
        Collections.shuffle(priorities, rnd);

        List<Process> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String pid   = "P" + (i + 1);
            int burst    = rnd.nextInt(30) + 1;   // 1–30
            int arrival  = rnd.nextInt(31);         // 0–30
            int priority = priorities.get(i);       // unique 1–20
            list.add(new Process(pid, burst, arrival, priority));
        }
        return list;
    }

    // ---------------------------------------------------------------
    // Randomly pick an algorithm and configure its settings in frame
    // ---------------------------------------------------------------
    private void randomizeAlgorithm(MainFrame frame) {
        String[] algos = {
            "FCFS",
            "Round Robin",
            "SJF (Non-Preemptive)",
            "SJF (Preemptive)",
            "Priority (Non-Preemptive)",
            "Priority (Preemptive)"
        };

        Random rnd = new Random();
        String chosen = algos[rnd.nextInt(algos.length)];
        frame.setSelectedAlgorithm(chosen);

        // Random quantum for Round Robin (1–10)
        if (chosen.contains("Round")) {
            frame.setTimeQuantum(rnd.nextInt(10) + 1);
        }

        // Random priority order for Priority algorithms
        if (chosen.contains("Priority")) {
            frame.setHigherPriorityFirst(rnd.nextBoolean());
        }
    }

    // ---------------------------------------------------------------
    // Show a brief summary of what was randomly generated
    // ---------------------------------------------------------------
    private void showRandomSummary(MainFrame frame) {
        String algo = frame.getSelectedAlgorithm();

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:sans-serif; font-size:12px; color:#222;'>");
        sb.append("<b>Random generation complete!</b><br><br>");
        sb.append("Processes generated: <b>").append(frame.getProcesses().size()).append("</b><br>");
        sb.append("Algorithm selected: <b>").append(algo).append("</b><br>");

        if (algo.contains("Round")) {
            sb.append("Time Quantum: <b>").append(frame.getTimeQuantum()).append("</b><br>");
        }
        if (algo.contains("Priority")) {
            sb.append("Priority order: <b>")
              .append(frame.isHigherPriorityFirst()
                  ? "Higher number = Higher priority"
                  : "Lower number = Higher priority")
              .append("</b><br>");
        }

        sb.append("<br><i>You can review and edit everything before running.</i>");
        sb.append("</body></html>");

        JOptionPane.showMessageDialog(frame, new JLabel(sb.toString()),
            "Random Input Summary", JOptionPane.INFORMATION_MESSAGE);
    }
}