package com.cpuScheduler.algorithm;

import com.cpuScheduler.model.GanttEntry;
import com.cpuScheduler.model.Process;
import com.cpuScheduler.model.ScheduleResult;

import java.util.*;

public class SJFNonPreemptive {
    public static ScheduleResult run(List<Process> input) {
        List<Process> processes = new ArrayList<>();
        for (Process p : input) processes.add(p.copy());

        List<GanttEntry> gantt = new ArrayList<>();
        List<Process> done = new ArrayList<>();
        int currentTime = 0;
        List<Process> remaining = new ArrayList<>(processes);

        while (!remaining.isEmpty()) {
            // Get all arrived processes
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

            // Pick shortest burst
            available.sort(Comparator.comparingInt((Process p) -> p.burstTime));
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
