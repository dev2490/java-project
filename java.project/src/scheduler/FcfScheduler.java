package scheduler;

import model.Process;
import model.Timeline;
import model.TimelineEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FcfScheduler implements Scheduler {

    @Override
    public Timeline schedule(List<Process> original) {
        List<Process> processes = new ArrayList<>(original);
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<TimelineEvent> events = new ArrayList<>();
        int time = 0;

        for (Process p : processes) {
            if (time < p.getArrivalTime()) {
                // Idle period
                events.add(new TimelineEvent("IDLE", time, p.getArrivalTime()));
                time = p.getArrivalTime();
            }
            p.setStartTimeIfUnset(time);
            time += p.getBurstTime();
            p.setCompletionTime(time);
            events.add(new TimelineEvent(p.getPid(), time - p.getBurstTime(), time));
        }

        return new Timeline(events);
    }
}