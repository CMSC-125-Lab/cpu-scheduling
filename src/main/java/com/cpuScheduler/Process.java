package com.cpuScheduler;

public class Process {
    public String pid;
    public int burstTime;
    public int arrivalTime;
    public int priority;

    // Computed fields
    public int waitingTime;
    public int turnaroundTime;
    public int remainingTime; // for preemptive

    public Process(String pid, int burstTime, int arrivalTime, int priority) {
        this.pid = pid;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
    }

    public Process copy() {
        return new Process(pid, burstTime, arrivalTime, priority);
    }

    @Override
    public String toString() {
        return pid;
    }
}
