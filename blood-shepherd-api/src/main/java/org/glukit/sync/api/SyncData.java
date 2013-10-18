package org.glukit.sync.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Sync data. It includes everything that has been received in a session.
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode
public class SyncData {
  private List<GlucoseRead> glucoseReads;
  private DeviceInfo deviceInfo;

  public SyncData(List<GlucoseRead> glucoseReads, DeviceInfo deviceInfo) {
    this.glucoseReads = glucoseReads;
    this.deviceInfo = deviceInfo;
  }

  public List<GlucoseRead> getGlucoseReads() {
    return glucoseReads;
  }

  public DeviceInfo getDeviceInfo() {
    return deviceInfo;
  }
}

