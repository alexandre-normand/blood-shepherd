package org.glukit.sync.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;

/**
 * Represents an insulin injection.
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class InsulinInjection extends TimestampedEvent {
  public static final String UNAVAILABLE_INSULIN_NAME = "N/A";

  private String insulinName;
  private InsulinType insulinType;
  private float unitValue;

  public InsulinInjection(Instant internalTime,
                          LocalDateTime localRecordedTime,
                          LocalDateTime localEventTime,
                          float unitValue,
                          InsulinType insulinType,
                          String insulinName) {
    super(internalTime, localRecordedTime, localEventTime);
    this.insulinName = insulinName;
    this.insulinType = insulinType;
    this.unitValue = unitValue;
  }

  public String getInsulinName() {
    return insulinName;
  }

  public InsulinType getInsulinType() {
    return insulinType;
  }

  public float getUnitValue() {
    return unitValue;
  }

  public static enum InsulinType {
    SLOW_ACTING, FAST_ACTING, UNKNOWN
  }
}
