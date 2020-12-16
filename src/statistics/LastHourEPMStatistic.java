package statistics;

import clock.Clock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class LastHourEPMStatistic implements EventsStatistic {
    static private class EventOccurrence {
        final String name;
        final Instant timestamp;

        private EventOccurrence(String name, Instant timestamp) {
            this.name = name;
            this.timestamp = timestamp;
        }
    }

    private static final double MINUTES_PER_HOUR = ChronoUnit.HOURS.getDuration().toMinutes();

    private final Clock clock;
    private final Queue<EventOccurrence> events = new ArrayDeque<>();
    private final Map<String, Long> eventCounts = new HashMap<>();

    public LastHourEPMStatistic(final Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        final Instant now = clock.now();
        clean(now);
        events.add(new EventOccurrence(name, now));
        eventCounts.put(name, eventCounts.getOrDefault(name, 0L) + 1);
    }

    @Override
    public double getEventStatisticByName(String name) {
        final Instant now = clock.now();
        clean(now);
        return eventCounts.getOrDefault(name, 0L) / MINUTES_PER_HOUR;
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        final Instant now = clock.now();
        clean(now);
        return eventCounts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / MINUTES_PER_HOUR));
    }

    @Override
    public void printStatistic() {
        eventCounts.forEach((name, count) -> System.out.println(name + ": " + count));
    }

    private void clean(Instant timestamp) {
        while (!events.isEmpty() && ChronoUnit.HOURS.between(events.peek().timestamp, timestamp) >= 1) {
            final EventOccurrence occurrence = events.poll();
            final long count = eventCounts.get(occurrence.name);
            if (count == 1) {
                eventCounts.remove(occurrence.name);
            } else if (count >= 2) {
                eventCounts.put(occurrence.name, count - 1);
            } else {
                throw new IllegalStateException("event counter should be positive");
            }
        }
    }
}
