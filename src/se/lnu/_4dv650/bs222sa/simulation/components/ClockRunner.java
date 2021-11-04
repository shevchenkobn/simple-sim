package se.lnu._4dv650.bs222sa.simulation.components;

import java.util.ArrayList;
import java.util.Collections;

public class ClockRunner {
    private final Clock clock;
    private final ArrayList<ClockRunnable> runnables;

    public ClockRunner(Clock clock, Iterable<ClockRunnable> runnables) {
        this.clock = clock;
        this.runnables = new ArrayList<>();
        runnables.forEach(this.runnables::add);
    }

    public Iterable<ClockRunnable> getRunnables() {
        return runnables;
    }

    public synchronized ClockRunner tick() {
        // Shuffling is used to avoid the first one getting all the load and simulate randomness of load-balancing.
        Collections.shuffle(runnables);
        clock.tick();
        for (var runnable : runnables) {
            runnable.updateOnTick(clock);
        }
        return this;
    }
}
