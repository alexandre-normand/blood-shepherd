package org.glukit.dexcom.sync.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Represents the full set of data from a sync on a {@link org.glukit.dexcom.sync.g4.DexcomG4DeviceFilter}
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode
public class DexcomSyncData {
  private List<GlucoseReadRecord> glucoseReads;
  private ManufacturingParameters manufacturingParameters;

  public DexcomSyncData(List<GlucoseReadRecord> glucoseReads, ManufacturingParameters manufacturingParameters) {
    this.glucoseReads = glucoseReads;
    this.manufacturingParameters = manufacturingParameters;
  }

  public List<GlucoseReadRecord> getGlucoseReads() {
    return glucoseReads;
  }

  public ManufacturingParameters getManufacturingParameters() {
    return manufacturingParameters;
  }
}
