package se.lnu._4dv650.bs222sa.simulation.components;

import java.util.ArrayList;
import java.util.Iterator;

public class DepartedCollector implements Iterable<SimulationEvent> {
    private final ArrayList<SimulationEvent> events = new ArrayList<>();

    @Override
    public Iterator<SimulationEvent> iterator() {
        return events.iterator();
    }

    public int eventsCount() {
        return events.size();
    }

    public synchronized DepartedCollector add(SimulationEvent event) {
        events.add(event);
        return this;
    }
}
