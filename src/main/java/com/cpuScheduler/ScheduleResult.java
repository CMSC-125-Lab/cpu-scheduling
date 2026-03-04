package com.cpuScheduler;

import java.util.List;

public class ScheduleResult {
    public List<GanttEntry> gantt;
    public List<Process> processes;

    public ScheduleResult(List<GanttEntry> gantt, List<Process> processes) {
        this.gantt = gantt;
        this.processes = processes;
    }
}
