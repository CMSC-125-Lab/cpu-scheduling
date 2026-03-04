error id: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/schedulingAlgorithms/FCFS.java:java/lang/Process#arrivalTime#
file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/schedulingAlgorithms/FCFS.java
empty definition using pc, found symbol in pc: java/lang/Process#arrivalTime#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 449
uri: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/schedulingAlgorithms/FCFS.java
text:
```scala
package com.cpuScheduler.schedulingAlgorithms;

import java.util.Comparator;
import java.util.List;

public class FCFS extends CPUScheduler {
    public FCFS(List<Process> processes) {
        super(processes);
    }

    @Override
    public void execute() {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;

        for (Process p : processes) {
            if (currentTime < p.arri@@valTime) {
                currentTime = p.arrivalTime;
            }
            p.startTime = currentTime;
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            
            gantt.add(new GanttRecord(p.pid, currentTime, p.completionTime, p.color));
            currentTime = p.completionTime;
        }
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/lang/Process#arrivalTime#