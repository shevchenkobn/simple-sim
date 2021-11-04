package se.lnu._4dv650.bs222sa.simulation.components;

import java.util.LinkedList;
import java.util.Queue;

public class EventQueue implements QueueInput, QueueServerInput {
    private final Queue<SimulationEvent> queue = new LinkedList<>();

    @Override
    public void enqueue(SimulationEvent event) {
        queue.add(event);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public SimulationEvent popFirst() {
        return queue.remove();
    }

//    public Queue<SimulationEvent> getQueue() {
//        return queue;
//    }
}
