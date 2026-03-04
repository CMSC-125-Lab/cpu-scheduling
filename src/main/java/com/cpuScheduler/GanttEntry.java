package com.cpuScheduler;

public class GanttEntry {
    public String pid;   // "IDLE" for idle time
    public int startTime;
    public int endTime;

    public GanttEntry(String pid, int startTime, int endTime) {
        this.pid = pid;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
