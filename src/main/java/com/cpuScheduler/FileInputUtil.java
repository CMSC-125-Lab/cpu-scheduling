package com.cpuScheduler;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class FileInputUtil {
    /**
     * Reads a CSV or TXT file. Expected format (one process per line):
     *   PID, BurstTime, ArrivalTime, Priority
     * Lines starting with '#' or the header line are skipped.
     */
    public static List<Process> loadFromFile(File file, JFrame parent) {
        List<Process> processes = new ArrayList<>();
        Set<Integer> usedPriorities = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("[,\\t]+");
                if (parts.length < 4) {
                    // Try skipping header
                    if (lineNum == 1) continue;
                    JOptionPane.showMessageDialog(parent,
                        "Line " + lineNum + " is malformed: \"" + line + "\"\nExpected: PID, Burst, Arrival, Priority",
                        "Parse Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                try {
                    String pid     = parts[0].trim();
                    int burst      = Integer.parseInt(parts[1].trim());
                    int arrival    = Integer.parseInt(parts[2].trim());
                    int priority   = Integer.parseInt(parts[3].trim());

                    if (burst < 1 || burst > 30)
                        throw new IllegalArgumentException("Burst time out of range [1-30] for " + pid);
                    if (arrival < 0 || arrival > 30)
                        throw new IllegalArgumentException("Arrival time out of range [0-30] for " + pid);
                    if (priority < 1 || priority > 20)
                        throw new IllegalArgumentException("Priority out of range [1-20] for " + pid);
                    if (usedPriorities.contains(priority))
                        throw new IllegalArgumentException("Duplicate priority " + priority + " for " + pid);

                    usedPriorities.add(priority);
                    processes.add(new Process(pid, burst, arrival, priority));
                } catch (NumberFormatException ex) {
                    if (lineNum == 1) continue; // skip header
                    JOptionPane.showMessageDialog(parent,
                        "Non-numeric value on line " + lineNum + ": \"" + line + "\"",
                        "Parse Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(parent, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Could not read file: " + ex.getMessage(),
                "File Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (processes.size() < 3 || processes.size() > 20) {
            JOptionPane.showMessageDialog(parent,
                "Number of processes must be between 3 and 20. Found: " + processes.size(),
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return processes;
    }
}
