package scheduler;

import model.Process;
import model.Timeline;
import model.TimelineEvent;

import java.util.*;

public class SrtfScheduler implements Scheduler {

    @Override
    public Timeline schedule(List<Process> original) {
        List<Process> processes = new ArrayList<>(original);
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<TimelineEvent> events = new ArrayList<>();
        PriorityQueue<Process> ready =
                new PriorityQueue<>(Comparator.comparingInt(Process::getRemainingTime));

        int time = 0;
        int index = 0;
        Process current = null;
        int currentStart = -1;

        while (index < processes.size() || !ready.isEmpty() || current != null) {
            // enqueue arrivals
            while (index < processes.size() &&
                   processes.get(index).getArrivalTime() <= time) {
                ready.add(processes.get(index++));
            }

            if (current == null) {
                if (ready.isEmpty()) {
                    // jump to next arrival
                    int nextArrival = processes.get(index).getArrivalTime();
                    events.add(new TimelineEvent("IDLE", time, nextArrival));
                    time = nextArrival;
                    continue;
                } else {
                    current = ready.poll();
                    currentStart = time;
                    current.setStartTimeIfUnset(time);
                }
            }

            // determine next interesting time (next arrival or completion)
            int nextArrivalTime = (index < processes.size())
                    ? processes.get(index).getArrivalTime()
                    : Integer.MAX_VALUE;
            int timeToFinish = current.getRemainingTime();
            int finishTime = time + timeToFinish;

            if (finishTime <= nextArrivalTime) {
                // no preemption, run to completion
                time = finishTime;
                current.decrementRemainingTime(timeToFinish);
                current.setCompletionTime(time);
                events.add(new TimelineEvent(current.getPid(), currentStart, time));
                current = null;
            } else {
                // preemption at next arrival
                int runFor = nextArrivalTime - time;
                current.decrementRemainingTime(runFor);
                time = nextArrivalTime;
                events.add(new TimelineEvent(current.getPid(), currentStart, time));
                ready.add(current);
                current = null;
            }
        }

        return new Timeline(events);
    }
}