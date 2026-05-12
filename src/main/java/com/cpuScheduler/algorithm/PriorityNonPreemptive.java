package com.cpuScheduler.algorithm;

import com.cpuScheduler.model.GanttEntry;
import com.cpuScheduler.model.Process;
import com.cpuScheduler.model.ScheduleResult;

import java.util.*;

public class PriorityNonPreemptive {
    /**
     * @param higherNumberMeansHighPriority true = bigger number = higher priority
     */
    public static ScheduleResult run(List<Process> input, boolean higherNumberMeansHighPriority) {
        List<Process> processes = new ArrayList<>();
        for (Process p : input) processes.add(p.copy());

        List<GanttEntry> gantt = new ArrayList<>();
        List<Process> done = new ArrayList<>();
        List<Process> remaining = new ArrayList<>(processes);
        int currentTime = 0;

        while (!remaining.isEmpty()) {
            List<Process> available = new ArrayList<>();
            for (Process p : remaining) {
                if (p.arrivalTime <= currentTime) available.add(p);
            }

            if (available.isEmpty()) {
                int nextArrival = remaining.stream().mapToInt(p -> p.arrivalTime).min().orElse(currentTime);
                gantt.add(new GanttEntry("IDLE", currentTime, nextArrival));
                currentTime = nextArrival;
                continue;
            }

            // Sort by priority, then by arrival time, then by input order
            available.sort((a, b) -> {
                // First compare by priority
                int priorityCompare = higherNumberMeansHighPriority
                        ? b.priority - a.priority
                        : a.priority - b.priority;
                if (priorityCompare != 0) return priorityCompare;
                
                // If same priority, compare by arrival time
                int arrivalCompare = a.arrivalTime - b.arrivalTime;
                if (arrivalCompare != 0) return arrivalCompare;
                
                // If same arrival time, maintain input order (both were in same available list, so compare by original index)
                // Find original indices in the processes list
                int indexA = remaining.indexOf(a);
                int indexB = remaining.indexOf(b);
                return indexA - indexB;
            });

            Process p = available.get(0);
            remaining.remove(p);

            int start = currentTime;
            int end = currentTime + p.burstTime;
            gantt.add(new GanttEntry(p.pid, start, end));
            p.waitingTime = start - p.arrivalTime;
            p.turnaroundTime = end - p.arrivalTime;
            currentTime = end;
            done.add(p);
        }

        return new ScheduleResult(gantt, done);
    }
}
