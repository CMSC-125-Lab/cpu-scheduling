package com.cpuScheduler.algorithm;

import com.cpuScheduler.model.GanttEntry;
import com.cpuScheduler.model.Process;
import com.cpuScheduler.model.ScheduleResult;

import java.util.*;

public class FCFS {
    public static ScheduleResult run(List<Process> input) {
        List<Process> processes = new ArrayList<>();
        for (Process p : input) processes.add(p.copy());

        processes.sort(Comparator.comparingInt((Process p) -> p.arrivalTime));

        List<GanttEntry> gantt = new ArrayList<>();
        int currentTime = 0;

        for (Process p : processes) {
            if (currentTime < p.arrivalTime) {
                gantt.add(new GanttEntry("IDLE", currentTime, p.arrivalTime));
                currentTime = p.arrivalTime;
            }
            int start = currentTime;
            int end = currentTime + p.burstTime;
            gantt.add(new GanttEntry(p.pid, start, end));
            p.waitingTime = start - p.arrivalTime;
            p.turnaroundTime = end - p.arrivalTime;
            currentTime = end;
        }

        return new ScheduleResult(gantt, processes);
    }
}
