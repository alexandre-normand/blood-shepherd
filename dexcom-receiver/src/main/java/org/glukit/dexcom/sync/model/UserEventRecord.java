package org.glukit.dexcom.sync.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Represents a record of {@link RecordType#UserEventData}.
 *
 * @author alexandre.normand
 */
@ToString
@EqualsAndHashCode
public class UserEventRecord {
  public static final int RECORD_LENGTH = 20;

  private long internalSecondsSinceDexcomEpoch;
  private long localSecondsSinceDexcomEpoch;
  private UserEventType eventType;
  private byte eventSubType;
  private long eventSecondsSinceDexcomEpoch;
  private long eventValue;

  public UserEventRecord(long internalSecondsSinceDexcomEpoch,
                         long localSecondsSinceDexcomEpoch,
                         long eventSecondsSinceDexcomEpoch,
                         UserEventType eventType,
                         byte eventSubType,
                         long eventValue) {
    this.internalSecondsSinceDexcomEpoch = internalSecondsSinceDexcomEpoch;
    this.localSecondsSinceDexcomEpoch = localSecondsSinceDexcomEpoch;
    this.eventType = eventType;
    this.eventSubType = eventSubType;
    this.eventSecondsSinceDexcomEpoch = eventSecondsSinceDexcomEpoch;
    this.eventValue = eventValue;
  }

  public long getInternalSecondsSinceDexcomEpoch() {
    return internalSecondsSinceDexcomEpoch;
  }

  public long getLocalSecondsSinceDexcomEpoch() {
    return localSecondsSinceDexcomEpoch;
  }

  public UserEventType getEventType() {
    return eventType;
  }

  public byte getEventSubType() {
    return eventSubType;
  }

  public long getEventSecondsSinceDexcomEpoch() {
    return eventSecondsSinceDexcomEpoch;
  }

  public long getEventValue() {
    return eventValue;
  }

  public static enum UserEventType {
    CARBS((byte) 1),
    EXERCISE((byte) 4),
    HEALTH((byte) 3),
    INSULIN((byte) 2),
    MaxValue((byte) 5),
    NullType((byte) 0);

    private byte id;
    private static Map<Byte, UserEventType> mappings;

    private UserEventType(byte id) {
      this.id = id;
      addMapping(id, this);
    }

    private static void addMapping(byte id, UserEventType recordType) {
      if (mappings == null) {
        mappings = newHashMap();
      }
      mappings.put(id, recordType);
    }

    public static UserEventType fromId(byte id) {
      return mappings.get(id);
    }

    public byte getId() {
      return id;
    }
  }

  public static enum ExerciseIntensity {
    HEAVY((byte) 3),
    LIGHT((byte) 1),
    MaxValue((byte) 4),
    MEDIUM((byte) 2),
    Null((byte) 0);

    private byte id;
    private static Map<Byte, ExerciseIntensity> mappings;

    private ExerciseIntensity(byte id) {
      this.id = id;
      addMapping(id, this);
    }

    private static void addMapping(byte id, ExerciseIntensity recordType) {
      if (mappings == null) {
        mappings = newHashMap();
      }
      mappings.put(id, recordType);
    }

    public static ExerciseIntensity fromId(byte id) {
      return mappings.get(id);
    }

    public byte getId() {
      return id;
    }
  }
}
