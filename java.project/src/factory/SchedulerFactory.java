package factory;

import scheduler.*;

public class SchedulerFactory {

    public enum Policy {
        FCFS, SJF, SRTF, RR
    }

    public static Scheduler create(Policy policy, Integer quantumOpt) {
        switch (policy) {
            case FCFS: return new FcfScheduler();
            case SJF: return new SjfScheduler();
            case SRTF: return new SrtfScheduler();
            case RR:
                if (quantumOpt == null) {
                    throw new IllegalArgumentException("RR requires a quantum");
                }
                return new RrScheduler(quantumOpt);
            default:
                throw new IllegalArgumentException("Unknown policy: " + policy);
        }
    }
}