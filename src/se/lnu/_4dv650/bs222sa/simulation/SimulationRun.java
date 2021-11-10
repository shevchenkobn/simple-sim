package se.lnu._4dv650.bs222sa.simulation;

import se.lnu._4dv650.bs222sa.simulation.components.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulationRun {
    private final int timeTickSize = 1;
    private final int arrivalInterval = 10;
    private final int serviceTime = 15;

    private SimulationState state = SimulationState.Initialized;

    private EventQueue queue;
    private Clock clock;
    private SimulationRandom random;
    private QueueProducer queueProducer;
    private List<StringPullServer> servers;
    private DepartedCollector departedCollector;
    private StatisticsCollector statisticsCollector;
    private ClockRunner clockRunner;

    private ArrayList<SimulationEvent> allEvents;
    private ArrayList<SimulationEvent> departedEvents;
    private ArrayList<SimulationEvent> startedProcessingEvents;
    private ArrayList<SimulationEvent> eventsInProgress;

    private double averageInterArrivalTime;
    private double averageWaitingTime;
    private double averageServiceTime;
    private List<Double> serversUsagePercentages;

    public SimulationRun() {
        queue = new EventQueue();
        clock = new Clock(0, timeTickSize);
        random = new SimulationRandom();

        queueProducer = new QueueProducer(random, arrivalInterval, queue);
        departedCollector = new DepartedCollector();
        servers = Arrays.asList(
                new StringPullServer(random, serviceTime, queue, departedCollector),
                new StringPullServer(random, serviceTime, queue, departedCollector)
        );

        statisticsCollector = new StatisticsCollector(queue);

        clockRunner = new ClockRunner(clock, Stream.concat(Stream.of(
                queueProducer
        ), servers.stream()).collect(Collectors.toList()), List.of(statisticsCollector));
    }

    public void run() {
        if (state != SimulationState.Initialized) {
            throw new IllegalStateException("The simulation was already run!");
        }
        state = SimulationState.RunFinished;
//        while (departedCollector.eventsCount() <= 1e6) {
        while (clock.getCurrentTime() < 10080) {
            clockRunner.tick();
        }
    }

    public void calculateStatistics() {
        if (state == SimulationState.StatisticsCalculated) {
            return;
        }
        if (state != SimulationState.RunFinished) {
            throw new IllegalStateException("The simulation wasn't run!");
        }
        allEvents = new ArrayList<>();
        departedEvents = new ArrayList<>();
        startedProcessingEvents = new ArrayList<>();
        eventsInProgress = new ArrayList<>();

        queue.forEach(allEvents::add);
        for (var server : servers) {
            if (server.getCurrentProcessing() != null) {
                eventsInProgress.add(server.getCurrentProcessing());
                allEvents.add(server.getCurrentProcessing());
                startedProcessingEvents.add(server.getCurrentProcessing());
            }
        }
        departedCollector.forEach(departedEvents::add);
        departedCollector.forEach(allEvents::add);
        departedCollector.forEach(startedProcessingEvents::add);
        allEvents.sort(Comparator.comparingInt(SimulationEvent::getArrivalTime));

        {
            var sum = 0;
            for (var i = 1; i < allEvents.size(); i += 1) {
                sum += allEvents.get(i).getArrivalTime() - allEvents.get(i - 1).getArrivalTime();
            }
            averageInterArrivalTime = sum / (double) (allEvents.size() - 1);
        }
        averageWaitingTime = startedProcessingEvents.stream().mapToInt(SimulationEvent::getDelayInQueue).average().getAsDouble();
        averageServiceTime = departedEvents.stream().mapToInt(SimulationEvent::getServiceTime).sum() / (double) departedEvents.size();
        serversUsagePercentages = servers.stream().map(s -> (double) s.getBusyTime() / clock.getTimeElapsed()).collect(Collectors.toUnmodifiableList());
    }

    public double getAverageQueueSize() {
        return statisticsCollector.getAverageQueueSize();
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTimeInSystem() {
        return averageWaitingTime + averageInterArrivalTime;
    }

    public List<Double> getServersUsagePercentages() {
        return serversUsagePercentages;
    }

    public double getAverageSimultaneousEvents() {
        return getServersUsagePercentages().stream().mapToDouble(v -> v).sum() + getAverageQueueSize();
    }

    public void printSimulationMetrics(PrintStream out) {
        calculateStatistics();
        out.printf("Average inter-arrival time: %f minutes.%n", averageInterArrivalTime);
        out.printf("Average event waiting time in queue (before processing starts): %f minutes.%n", averageWaitingTime);
        out.printf("Average service time: %f minutes.%n", averageServiceTime);
        out.println();
        out.printf("Average queue size: %f.%n", getAverageQueueSize());
        out.printf("Average servers utilization (%%): %f (%s).%n", serversUsagePercentages.stream().mapToDouble(v -> v * 100).average().getAsDouble(), serversUsagePercentages.stream().map(v -> String.valueOf(v * 100)).collect(Collectors.joining(", ")));
        out.println();
        out.printf("Total time required: %d minutes.%n", clock.getTicksElapsed());
        out.printf("Total events produced: %d.%n", queueProducer.getEventsProduced());
        out.printf("Total processed (departed) events: %d.%n", departedCollector.eventsCount());
        out.println();
        out.printf("Current events in progress (being processed by servers): %d.%n", eventsInProgress.size());
        out.printf("Current events in queue (waiting for processing by servers): %d.%n", queue.size());
    }

    public void printEvents(PrintStream out) {
        calculateStatistics();
        for (var event : allEvents) {
            out.println(event.toReadableString());
        }
    }
}
