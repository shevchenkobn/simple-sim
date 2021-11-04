package se.lnu._4dv650.bs222sa.simulation.components;

public class SimulationEvent {
    private final String id;
    private final int arrivalTime;
    private int serviceStartTime;
    private int serviceFinishTime;
    private SimulationEventState state;

    public SimulationEvent(String id, int arrivalTime) {
        if (id == null) {
            throw new IllegalArgumentException("Event id cannot be null");
        }
        this.id = id;
        state = SimulationEventState.Queued;
        this.arrivalTime = arrivalTime;
        this.serviceStartTime = Integer.MIN_VALUE;
        this.serviceFinishTime = Integer.MIN_VALUE;
    }

    public String getId() {
        return id;
    }

    public SimulationEventState getState() {
        return state;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceStartTime() {
        return serviceStartTime;
    }

    public int getServiceFinishTime() {
        return serviceFinishTime;
    }

    public synchronized void startServing(int serviceStartTime) {
        if (state != SimulationEventState.Queued) {
            throw new IllegalStateException("Cannot start serving, the event is not in Queued state!");
        }
        if (serviceStartTime < arrivalTime) {
            throw new IllegalArgumentException(String.format("Service Start Time %d cannot be fewer than Arrival Time %d!", serviceStartTime, arrivalTime));
        }
        this.state = SimulationEventState.Serving;
        this.serviceStartTime = serviceStartTime;
    }

    public synchronized void finishServing(int serviceFinishTime) {
        if (state != SimulationEventState.Serving) {
            throw new IllegalStateException("Cannot finish serving, the event is not in Serving state!");
        }
        if (serviceFinishTime < serviceStartTime) {
            throw new IllegalArgumentException(String.format("Service Finish Time %d cannot be fewer than Service Start Time %d!", serviceFinishTime, serviceStartTime));
        }
        this.state = SimulationEventState.Departed;
        this.serviceFinishTime = serviceFinishTime;
    }

    public int getDelayInQueue() {
        if (state == SimulationEventState.Queued) {
            throw new IllegalArgumentException("Cannot get delay in queue, the event is still in Queued state!");
        }
        return serviceStartTime - arrivalTime;
    }

    public int getServiceTime() {
        if (state != SimulationEventState.Departed) {
            throw new IllegalArgumentException("Cannot get service time, the event is not in Served state!");
        }
        return serviceFinishTime - serviceStartTime;
    }
}
