package se.lnu._4dv650.bs222sa.simulation;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class Main {
    private static final int repetitionCount = 12;
    private static final double percentageStudentsDistributionPoints = 2.2;
    private static PrintStream out = System.out;
    private static ArrayList<SimulationRun> simulations;

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                out = new PrintStream(args[0]);
            } catch (FileNotFoundException e) {
                System.err.printf("Failed to open output file %s!%n", args[0]);
                e.printStackTrace(System.err);
            }
        }
        simulations = new ArrayList<SimulationRun>();
        for (int i = 0; i < repetitionCount; i += 1) {
            var simulation = new SimulationRun();
            simulations.add(simulation);
            out.printf("Repetition #%d.%n", i + 1);
            System.out.println("Started simulation...");
            simulation.run();
            System.out.println("Finished simulation. Counting metrics...");
            simulation.calculateStatistics();
            simulation.printEvents(out);
            out.println();
            simulation.printSimulationMetrics(out);
            out.println();
        }

        final var sampleAverageNumberInQueue = getSampleAverage(SimulationRun::getAverageQueueSize);
        final var sampleAverageDelayInQueue = getSampleAverage(SimulationRun::getAverageWaitingTime);
        final var sampleAverageTimeSpentInSystem = getSampleAverage(SimulationRun::getAverageTimeInSystem);
        final var sampleAverageSimultaneousEventsInSystem = getSampleAverage(SimulationRun::getAverageSimultaneousEvents);

        out.printf("Lq - Total Average Number in Queue %f.%n", sampleAverageNumberInQueue);
        out.printf("Wq - Total Average Delay in Queue %f.%n", sampleAverageDelayInQueue);
        out.printf("w - Total Average Time Spent in System %f minutes.%n", sampleAverageTimeSpentInSystem);
        out.printf("L - Total Average # of Simultaneous Events in System %f.%n", sampleAverageSimultaneousEventsInSystem);

        final var squaredSampleVarianceNumberInQueue = getSquaredSampleVariance(SimulationRun::getAverageQueueSize, sampleAverageNumberInQueue);
        final var squaredSampleVarianceDelayInQueue = getSquaredSampleVariance(SimulationRun::getAverageWaitingTime, sampleAverageDelayInQueue);
        final var squaredSampleVarianceTimeSpentInSystem = getSquaredSampleVariance(SimulationRun::getAverageTimeInSystem, sampleAverageTimeSpentInSystem);
        final var squaredSampleVarianceSimultaneousEventsInSystem = getSquaredSampleVariance(SimulationRun::getAverageSimultaneousEvents, sampleAverageSimultaneousEventsInSystem);

        out.println();
        out.printf("Sample Variance Number in Queue (S^2) %f.%n", getSquaredSampleVariance(SimulationRun::getAverageQueueSize, sampleAverageNumberInQueue));
        out.printf("Sample Variance Delay in Queue (S^2) %f.%n", getSquaredSampleVariance(SimulationRun::getAverageWaitingTime, sampleAverageDelayInQueue));
        out.printf("Sample Variance Time Spent in System (S^2) %f minutes.%n", getSquaredSampleVariance(SimulationRun::getAverageTimeInSystem, sampleAverageTimeSpentInSystem));
        out.printf("Sample Variance # of Simultaneous Events in System (S^2) %f.%n", getSquaredSampleVariance(SimulationRun::getAverageSimultaneousEvents, sampleAverageSimultaneousEventsInSystem));

        out.println();
        out.printf("Confidence Interval Number in Queue %s.%n", getConfidenceIntervalAsString(sampleAverageNumberInQueue));
        out.printf("Confidence Interval Delay in Queue %s.%n", getConfidenceIntervalAsString(sampleAverageDelayInQueue));
        out.printf("Confidence Interval Time Spent in System %s minutes.%n", getConfidenceIntervalAsString(sampleAverageTimeSpentInSystem));
        out.printf("Confidence Interval # of Simultaneous Events in System %s.%n", getConfidenceIntervalAsString(sampleAverageSimultaneousEventsInSystem));
    }

    private static double getSampleAverage(ToDoubleFunction<SimulationRun> getAverageValue) {
        return 1.0 / (simulations.size() - 1) * simulations.stream().mapToDouble(getAverageValue).sum();
    }

    private static double getSquaredSampleVariance(ToDoubleFunction<SimulationRun> getAverageValue, double sampleAverage) {
        return 1.0 / (simulations.size() - 1) * simulations.stream().mapToDouble(s -> Math.pow(getAverageValue.applyAsDouble(s) - sampleAverage, 2)).sum();
    }

    private static List<Double> getConfidenceInterval(double sampleAverage) {
        var halfWidth = percentageStudentsDistributionPoints * Math.sqrt(sampleAverage) / Math.sqrt(repetitionCount);
        return List.of(sampleAverage - halfWidth, sampleAverage + halfWidth);
    }

    private static String getConfidenceIntervalAsString(double sampleAverage) {
        return '[' + getConfidenceInterval(sampleAverage).stream().map(String::valueOf).collect(Collectors.joining(", ")) + ']';
    }
}
