package se.lnu._4dv650.bs222sa.simulation.components;

public class Clock implements CurrentTime {
    private final int startTime;
    private final int tickSize;
    private int currentTime;

    public Clock(int startTime, int tick) {
        if (tick <= 0) {
            throw new IllegalArgumentException(String.format("Tick must be a positive value, got %d!", tick));
        }
        this.startTime = startTime;
        tickSize = tick;

        currentTime = this.startTime;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public int getTickSize() {
        return tickSize;
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    public synchronized int tick() {
        currentTime += tickSize;
        return currentTime;
    }
}
