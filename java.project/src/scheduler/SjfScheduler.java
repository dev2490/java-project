package scheduler;

import model.Process;
import model.Timeline;
import model.TimelineEvent;

import java.util.*;

public class SjfScheduler implements Scheduler {

    @Override
    public Timeline schedule(List<Process> original) {
        List<Process> processes = new ArrayList<>(original);
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<TimelineEvent> events = new ArrayList<>();
        PriorityQueue<Process> ready =
                new PriorityQueue<>(Comparator.comparingInt(Process::getBurstTime));

        int time = 0;
        int index = 0; // pointer into processes list

        while (index < processes.size() || !ready.isEmpty()) {
            // load newly arrived processes
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
            int start = time;
            time += p.getBurstTime();
            p.setCompletionTime(time);
            events.add(new TimelineEvent(p.getPid(), start, time));
        }

        return new Timeline(events);
    }
}