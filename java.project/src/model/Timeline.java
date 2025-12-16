package model;

import java.util.List;

public class Timeline {
    private final List<TimelineEvent> events;

    public Timeline(List<TimelineEvent> events) {
        this.events = events;
    }

    public List<TimelineEvent> getEvents() { return events; }

    public int getStartTime() {
        return events.isEmpty() ? 0 : events.get(0).getStartTime();
    }

    public int getEndTime() {
        return events.isEmpty() ? 0 : events.get(events.size() - 1).getEndTime();
    }

    public int getBusyTime() {
        return events.stream()
                     .filter(e -> !"IDLE".equals(e.getPid()))
                     .mapToInt(TimelineEvent::getDuration)
                     .sum();
    }
}