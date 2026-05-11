package com.cpuScheduler.ui;

import com.cpuScheduler.model.Process;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ManualInputScreen extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel algorithmLabel;
    private MainFrame frame;
    
    // Custom table model with duplicate priority detection
    private class ProcessTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int col) {
            // Column 0 (PID): not editable
            if (col == 0) return false;
            // Column 3 (Priority): only editable for priority algorithms
            if (col == 3) {
                String algo = frame.getSelectedAlgorithm();
                return algo != null && algo.contains("Priority");
            }
            // Columns 1, 2 (Burst, Arrival): always editable
            return true;
        }
        
        @Override
        public void setValueAt(Object aValue, int row, int col) {
            // If setting priority, check for duplicates
            if (col == 3 && aValue != null) {
                try {
                    int newPriority = Integer.parseInt(aValue.toString().trim());
                    String algo = frame.getSelectedAlgorithm();
                    
                    // Only validate duplicates for priority algorithms
                    if (algo != null && algo.contains("Priority")) {
                        for (int i = 0; i < getRowCount(); i++) {
                            if (i != row) {
                                Object existingVal = getValueAt(i, 3);
                                if (existingVal != null) {
                                    try {
                                        int existingPriority = Integer.parseInt(existingVal.toString().trim());
                                        if (existingPriority == newPriority) {
                                            JOptionPane.showMessageDialog(frame,
                                                "Priority " + newPriority + " is already used in row " + (i+1) + ".",
                                                "Duplicate Priority", JOptionPane.WARNING_MESSAGE);
                                            return; // Don't set the value
                                        }
                                    } catch (NumberFormatException ignored) {}
                                }
                            }
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
            super.setValueAt(aValue, row, col);
        }
    }

    public ManualInputScreen(MainFrame frame) {
        this.frame = frame;
        setBackground(MainFrame.BG_COLOR);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // ---- TOP BAR ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel title = new JLabel("Process Input");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(MainFrame.LIGHT_TEXT);
        topBar.add(UIUtils.createNavButton("← Back", e -> frame.showScreen("INPUT_METHOD")), BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);
        add(topBar, BorderLayout.NORTH);

        // ---- TABLE ----
        String[] columns = {"PID", "Burst Time (ms)", "Arrival Time (ms)", "Priority"};
        tableModel = new ProcessTableModel();
        tableModel.setColumnIdentifiers(columns);
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(MainFrame.BG_COLOR);
        scrollPane.getViewport().setBackground(new Color(30, 15, 50));
        scrollPane.setBorder(BorderFactory.createLineBorder(MainFrame.PURPLE_BTN, 1));

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MainFrame.PURPLE_BTN),
            "Process List",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 13),
            MainFrame.LIGHT_TEXT));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        add(tableWrapper, BorderLayout.CENTER);

        // ---- SIDEBAR ----
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        algorithmLabel = new JLabel("Algorithm: " + frame.getSelectedAlgorithm());
        algorithmLabel.setForeground(MainFrame.LIGHT_TEXT);
        algorithmLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        algorithmLabel.setAlignmentX(CENTER_ALIGNMENT);

        JButton addBtn       = UIUtils.createStyledButton("+ Add Process");
        JButton removeBtn    = UIUtils.createStyledButton("− Remove Selected");
        JButton clearBtn     = UIUtils.createStyledButton("Clear All");
        JButton algoBtn      = UIUtils.createStyledButton("Select Algorithm");
        JButton runBtn       = UIUtils.createStyledButton("▶ Run Simulation");

        for (JButton b : new JButton[]{addBtn, removeBtn, clearBtn, algoBtn, runBtn}) {
            b.setPreferredSize(new Dimension(190, 38));
            b.setMaximumSize(new Dimension(190, 38));
            b.setAlignmentX(CENTER_ALIGNMENT);
        }
        runBtn.setBackground(new Color(80, 10, 120));

        addBtn.addActionListener(e -> addProcessRow());
        removeBtn.addActionListener(e -> removeSelectedRow());
        clearBtn.addActionListener(e -> tableModel.setRowCount(0));
        algoBtn.addActionListener(e -> selectAlgorithm());
        runBtn.addActionListener(e -> runSimulation());

        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(algorithmLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));
        sidebar.add(addBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(removeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(clearBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(algoBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(runBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));

        add(sidebar, BorderLayout.EAST);

        // Populate from existing frame data
        refreshTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override
        public void componentShown(java.awt.event.ComponentEvent e) {
            refreshTable();
            algorithmLabel.setText("Algorithm: " + frame.getSelectedAlgorithm());
        }
    });
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Process p : frame.getProcesses()) {
            tableModel.addRow(new Object[]{p.pid, p.burstTime, p.arrivalTime, p.priority});
        }
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(30, 15, 50));
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(28);
        t.setSelectionBackground(MainFrame.PURPLE_BTN);
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(80, 40, 100));
        t.getTableHeader().setBackground(new Color(60, 20, 80));
        t.getTableHeader().setForeground(MainFrame.LIGHT_TEXT);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        
        // Center align all cells with disabled state styling for priority column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                // Check if priority column is disabled
                boolean priorityDisabled = (column == 3) && 
                    (frame.getSelectedAlgorithm() == null || 
                     !frame.getSelectedAlgorithm().contains("Priority"));
                
                // Show empty string for disabled priority column
                Object displayValue = priorityDisabled ? "" : value;
                super.getTableCellRendererComponent(table, displayValue, isSelected, hasFocus, row, column);
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

    private void addProcessRow() {
        int rowCount = tableModel.getRowCount();
        if (rowCount >= 20) {
            JOptionPane.showMessageDialog(frame, "Maximum 20 processes allowed.", "Limit Reached", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String pid = "P" + (rowCount + 1);
        tableModel.addRow(new Object[]{pid, 1, 0, rowCount + 1});
    }

    private void removeSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) tableModel.removeRow(row);
    }

    private void selectAlgorithm() {
        String[] algos = {
            "FCFS",
            "Round Robin",
            "SJF (Non-Preemptive)",
            "SJF (Preemptive)",
            "Priority (Non-Preemptive)",
            "Priority (Preemptive)"
        };

        JComboBox<String> algoBox = new JComboBox<>(algos);
        algoBox.setSelectedItem(frame.getSelectedAlgorithm());

        JSpinner quantumSpinner = new JSpinner(new SpinnerNumberModel(frame.getTimeQuantum(), 1, 10, 1));
        JLabel quantumLabel = new JLabel("Time Quantum (1–10):");
        quantumLabel.setVisible(frame.getSelectedAlgorithm().contains("Round"));

        JRadioButton highFirst = new JRadioButton("Higher number = Higher priority");
        JRadioButton lowFirst  = new JRadioButton("Lower number = Higher priority");
        ButtonGroup bg = new ButtonGroup();
        bg.add(highFirst); bg.add(lowFirst);
        if (frame.isHigherPriorityFirst()) highFirst.setSelected(true);
        else lowFirst.setSelected(true);
        JLabel priorityLabel = new JLabel("Priority Order:");
        boolean isPriority = frame.getSelectedAlgorithm().contains("Priority");
        priorityLabel.setVisible(isPriority);
        highFirst.setVisible(isPriority);
        lowFirst.setVisible(isPriority);

        algoBox.addActionListener(e -> {
            String sel = (String) algoBox.getSelectedItem();
            boolean rr = sel != null && sel.contains("Round");
            boolean pr = sel != null && sel.contains("Priority");
            quantumLabel.setVisible(rr);
            quantumSpinner.setVisible(rr);
            priorityLabel.setVisible(pr);
            highFirst.setVisible(pr);
            lowFirst.setVisible(pr);
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Scheduling Algorithm:"));
        panel.add(algoBox);
        panel.add(quantumLabel);
        panel.add(quantumSpinner);
        panel.add(priorityLabel);
        panel.add(highFirst);
        panel.add(lowFirst);

        int result = JOptionPane.showConfirmDialog(frame, panel,
            "Configure Algorithm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String sel = (String) algoBox.getSelectedItem();
            frame.setSelectedAlgorithm(sel);
            frame.setTimeQuantum((Integer) quantumSpinner.getValue());
            frame.setHigherPriorityFirst(highFirst.isSelected());
            algorithmLabel.setText("Algorithm: " + sel);
            // Refresh table to update cell editability
            table.repaint();
        }
    }

    private void runSimulation() {
        // Commit any in-progress cell edit
        if (table.isEditing()) table.getCellEditor().stopCellEditing();

        List<Process> processes = new ArrayList<>();
        Set<Integer> usedPriorities = new HashSet<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                String pid    = tableModel.getValueAt(i, 0).toString().trim();
                int burst     = Integer.parseInt(tableModel.getValueAt(i, 1).toString().trim());
                int arrival   = Integer.parseInt(tableModel.getValueAt(i, 2).toString().trim());
                int priority  = Integer.parseInt(tableModel.getValueAt(i, 3).toString().trim());

                if (burst < 1 || burst > 30)
                    throw new IllegalArgumentException("Row " + (i+1) + ": Burst time must be 1–30.");
                if (arrival < 0 || arrival > 30)
                    throw new IllegalArgumentException("Row " + (i+1) + ": Arrival time must be 0–30.");
                if (priority < 1 || priority > 20)
                    throw new IllegalArgumentException("Row " + (i+1) + ": Priority must be 1–20.");
                if (usedPriorities.contains(priority))
                    throw new IllegalArgumentException("Row " + (i+1) + ": Duplicate priority " + priority + ".");

                usedPriorities.add(priority);
                processes.add(new Process(pid, burst, arrival, priority));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Row " + (i+1) + " contains non-numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (processes.size() < 3) {
            JOptionPane.showMessageDialog(frame, "Please enter at least 3 processes.", "Too Few Processes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        frame.setProcesses(processes);
        frame.showScreen("SIMULATION");
    }
}
