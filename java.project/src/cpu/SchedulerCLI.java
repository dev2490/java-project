package cpu;

import factory.SchedulerFactory;
import factory.SchedulerFactory.Policy;
import io.ActivityLogger;
import io.CsvProcessor;
import io.GanttWriter;
import model.Process;
import model.Timeline;
import scheduler.Scheduler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SchedulerCLI {

    private File processFile;
    private Policy policy = Policy.FCFS;
    private Integer quantum = null;
    private List<Process> processes;
    private Timeline lastTimeline;
    private MetricsCalculator.MetricsResult lastMetrics;

    private final CsvProcessor csvProcessor = new CsvProcessor();
    private final GanttWriter ganttWriter = new GanttWriter();
    private final ActivityLogger logger = new ActivityLogger(new File("activity.log"));
    private final MetricsCalculator metricsCalculator = new MetricsCalculator();

    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println("CPU Scheduling CLI. Type 'help' for commands.");

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine();
            if (line == null) break;
            line = line.trim();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;

            try {
                handleCommand(line);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                logger.log("ERROR: " + e.getMessage());
            }
        }
    }

    private void handleCommand(String line) throws IOException {
        if (line.startsWith("load ")) {
            String path = line.substring("load ".length()).trim();
            processFile = new File(path);
            processes = csvProcessor.readProcesses(processFile);
            logger.log("Loaded processes from " + path);
            System.out.println("Loaded " + processes.size() + " processes.");
        } else if (line.startsWith("set policy")) {
            // e.g. set policy FCFS, or set policy RR --quantum 3
            String[] parts = line.split("\\s+");
            if (parts.length < 3) {
                System.out.println("Usage: set policy <FCFS|SJF|SRTF|RR> [--quantum q]");
                return;
            }
            policy = Policy.valueOf(parts[2].toUpperCase());
            quantum = null;
            if (policy == Policy.RR && parts.length >= 5 && "--quantum".equals(parts[3])) {
                quantum = Integer.parseInt(parts[4]);
            }
            logger.log("Policy set to " + policy + (quantum != null ? " q=" + quantum : ""));
        } else if (line.equals("simulate")) {
            if (processes == null) {
                System.out.println("No processes loaded.");
                return;
            }
            Scheduler scheduler = SchedulerFactory.create(policy, quantum);
            lastTimeline = scheduler.schedule(processes);
            lastMetrics = metricsCalculator.compute(processes, lastTimeline);
            logger.log("Simulation executed with " + policy);
            System.out.println("Simulation complete.");
        } else if (line.equals("metrics")) {
            if (lastMetrics == null) {
                System.out.println("No simulation run yet.");
                return;
            }
            System.out.printf("Avg WT: %.2f%n", lastMetrics.avgWT);
            System.out.printf("Avg TAT: %.2f%n", lastMetrics.avgTAT);
            System.out.printf("Avg RT: %.2f%n", lastMetrics.avgRT);
            System.out.printf("Throughput: %.2f%n", lastMetrics.throughput);
            System.out.printf("CPU Utilization: %.2f%n", lastMetrics.utilization);
        } else if (line.startsWith("export")) {
            if (lastTimeline == null || lastMetrics == null) {
                System.out.println("Run simulate first.");
                return;
            }
            ganttWriter.writeGantt(new File("gantt.txt"), lastTimeline);
            csvProcessor.writeMetrics(new File("metrics.csv"), processes,
                    lastMetrics.avgWT, lastMetrics.avgTAT, lastMetrics.avgRT,
                    lastMetrics.throughput, lastMetrics.utilization);
            logger.log("Exported gantt.txt and metrics.csv");
            System.out.println("Exported gantt.txt and metrics.csv");
        } else if (line.equals("help")) {
            System.out.println("Commands:");
            System.out.println("  load <path/to/processes.csv>");
            System.out.println("  set policy <FCFS|SJF|SRTF|RR> [--quantum q]");
            System.out.println("  simulate");
            System.out.println("  metrics");
            System.out.println("  export");
            System.out.println("  exit");
        } else {
            System.out.println("Unknown command. Type 'help'.");
        }
    }

    public static void main(String[] args) {
        new SchedulerCLI().run();
    }
}