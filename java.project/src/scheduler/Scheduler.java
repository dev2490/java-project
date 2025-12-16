package scheduler;

import java.util.List;
import model.Process;
import model.Timeline;

public interface Scheduler {
    Timeline schedule(List<Process> processes);
}