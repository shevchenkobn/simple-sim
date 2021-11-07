package se.lnu._4dv650.bs222sa.simulation.components;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class EventQueue implements QueueInput, QueueServerInput, Iterable<SimulationEvent> {
    private final Queue<SimulationEvent> queue = new LinkedList<>();

    @Override
    public void enqueue(SimulationEvent event) {
        queue.add(event);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public synchronized SimulationEvent popFirst() {
        return queue.remove();
    }

    @Override
    public Iterator<SimulationEvent> iterator() {
        return queue.iterator();
    }
}
