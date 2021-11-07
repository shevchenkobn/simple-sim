package se.lnu._4dv650.bs222sa.simulation.components;

public class StringPullServer implements ClockRunnable {
    private final SimulationRandom random;
    private final int serviceTime;
    private final QueueServerInput queue;
    private final DepartedCollector departed;
    private SimulationEvent currentProcessing;
    private int currentProcessingTime = Integer.MIN_VALUE;
    private int busyTime;

    public StringPullServer(SimulationRandom random, int serviceTime, QueueServerInput queue, DepartedCollector departed) {
        if (random == null) {
            throw new IllegalArgumentException("Random generator cannot be null!");
        }
        if (serviceTime <= 0) {
            throw new IllegalArgumentException("Service time must be positive!");
        }
        if (queue == null) {
            throw new IllegalArgumentException("Queue cannot be null!");
        }
        if (departed == null) {
            throw new IllegalArgumentException("Departed cannot be null!");
        }
        this.random = random;
        this.serviceTime = serviceTime;
        this.queue = queue;
        this.departed = departed;
    }

    public SimulationEvent getCurrentProcessing() {
        return currentProcessing;
    }

    public int getBusyTime() {
        return busyTime;
    }

    @Override
    public void updateOnTick(CurrentTime time) {
        if (currentProcessing != null) {
            tryFinishProcessing(time);
        } else {
            tryStartProcessing(time);
        }
        updateMetrics(time);
    }

    private void tryStartProcessing(CurrentTime time) {
        if (queue.isEmpty()) {
            return;
        }
        currentProcessing = queue.popFirst();
        currentProcessing.startServing(time.getCurrentTime());
        currentProcessingTime = time.getCurrentTime() + random.nextIntExponential(serviceTime);
    }

    private void tryFinishProcessing(CurrentTime time) {
        if (time.getCurrentTime() >= currentProcessingTime) {
            currentProcessing.finishServing(time.getCurrentTime());
            departed.add(currentProcessing);
            currentProcessing = null;
            currentProcessingTime = Integer.MIN_VALUE;
        }
    }

    private void updateMetrics(CurrentTime time) {
        if (currentProcessing != null) {
            busyTime += time.getTickSize();
        }
    }
}
