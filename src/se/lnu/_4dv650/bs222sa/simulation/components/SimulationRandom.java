package se.lnu._4dv650.bs222sa.simulation.components;

import java.util.Random;

public class SimulationRandom {
    private final Random random;

    public SimulationRandom() {
        random = new Random();
    }

    /**
     *
     * @return Double in [0; 1)
     */
    double next() {
        return random.nextDouble();
    }
}
