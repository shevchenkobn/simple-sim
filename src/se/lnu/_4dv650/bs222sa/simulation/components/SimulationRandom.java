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

    double nextExponential(double mean) {
        return -mean * Math.log(1 - next());
    }

    int nextIntExponential(double mean) {
        return (int) Math.round(nextExponential(mean));
    }
}
