package org.glukit.sync.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;

/**
 * Represents a loosely defined exercise session.
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class ExerciseSession extends TimestampedEvent {
  public static final String EMPTY_DESCRIPTION = "";

  private Duration duration;
  private Intensity intensity;
  private String description;

  public ExerciseSession(Instant internalTime,
                         LocalDateTime localRecordedTime,
                         LocalDateTime eventLocalTime,
                         Intensity intensity,
                         Duration duration,
                         String description) {
    super(internalTime, localRecordedTime, eventLocalTime);
    this.duration = duration;
    this.intensity = intensity;
    this.description = description;
  }

  public Duration getDuration() {
    return duration;
  }

  public LocalDateTime getLocalEventTime() {
    return this.localTime;
  }

  public Intensity getIntensity() {
    return intensity;
  }

  public String getDescription() {
    return description;
  }

  public static enum Intensity {
    LIGHT, MEDIUM, HEAVY
  }
}
