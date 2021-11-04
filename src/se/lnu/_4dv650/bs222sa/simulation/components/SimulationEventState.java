package se.lnu._4dv650.bs222sa.simulation.components;

public enum SimulationEventState {
    Queued,
    /**
     * Processed by the server.
     */
    Serving,
    Departed,
}
