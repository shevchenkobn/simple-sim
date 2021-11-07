package se.lnu._4dv650.bs222sa.simulation;

import se.lnu._4dv650.bs222sa.simulation.components.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final int timeTickSize = 1;
    private static final int arrivalInterval = 10;
    private static final int serviceTime = 15;

    private static PrintStream out = System.out;

    private static EventQueue queue;
    private static Clock clock;
    private static SimulationRandom random;
    private static QueueProducer queueProducer;
    private static List<StringPullServer> servers;
    private static DepartedCollector departedCollector;
    private static StatisticsCollector statisticsCollector;
    private static ClockRunner clockRunner;

    private static ArrayList<SimulationEvent> allEvents;
    private static ArrayList<SimulationEvent> departedEvents;
    private static ArrayList<SimulationEvent> startedProcessingEvents;
    private static ArrayList<SimulationEvent> eventsInProgress;

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                out = new PrintStream(args[0]);
            } catch (FileNotFoundException e) {
                System.err.printf("Failed to open output file %s!%n", args[0]);
                e.printStackTrace(System.err);
            }
        }
        initializeSimulation();
        System.out.println("Started simulation...");
        runSimulation();
        System.out.println("Finished simulation. Counting metrics...");
        groupEvents();
        printSimulationMetrics();
    }

    private static void initializeSimulation() {
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

    private static void runSimulation() {
//        while (departedCollector.eventsCount() <= 1e6) {
        while (clock.getCurrentTime() < 22e3) {
            clockRunner.tick();
        }
    }

    private static void groupEvents() {
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
    }

    private static void printSimulationMetrics() {
        {
            allEvents.sort(Comparator.comparingInt(SimulationEvent::getArrivalTime));
            var sum = 0;
            for (var i = 1; i < allEvents.size(); i += 1) {
                sum += allEvents.get(i).getArrivalTime() - allEvents.get(i - 1).getArrivalTime();
            }
            out.printf("Average inter-arrival time: %f minutes.%n", sum / (double) (allEvents.size() - 1));
        }
        out.printf("Average event waiting time in queue (before processing starts): %f minutes.%n", startedProcessingEvents.stream().mapToInt(SimulationEvent::getDelayInQueue).average().getAsDouble());
        out.printf("Average service time: %f minutes.%n", departedEvents.stream().mapToInt(SimulationEvent::getServiceTime).sum() / (double) departedEvents.size());
        out.println();
        out.printf("Average queue size: %f.%n", statisticsCollector.getAverageQueueSize());
        out.printf("Average servers utilization (%%): %f (%s).%n", servers.stream().mapToDouble(Main::getServerUtilization).average().getAsDouble(), servers.stream().map(Main::getServerUtilization).map(String::valueOf).collect(Collectors.joining(", ")));
        out.println();
        out.printf("Total time required: %d minutes.%n", clock.getTicksElapsed());
        out.printf("Total events produced: %d.%n", queueProducer.getEventsProduced());
        out.printf("Total processed (departed) events: %d.%n", departedCollector.eventsCount());
        out.println();
        out.printf("Current events in progress (being processed by servers): %d.%n", eventsInProgress.size());
        out.printf("Current events in queue (waiting for processing by servers): %d.%n", queue.size());
    }

    private static Double getServerUtilization(StringPullServer s) {
        return (double) s.getBusyTime() / clock.getTimeElapsed() * 100;
    }
}
