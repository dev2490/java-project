package scheduler;

import exceptions.InvalidQuantumException;
import model.Process;
import model.Timeline;
import model.TimelineEvent;

import java.util.*;

public class RrScheduler implements Scheduler {

    private final int quantum;

    public RrScheduler(int quantum) {
        if (quantum <= 0) throw new InvalidQuantumException("Quantum must be > 0");
        this.quantum = quantum;
    }

    @Override
    public Timeline schedule(List<Process> original) {
        List<Process> processes = new ArrayList<>(original);
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        Queue<Process> ready = new ArrayDeque<>();
        List<TimelineEvent> events = new ArrayList<>();

        int time = 0;
        int index = 0;

        while (index < processes.size() || !ready.isEmpty()) {
            // bring in processes that have arrived
            while (index < processes.size() &&
                   processes.get(index).getArrivalTime() <= time) {
                ready.add(processes.get(index++));
            }

            if (ready.isEmpty()) {
                int nextArrival = processes.get(index).getArrivalTime();
                events.add(new TimelineEvent("IDLE", time, nextArrival));
                time = nextArrival;
                continue;
            }

            Process p = ready.poll();
            p.setStartTimeIfUnset(time);

            int execTime = Math.min(quantum, p.getRemainingTime());
            int start = time;
            time += execTime;
            p.decrementRemainingTime(execTime);
            events.add(new TimelineEvent(p.getPid(), start, time));

            // enqueue new arrivals during this quantum
            while (index < processes.size() &&
                   processes.get(index).getArrivalTime() <= time) {
                ready.add(processes.get(index++));
            }

            if (p.getRemainingTime() > 0) {
                ready.add(p);
            } else {
                p.setCompletionTime(time);
            }
        }

        return new Timeline(events);
    }
}