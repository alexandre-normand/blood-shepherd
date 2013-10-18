package org.glukit.sync.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;

/**
 * Represents a food event (usually, eating carbs but proteins are important too!).
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class FoodEvent extends TimestampedEvent {
  private float carbohydrates;
  private float proteins;

  public FoodEvent(Instant internalTime,
                   LocalDateTime localRecordedTime,
                   LocalDateTime eventLocalTime,
                   float carbohydrates,
                   float proteins) {
    super(internalTime, localRecordedTime, eventLocalTime);
    this.carbohydrates = carbohydrates;
    this.proteins = proteins;
  }

  public float getCarbohydrates() {
    return carbohydrates;
  }

  public float getProteins() {
    return proteins;
  }
}
