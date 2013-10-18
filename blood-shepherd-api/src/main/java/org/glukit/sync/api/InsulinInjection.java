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
  private String insulinName;
  private InsulinType insulinType;
  private float unitValue;

  public InsulinInjection(Instant internalTime,
                          LocalDateTime localTime,
                          float unitValue,
                          InsulinType insulinType,
                          String insulinName) {
    super(internalTime, localTime);
    this.insulinName = insulinName;
    this.insulinType = insulinType;
    this.unitValue = unitValue;
  }

  public static enum InsulinType {
    SLOW_ACTING, FAST_ACTING, UNKNOWN
  }
}
