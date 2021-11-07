package se.lnu._4dv650.bs222sa.simulation.components;

public class StatisticsCollector implements ClockRunnable {
    private final QueueServerInput queue;
    private double averageQueueSize;

    public StatisticsCollector(QueueServerInput queue) {
        if (queue == null) {
            throw new IllegalArgumentException("Queue cannot be null!");
        }
        this.queue = queue;
        averageQueueSize = this.queue.size();
    }

    public double getAverageQueueSize() {
        return averageQueueSize;
    }

    @Override
    public void updateOnTick(CurrentTime time) {
        averageQueueSize = (averageQueueSize * (time.getTicksElapsed() - 1) + this.queue.size()) / time.getTicksElapsed();
    }
}
