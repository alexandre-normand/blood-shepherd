package org.glukit.sync.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.threeten.bp.Instant;

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
  private Instant updateTime;

  public SyncData(List<GlucoseRead> glucoseReads,
                  List<InsulinInjection> insulinInjections,
                  List<FoodEvent> foodEvents,
                  List<ExerciseSession> exerciseSessions,
                  DeviceInfo deviceInfo,
                  Instant updateTime) {
    this.glucoseReads = glucoseReads;
    this.insulinInjections = insulinInjections;
    this.foodEvents = foodEvents;
    this.exerciseSessions = exerciseSessions;
    this.deviceInfo = deviceInfo;
    this.updateTime = updateTime;
  }

  public List<GlucoseRead> getGlucoseReads() {
    return glucoseReads;
  }

  public List<InsulinInjection> getInsulinInjections() {
    return insulinInjections;
  }

  public List<FoodEvent> getFoodEvents() {
    return foodEvents;
  }

  public List<ExerciseSession> getExerciseSessions() {
    return exerciseSessions;
  }

  public DeviceInfo getDeviceInfo() {
    return deviceInfo;
  }

  public Instant getUpdateTime() {
    return updateTime;
  }
}

