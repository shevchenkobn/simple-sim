package se.lnu._4dv650.bs222sa.simulation.components;

public interface CurrentTime {
    int getCurrentTime();

    int getTickSize();

    int getStartTime();

    default int getTimeElapsed() {
        return getCurrentTime() - getStartTime();
    }

    default int getTicksElapsed() {
        return getTimeElapsed() / getTickSize();
    }
}
