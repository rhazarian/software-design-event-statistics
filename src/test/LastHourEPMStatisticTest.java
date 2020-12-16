package test;

import clock.EmulatedClock;
import org.junit.Test;
import statistics.EventsStatistic;
import statistics.LastHourEPMStatistic;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LastHourEPMStatisticTest {
    private final EmulatedClock clock = new EmulatedClock(Instant.now());
    private final EventsStatistic statistic = new LastHourEPMStatistic(clock);

    @Test
    public void testBasic() {
        statistic.incEvent("1");
        statistic.incEvent("2");
        statistic.incEvent("2");

        assertThat(statistic.getAllEventStatistic(), is(new HashMap<>() {{
            put("1", 1 / 60D);
            put("2", 2 / 60D);
        }}));
    }

    @Test
    public void testNonExistentEvent() {
        statistic.incEvent("1");
        statistic.incEvent("2");
        statistic.incEvent("2");

        assertThat(statistic.getEventStatisticByName("3"), is(0D));
    }

    @Test
    public void testExpiredEvents() {
        statistic.incEvent("1");
        statistic.incEvent("2");
        statistic.incEvent("2");

        clock.plus(Duration.ofHours(2));

        assertThat(statistic.getAllEventStatistic(), is(Collections.emptyMap()));
    }

    @Test
    public void testPartiallyExpiredEvents() {
        statistic.incEvent("1");
        clock.plus(Duration.ofMinutes(45));
        statistic.incEvent("2");

        assertThat(statistic.getAllEventStatistic(), is(new HashMap<>() {{
            put("1", 1 / 60D);
            put("2", 1 / 60D);
        }}));

        clock.plus(Duration.ofMinutes(45));
        statistic.incEvent("2");

        assertThat(statistic.getAllEventStatistic(), is(new HashMap<>() {{
            put("2", 2 / 60D);
        }}));

        clock.plus(Duration.ofMinutes(16));

        assertThat(statistic.getAllEventStatistic(), is(new HashMap<>() {{
            put("2", 1 / 60D);
        }}));
    }
}
