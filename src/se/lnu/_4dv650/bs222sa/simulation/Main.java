package se.lnu._4dv650.bs222sa.simulation;

import se.lnu._4dv650.bs222sa.simulation.components.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        final var arrivalInterval = 10;
        final var serviceTime = 15;

        var queue = new EventQueue();
	    var clock = new Clock(0, 1);
        var random = new SimulationRandom();
        var departedCollector = new DepartedCollector();

        var servers = Arrays.asList(
                new StringPullServer(random, serviceTime, queue, departedCollector),
                new StringPullServer(random, serviceTime, queue, departedCollector)
        );

        var clockRunner = new ClockRunner(clock, Stream.concat(Stream.of(
                new QueueProducer(random, arrivalInterval, queue)
        ), servers.stream()).collect(Collectors.toList()));

        while (clock.getCurrentTime() < 22000) {
            clockRunner.tick();
        }

        var allEvents = new ArrayList<SimulationEvent>();
        var departed = new ArrayList<SimulationEvent>();
        var startedProcessing = new ArrayList<SimulationEvent>();
        queue.forEach(allEvents::add);
        for (var server : servers) {
            if (server.getCurrentProcessing() != null) {
                allEvents.add(server.getCurrentProcessing());
                startedProcessing.add(server.getCurrentProcessing());
            }
        }
        departedCollector.forEach(departed::add);
        departedCollector.forEach(allEvents::add);
        departedCollector.forEach(startedProcessing::add);

        {
            allEvents.sort((a, b) -> a.getArrivalTime() - b.getArrivalTime());
            var sum = 0;
            for (var i = 1; i < allEvents.size(); i += 1) {
                sum += allEvents.get(i).getArrivalTime() - allEvents.get(i - 1).getArrivalTime();
            }
            System.out.println(String.format("Average arrival time: %f minutes.", sum / (double)(allEvents.size() - 1)));
        }
        System.out.println(String.format("Average time in queue: %f minutes.", startedProcessing.stream().mapToInt(SimulationEvent::getDelayInQueue).sum() / (double)startedProcessing.size()));
        System.out.println(String.format("Average service time: %f minutes.", departed.stream().mapToInt(SimulationEvent::getServiceTime).sum() / (double)departed.size()));
    }
}
