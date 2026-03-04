package com.cpuScheduler.schedulingAlgorithms;

import com.cpuScheduler.GanttEntry;
import com.cpuScheduler.Process;
import com.cpuScheduler.ScheduleResult;

import java.util.*;

public class RoundRobin {
    public static ScheduleResult run(List<Process> input, int quantum) {
        List<Process> processes = new ArrayList<>();
        for (Process p : input) processes.add(p.copy());
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        List<GanttEntry> gantt = new ArrayList<>();
        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0;
        int idx = 0;
        int n = processes.size();
        int[] completionTime = new int[n];

        while (true) {
            // Add newly arrived processes
            while (idx < n && processes.get(idx).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(idx));
                idx++;
            }

            if (readyQueue.isEmpty()) {
                if (idx >= n) break;
                // Idle
                int nextArrival = processes.get(idx).arrivalTime;
                gantt.add(new GanttEntry("IDLE", currentTime, nextArrival));
                currentTime = nextArrival;
                continue;
            }

            Process p = readyQueue.poll();
            int execTime = Math.min(quantum, p.remainingTime);
            int start = currentTime;
            int end = currentTime + execTime;
            gantt.add(new GanttEntry(p.pid, start, end));
            currentTime = end;
            p.remainingTime -= execTime;

            // Add newly arrived during this slice
            while (idx < n && processes.get(idx).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(idx));
                idx++;
            }

            if (p.remainingTime > 0) {
                readyQueue.add(p);
            } else {
                int procIdx = indexOf(processes, p);
                completionTime[procIdx] = currentTime;
            }

            if (readyQueue.isEmpty() && idx >= n) break;
        }

        for (int i = 0; i < n; i++) {
            Process p = processes.get(i);
            p.turnaroundTime = completionTime[i] - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
        }

        return new ScheduleResult(gantt, processes);
    }

    private static int indexOf(List<Process> list, Process p) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == p) return i;
        }
        return -1;
    }
}
