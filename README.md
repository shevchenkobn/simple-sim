# Summary

This project uses vanilla Java 11 to implement a simulation.

## Simulation problem statement

At Metropolis City Hall, two workers “pull strings” (make deals) every day.
Strings arrive to be pulled on an average of one every 10 minutes throughout the day. It
takes an average of 15 minutes to pull a string. Both times between arrivals and service
times are exponentially distributed.

# Starting the project

Java must be preinstalled. Version (LTS version was used):

```shell
$ java --version
openjdk 11.0.11 2021-04-20
OpenJDK Runtime Environment (build 11.0.11+9-Ubuntu-0ubuntu2.20.04)
OpenJDK 64-Bit Server VM (build 11.0.11+9-Ubuntu-0ubuntu2.20.04, mixed mode, sharing)
```

The project is ready for use in JetBrains IDEA Community or JetBrains IDEA Ultimate (version 2021.2.3 was used).

To compile the program from project root CLI:
```shell
javac -d ./out/production/simple-sim/ ./src/se/lnu/_4dv650/bs222sa/simulation/**/*.java ./src/se/lnu/_4dv650/bs222sa/simulation/*.java
```

After compiling, the program can be run as (**the output is stdout**):
```shell
java -Dfile.encoding=UTF-8 -classpath ./out/production/simple-sim se.lnu._4dv650.bs222sa.simulation.Main
```

Or **the file can be used for output** (file name is the last CLI argument): 
```shell
java -Dfile.encoding=UTF-8 -classpath ./out/production/simple-sim se.lnu._4dv650.bs222sa.simulation.Main ./out/output.txt
```

# Project description

The parent package is `se.lnu._4dv650.bs222sa.simulation`.

There is a `components.*` subpackage, which contains all the types for simulation and a `se.lnu._4dv650.bs222sa.simulation.Main` class, which is an entry point class.

## Main class

The class contains constants and static fields for all the components.

The `main()` method:
1. Initializes simulation:
   1. Create `EventQueue` - storage for events to be processed;
   2. Create `Clock` - a clock with ability to do increase the time on-demand (tick the clock);
   3. Create `SimulationRandom` - a helper with random-number methods for simulation.
   4. Create `QueueProducer` - a class which produces the event to the aforementioned `EventQueue` and counts the produced events;
   5. Create `DepartedCollector` - a class for storing departed (already processed events) for further statistics calculation.
   6. Create several `StringPullServer` - a list of servers that will consume events from `EventQueue`, "process" it, and add it to `DepartedCollector`;
   7. Create `StatisticsCollector` - a class for calculating statistics (average queue size) after each clock tick;
   8. Create `ClockRunner` - a class that runs the actual simulation. It calls updates on each `Clock` tick.
2. Runs simulation - uses while loop to run the simulation until time limit or event limit is reached;
3. Collects and groups events from simulation for metrics calculation;
4. Prints statistics and metrics;

# Subpackage `.components` and ideas behind the design

For implementation 3 main ideas were used:
- _a major inspiration source_ - frame-by-frame rendering systems, which update the screen every "tick" or every `n` milliseconds (e.g. game engines, such as Unity and its game components). The identical approach was used for the main simulation loop, which calls the `ClockRunner.tick()`;
- a classical producer-consumer parallel programming problem. In this implementation only the main concept was used, not the original thread-based Java solution of this problem. `QueueProducer` is producer, `StringPullServer` is consumer.
- SOLID principles in code design.

Using a tick-based simulation together with SOLID principles allow creating a flexible code architecture, that is straightforward, easy-to-read, extensible and maintainable.

Calling `ClockRunner.tick()` updates the `Clock` and calls all `ClockRunnable` implementors which are `QueueProducer`, `StringPullServer` and `StatisticsCollector`. Interface `ClockRunnable` has a single method - `void updateOnTick(CurrentTime time);`, that is called after each `Clock` update.

`SimulationEvent` keeps track of state of event and event-related statistics, which includes `state` (an enum), `arrivalTime`, `serviceStartTime`, and `serviceFinishTime`. The simple state machine by `state` field and other 3 aforementioned fields allow quickly calculate different event metrics, such as service time and delay in queue.
