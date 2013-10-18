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
// TODO review this ambiguation of internal vs display vs event times
public class ExerciseSession extends TimestampedEvent {
  private Duration duration;
  private Intensity intensity;
  private String description;

  public ExerciseSession(Duration duration,
                         Instant internalTime,
                         LocalDateTime localEventTime,
                         Intensity intensity,
                         String description) {
    super(internalTime, localEventTime);
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
