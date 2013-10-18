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

  public FoodEvent(float carbohydrates, float proteins, Instant internalTime, LocalDateTime localTime) {
    super(internalTime, localTime);
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
