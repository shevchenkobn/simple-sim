package se.lnu._4dv650.bs222sa.simulation;

import se.lnu._4dv650.bs222sa.simulation.components.*;

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

    private static EventQueue queue;
    private static Clock clock;
    private static SimulationRandom random;
    private static DepartedCollector departedCollector;
    private static List<StringPullServer> servers;
    private static ClockRunner clockRunner;

    private static ArrayList<SimulationEvent> allEvents;
    private static ArrayList<SimulationEvent> departedEvents;
    private static ArrayList<SimulationEvent> startedProcessingEvents;

    public static void main(String[] args) {
        initializeSimulation();
        runSimulation();
        groupEvents();
        printSimulationMetrics();
    }

    private static void initializeSimulation() {
        queue = new EventQueue();
        clock = new Clock(0, timeTickSize);
        random = new SimulationRandom();
        departedCollector = new DepartedCollector();

        servers = Arrays.asList(
                new StringPullServer(random, serviceTime, queue, departedCollector),
                new StringPullServer(random, serviceTime, queue, departedCollector)
        );

        clockRunner = new ClockRunner(clock, Stream.concat(Stream.of(
                new QueueProducer(random, arrivalInterval, queue)
        ), servers.stream()).collect(Collectors.toList()));
    }

    private static void runSimulation() {
        while (clock.getCurrentTime() < 22000) {
            clockRunner.tick();
        }
    }

    private static void groupEvents() {
        allEvents = new ArrayList<SimulationEvent>();
        departedEvents = new ArrayList<SimulationEvent>();
        startedProcessingEvents = new ArrayList<SimulationEvent>();
        queue.forEach(allEvents::add);
        for (var server : servers) {
            if (server.getCurrentProcessing() != null) {
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
            System.out.printf("Average arrival time: %f minutes.%n", sum / (double) (allEvents.size() - 1));
        }
        System.out.printf("Average time in queue: %f minutes.%n", startedProcessingEvents.stream().mapToInt(SimulationEvent::getDelayInQueue).sum() / (double) startedProcessingEvents.size());
        System.out.printf("Average service time: %f minutes.%n", departedEvents.stream().mapToInt(SimulationEvent::getServiceTime).sum() / (double) departedEvents.size());
    }
}
