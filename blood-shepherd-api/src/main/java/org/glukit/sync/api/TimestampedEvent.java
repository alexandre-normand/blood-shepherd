package org.glukit.sync.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;

/**
 * This represents a timestamped value. It implements {@link Comparable} so that all subclasses get proper ordering for
 * free.
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode
public abstract class TimestampedEvent implements Comparable<TimestampedEvent> {
  protected final Instant internalTime;
  protected final LocalDateTime localTime;

  public TimestampedEvent(Instant internalTime, LocalDateTime localTime) {
    this.internalTime = internalTime;
    this.localTime = localTime;
  }

  public Instant getInternalTime() {
    return internalTime;
  }

  public LocalDateTime getLocalTime() {
    return localTime;
  }

  @Override
  public int compareTo(TimestampedEvent other) {
    return other.internalTime.compareTo(this.internalTime);
  }
}
