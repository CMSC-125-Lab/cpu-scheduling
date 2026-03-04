package com.cpuScheduler.schedulingAlgorithms;

import com.cpuScheduler.GanttEntry;
import com.cpuScheduler.Process;
import com.cpuScheduler.ScheduleResult;

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

            available.sort((a, b) -> higherNumberMeansHighPriority
                    ? b.priority - a.priority
                    : a.priority - b.priority);

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
