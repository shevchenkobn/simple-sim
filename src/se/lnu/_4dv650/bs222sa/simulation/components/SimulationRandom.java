package se.lnu._4dv650.bs222sa.simulation.components;

import java.util.Random;

public class SimulationRandom {
    private final Random random;

    public SimulationRandom() {
        random = new Random();
    }

    /**
     * Get a pseudo-random Uniformly distributed value.
     *
     * @return Double in [0; 1)
     */
    double next() {
        return random.nextDouble();
    }

    /**
     * Checks if a pseudo-random Uniformly distributed event will happen on this tick of simulation.
     *
     * @param timeTickSize  The size of tick (step) in the simulation step.
     * @param eventInterval The time interval, during which exactly one event is expected to happen
     * @return The flag showing if the event will happen for the current call.
     */
    boolean nextBoolTimeNormalized(int timeTickSize, int eventInterval) {
        return next() < (double) timeTickSize / eventInterval;
    }
}
