package org.glukit.sync.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;

/**
 * Timestamped event. In addition to being a timestamped value, it has a {@link LocalDateTime} that is
 * associated with the event.
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class TimestampedEvent extends TimestampedValue {
  private LocalDateTime eventTime;

  public TimestampedEvent(Instant internalTime, LocalDateTime localRecordedTime, LocalDateTime eventLocalTime) {
    super(internalTime, localRecordedTime);
    this.eventTime = eventLocalTime;
  }

  public LocalDateTime getEventLocalTime() {
    return this.eventTime;
  }
}
