package org.glukit.dexcom.sync.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.glukit.sync.api.ReceiverSyncData;
import org.threeten.bp.Instant;

import java.util.List;

/**
 * Represents the full set of data from a sync on a {@link org.glukit.dexcom.sync.g4.DexcomG4DeviceFilter}
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode
public class DexcomSyncData implements ReceiverSyncData {
  private List<GlucoseReadRecord> glucoseReads;
  private List<UserEventRecord> userEvents;
  private ManufacturingParameters manufacturingParameters;
  private Instant syncTime;

  public DexcomSyncData(List<GlucoseReadRecord> glucoseReads,
                        List<UserEventRecord> userEvents,
                        ManufacturingParameters manufacturingParameters) {
    this.glucoseReads = glucoseReads;
    this.userEvents = userEvents;
    this.manufacturingParameters = manufacturingParameters;
    this.syncTime = Instant.now();
  }

  public DexcomSyncData(List<GlucoseReadRecord> glucoseReads,
                        List<UserEventRecord> userEvents,
                        ManufacturingParameters manufacturingParameters,
                        Instant updateTime) {
      this.glucoseReads = glucoseReads;
      this.userEvents = userEvents;
      this.manufacturingParameters = manufacturingParameters;
      this.syncTime = updateTime;
    }

  public List<GlucoseReadRecord> getGlucoseReads() {
    return glucoseReads;
  }

  public ManufacturingParameters getManufacturingParameters() {
    return manufacturingParameters;
  }

  public List<UserEventRecord> getUserEvents() {
    return userEvents;
  }

  @Override
  public Instant getUpdateTime() {
    return syncTime;
  }
}
