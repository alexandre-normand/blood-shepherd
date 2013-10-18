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
  private List<InsulinInjection> insulinInjections;
  private List<FoodEvent> foodEvents;
  private List<ExerciseSession> exerciseSessions;
  private DeviceInfo deviceInfo;

  public SyncData(List<GlucoseRead> glucoseReads,
                  List<InsulinInjection> insulinInjections,
                  List<FoodEvent> foodEvents,
                  List<ExerciseSession> exerciseSessions,
                  DeviceInfo deviceInfo) {
    this.glucoseReads = glucoseReads;
    this.insulinInjections = insulinInjections;
    this.foodEvents = foodEvents;
    this.exerciseSessions = exerciseSessions;
    this.deviceInfo = deviceInfo;
  }

  public List<GlucoseRead> getGlucoseReads() {
    return glucoseReads;
  }

  public DeviceInfo getDeviceInfo() {
    return deviceInfo;
  }
}

