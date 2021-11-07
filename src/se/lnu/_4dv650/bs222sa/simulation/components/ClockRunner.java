package se.lnu._4dv650.bs222sa.simulation.components;

import java.util.ArrayList;
import java.util.Collections;

public class ClockRunner {
    private final Clock clock;
    private final ArrayList<ClockRunnable> shufflableRunnables;
    private final Iterable<ClockRunnable> postRunnables;

    public ClockRunner(Clock clock, Iterable<ClockRunnable> shufflableRunnables, Iterable<ClockRunnable> postRunnables) {
        this.clock = clock;
        this.shufflableRunnables = new ArrayList<>();
        shufflableRunnables.forEach(this.shufflableRunnables::add);
        this.postRunnables = postRunnables;
    }

    public Iterable<ClockRunnable> getShufflableRunnables() {
        return shufflableRunnables;
    }

    public synchronized ClockRunner tick() {
        // Shuffling is used to avoid the first one getting all the load and simulate randomness of load-balancing.
        Collections.shuffle(shufflableRunnables);
        clock.tick();
        for (var runnable : shufflableRunnables) {
            runnable.updateOnTick(clock);
        }
        if (postRunnables != null) {
            for (var runnable : postRunnables) {
                runnable.updateOnTick(clock);
            }
        }
        return this;
    }
}
