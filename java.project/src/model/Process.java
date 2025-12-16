package model;

public class Process {
    private final String pid;
    private final int arrivalTime;
    private final int burstTime;

    private int remainingTime;
    private int startTime = -1;
    private int completionTime = -1;

    public Process(String pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    public String getPid() { return pid; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }

    public void decrementRemainingTime(int delta) {
        remainingTime = Math.max(0, remainingTime - delta);
    }

    public int getStartTime() { return startTime; }
    public void setStartTimeIfUnset(int time) {
        if (startTime == -1) startTime = time;
    }

    public int getCompletionTime() { return completionTime; }
    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    // Derived metrics
    public int getTurnaroundTime() { return completionTime - arrivalTime; }
    public int getWaitingTime() { return getTurnaroundTime() - burstTime; }
    public int getResponseTime() { return startTime - arrivalTime; }
}