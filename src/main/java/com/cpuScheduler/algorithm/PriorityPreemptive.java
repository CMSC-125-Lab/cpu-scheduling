package com.cpuScheduler.algorithm;

import com.cpuScheduler.model.GanttEntry;
import com.cpuScheduler.model.Process;
import com.cpuScheduler.model.ScheduleResult;

import java.util.*;

public class PriorityPreemptive {
    public static ScheduleResult run(List<Process> input, boolean higherNumberMeansHighPriority) {
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
            Process best = null;
            int bestPriority = higherNumberMeansHighPriority ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            int bestArrival = Integer.MAX_VALUE;
            int bestIndex = Integer.MAX_VALUE;

            for (int i = 0; i < processes.size(); i++) {
                Process p = processes.get(i);
                if (p.arrivalTime <= currentTime && p.remainingTime > 0) {
                    boolean isBetter = false;
                    
                    // Compare by priority first
                    boolean priorityBetter = higherNumberMeansHighPriority
                            ? p.priority > bestPriority
                            : p.priority < bestPriority;
                    
                    if (best == null) {
                        isBetter = true;
                    } else if (p.priority == bestPriority) {
                        // Same priority: use arrival time, then input order
                        if (p.arrivalTime < bestArrival) {
                            isBetter = true;
                        } else if (p.arrivalTime == bestArrival && i < bestIndex) {
                            isBetter = true;
                        }
                    } else if (priorityBetter) {
                        isBetter = true;
                    }
                    
                    if (isBetter) {
                        bestPriority = p.priority;
                        bestArrival = p.arrivalTime;
                        bestIndex = i;
                        best = p;
                    }
                }
            }

            if (best == null) {
                if (!"IDLE".equals(lastPid)) {
                    if (lastPid != null) gantt.add(new GanttEntry(lastPid, segStart, currentTime));
                    segStart = currentTime;
                    lastPid = "IDLE";
                }
                currentTime++;
                continue;
            }

            if (!best.pid.equals(lastPid)) {
                if (lastPid != null) gantt.add(new GanttEntry(lastPid, segStart, currentTime));
                segStart = currentTime;
                lastPid = best.pid;
            }

            best.remainingTime--;
            currentTime++;

            if (best.remainingTime == 0) {
                completed++;
                int idx = indexOf(processes, best);
                completionTime[idx] = currentTime;
            }
        }

        if (lastPid != null) gantt.add(new GanttEntry(lastPid, segStart, currentTime));

        for (int i = 0; i < n; i++) {
            Process p = processes.get(i);
            p.turnaroundTime = completionTime[i] - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
        }

        return new ScheduleResult(gantt, processes);
    }

    private static int indexOf(List<Process> list, Process p) {
        for (int i = 0; i < list.size(); i++) if (list.get(i) == p) return i;
        return -1;
    }
}
