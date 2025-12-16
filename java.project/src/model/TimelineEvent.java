package model;

public class TimelineEvent {
    private final String pid;      // can be "IDLE" for idle CPU
    private final int startTime;
    private final int endTime;

    public TimelineEvent(String pid, int startTime, int endTime) {
        this.pid = pid;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getPid() { return pid; }
    public int getStartTime() { return startTime; }
    public int getEndTime() { return endTime; }

    public int getDuration() { return endTime - startTime; }
}