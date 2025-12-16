package cpu;

import model.Process;
import model.Timeline;

import java.util.List;

public class MetricsCalculator {

    public MetricsResult compute(List<Process> processes, Timeline timeline) {
        int n = processes.size();

        double avgWT = processes.stream()
                .mapToInt(Process::getWaitingTime)
                .average().orElse(0);

        double avgTAT = processes.stream()
                .mapToInt(Process::getTurnaroundTime)
                .average().orElse(0);

        double avgRT = processes.stream()
                .mapToInt(Process::getResponseTime)
                .average().orElse(0);

        int totalTime = timeline.getEndTime() - timeline.getStartTime();
        int completed = (int) processes.stream()
                .filter(p -> p.getCompletionTime() >= 0)
                .count();

        double throughput = (totalTime == 0) ? 0 : (double) completed / totalTime;
        double utilization = (totalTime == 0) ? 0 :
                (double) timeline.getBusyTime() / totalTime;

        return new MetricsResult(avgWT, avgTAT, avgRT, throughput, utilization);
    }

    public static class MetricsResult {
        public final double avgWT;
        public final double avgTAT;
        public final double avgRT;
        public final double throughput;
        public final double utilization;

        public MetricsResult(double avgWT, double avgTAT, double avgRT,
                             double throughput, double utilization) {
            this.avgWT = avgWT;
            this.avgTAT = avgTAT;
            this.avgRT = avgRT;
            this.throughput = throughput;
            this.utilization = utilization;
        }
    }
}