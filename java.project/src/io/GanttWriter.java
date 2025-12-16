package io;

import model.Timeline;
import model.TimelineEvent;

import java.io.*;

public class GanttWriter {

    public void writeGantt(File out, Timeline timeline) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(out))) {
            // simple ASCII: [P1][P1][IDLE][P2]...
            for (TimelineEvent e : timeline.getEvents()) {
                pw.printf("[%s:%d-%d]", e.getPid(), e.getStartTime(), e.getEndTime());
            }
            pw.println();
        }
    }
}