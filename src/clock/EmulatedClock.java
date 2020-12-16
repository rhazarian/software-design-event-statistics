package clock;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.atomic.AtomicReference;

public class EmulatedClock implements Clock {
    private final AtomicReference<Instant> now = new AtomicReference<>();

    public EmulatedClock(final Instant now) {
        this.now.set(now);
    }

    public void setNow(final Instant now) {
        this.now.set(now);
    }

    public void plus(final TemporalAmount amount) {
        now.updateAndGet(instant -> instant.plus(amount));
    }

    public void minus(final TemporalAmount amount) {
        now.updateAndGet(instant -> instant.minus(amount));
    }

    @Override
    public Instant now() {
        return now.get();
    }
}
