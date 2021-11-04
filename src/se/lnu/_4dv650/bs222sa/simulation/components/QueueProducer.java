package se.lnu._4dv650.bs222sa.simulation.components;

public class QueueProducer implements ClockRunnable {
    private final SimulationRandom random;
    private final int arrivalInterval;
    private final QueueInput queue;
    private int eventsProduced = 0;

    public QueueProducer(SimulationRandom random, int arriveInterval, QueueInput queue) {
        if (random == null) {
            throw new IllegalArgumentException("Random generator cannot be null!");
        }
        if (arriveInterval <= 0) {
            throw new IllegalArgumentException("Time interval must be positive!");
        }
        if (queue == null) {
            throw new IllegalArgumentException("Queue cannot be null!");
        }
        this.random = random;
        this.arrivalInterval = arriveInterval;
        this.queue = queue;
    }

    @Override
    public void updateOnTick(CurrentTime time) {
        // Checking if the event is to be produced.
        if (random.next() < 1.0 / arrivalInterval) {
            queue.enqueue(new SimulationEvent(Integer.toString(eventsProduced), time.getCurrentTime()));
            eventsProduced += 1;
        }
    }

    public int getEventsProduced() {
        return eventsProduced;
    }
}
