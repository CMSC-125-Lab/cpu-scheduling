package com.cpuScheduler.algorithm;

import com.cpuScheduler.model.GanttEntry;
import com.cpuScheduler.model.Process;
import com.cpuScheduler.model.ScheduleResult;

import java.util.*;

public class SJFPreemptive {
    public static ScheduleResult run(List<Process> input) {
        List<Process> processes = new ArrayList<>();
        for (Process p : input) processes.add(p.copy());

        List<GanttEntry> gantt = new ArrayList<>();
        int n = processes.size();
        int[] completionTime = new int[n];
        int currentTime = 0;
        int completed = 0;

        String lastPid = null;
        int segStart = 0;

        while (completed < n) {
            Process shortest = null;
            int minRemaining = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && p.remainingTime < minRemaining) {
                    minRemaining = p.remainingTime;
                    shortest = p;
                }
            }

            if (shortest == null) {
                // Idle
                if (!"IDLE".equals(lastPid)) {
                    if (lastPid != null) gantt.add(new GanttEntry(lastPid, segStart, currentTime));
                    segStart = currentTime;
                    lastPid = "IDLE";
                }
                currentTime++;
                continue;
            }

            if (!shortest.pid.equals(lastPid)) {
                if (lastPid != null) gantt.add(new GanttEntry(lastPid, segStart, currentTime));
                segStart = currentTime;
                lastPid = shortest.pid;
            }

            shortest.remainingTime--;
            currentTime++;

            if (shortest.remainingTime == 0) {
                completed++;
                int idx = indexOf(processes, shortest);
                completionTime[idx] = currentTime;
            }
        }

        if (lastPid != null) gantt.add(new GanttEntry(lastPid, segStart, currentTime));

        List<Process> result = new ArrayList<>(processes);
        for (int i = 0; i < n; i++) {
            Process p = result.get(i);
            p.turnaroundTime = completionTime[i] - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
        }

        return new ScheduleResult(gantt, result);
    }

    private static int indexOf(List<Process> list, Process p) {
        for (int i = 0; i < list.size(); i++) if (list.get(i) == p) return i;
        return -1;
    }
}
