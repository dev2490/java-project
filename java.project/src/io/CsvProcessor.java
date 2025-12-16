package io;

import exceptions.NegativeBurstException;
import model.Process;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvProcessor {

    public List<Process> readProcesses(File csvFile) throws IOException {
        List<Process> processes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            // optional: skip header if present
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                String pid = parts[0].trim();
                int arrival = Integer.parseInt(parts[1].trim());
                int burst = Integer.parseInt(parts[2].trim());
                if (burst < 0) throw new NegativeBurstException("Burst < 0 for " + pid);

                processes.add(new Process(pid, arrival, burst));
            }
        }
        return processes;
    }

    public void writeMetrics(File out, List<Process> processes,
                             double avgWT, double avgTAT, double avgRT,
                             double throughput, double utilization) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(out))) {
            pw.println("pid,arrival,burst,start,completion,WT,TAT,RT");
            for (Process p : processes) {
                pw.printf("%s,%d,%d,%d,%d,%d,%d,%d%n",
                        p.getPid(),
                        p.getArrivalTime(),
                        p.getBurstTime(),
                        p.getStartTime(),
                        p.getCompletionTime(),
                        p.getWaitingTime(),
                        p.getTurnaroundTime(),
                        p.getResponseTime());
            }
            pw.println();
            pw.printf("AVG_WT,%.2f%n", avgWT);
            pw.printf("AVG_TAT,%.2f%n", avgTAT);
            pw.printf("AVG_RT,%.2f%n", avgRT);
            pw.printf("THROUGHPUT,%.2f%n", throughput);
            pw.printf("CPU_UTILIZATION,%.2f%n", utilization);
        }
    }
}