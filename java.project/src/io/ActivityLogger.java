package io;

import java.io.*;

public class ActivityLogger {
    private final File logFile;

    public ActivityLogger(File logFile) {
        this.logFile = logFile;
    }

    public void log(String message) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
            pw.println(message);
        } catch (IOException e) {
            // optional: print to stderr
            e.printStackTrace();
        }
    }
}