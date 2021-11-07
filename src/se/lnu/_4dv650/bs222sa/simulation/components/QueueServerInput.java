package se.lnu._4dv650.bs222sa.simulation.components;

public interface QueueServerInput {
    int size();

    boolean isEmpty();

    /**
     * Removes the first element in queue and returns it.
     */
    SimulationEvent popFirst();
}
