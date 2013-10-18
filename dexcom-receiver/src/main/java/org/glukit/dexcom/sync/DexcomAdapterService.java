package org.glukit.dexcom.sync;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.glukit.dexcom.sync.g4.DexcomG4Constants;
import org.glukit.dexcom.sync.model.DexcomSyncData;
import org.glukit.dexcom.sync.model.GlucoseReadRecord;
import org.glukit.dexcom.sync.model.ManufacturingParameters;
import org.glukit.dexcom.sync.model.UserEventRecord;
import org.glukit.sync.AdapterService;
import org.glukit.sync.api.*;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static org.glukit.dexcom.sync.model.UserEventRecord.UserEventType.CARBS;
import static org.glukit.dexcom.sync.model.UserEventRecord.UserEventType.EXERCISE;
import static org.glukit.dexcom.sync.model.UserEventRecord.UserEventType.INSULIN;
import static org.glukit.sync.api.InsulinInjection.InsulinType.UNKNOWN;

/**
 * This service adapts Dexcom-specific physical models to higher-level models.
 *
 * @author alexandre.normand
 */
public class DexcomAdapterService implements AdapterService<DexcomSyncData> {

  private static final int GLUCOSE_DISPLAY_ONLY_MASK = 0x8000;
  private static final int GLUCOSE_READ_VALUE_MASK = 0x3ff;

  private static final float INVALID_GLUCOSE_RECORD_VALUE = -1.0f;
  public static final Predicate<GlucoseRead> VALID_READS_FILTER = new Predicate<GlucoseRead>() {
    @Override
    public boolean apply(@Nullable GlucoseRead input) {
      return input.getValue() != INVALID_GLUCOSE_RECORD_VALUE;
    }
  };

  static final List<Integer> SPECIAL_GLUCOSE_VALUES = Arrays.asList(0, 1, 2, 3, 5, 6, 9, 10, 12);

  private static final Predicate<UserEventRecord> INSULIN_EVENT_FILTER = new Predicate<UserEventRecord>() {
    @Override
    public boolean apply(@Nullable UserEventRecord input) {
      return input.getEventType() == INSULIN;
    }
  };

  private static final Predicate<UserEventRecord> EXERCISE_EVENT_FILTER = new Predicate<UserEventRecord>() {
    @Override
    public boolean apply(@Nullable UserEventRecord input) {
      return input.getEventType() == EXERCISE;
    }
  };

  private static final Predicate<UserEventRecord> CARB_EVENT_FILTER = new Predicate<UserEventRecord>() {
    @Override
    public boolean apply(@Nullable UserEventRecord input) {
      return input.getEventType() == CARBS;
    }
  };

  private Function<Long, Instant> DEXCOM_SYSTEM_TIME_TO_INSTANT = new Function<Long, Instant>() {
    @Nullable
    @Override
    public Instant apply(@Nullable Long secondsSinceDexcomEpoch) {
      checkNotNull(secondsSinceDexcomEpoch, "secondsSinceDexcomEpoch should be non-null.");
      return DexcomG4Constants.DEXCOM_EPOCH.plusSeconds(secondsSinceDexcomEpoch);
    }
  };

  private Function<Long, LocalDateTime> DEXCOM_DISPLAY_TIME_TO_LOCAL_DATE_TIME = new Function<Long, LocalDateTime>() {
    @Nullable
    @Override
    public LocalDateTime apply(@Nullable Long secondsSinceDexcomEpoch) {
      checkNotNull(secondsSinceDexcomEpoch, "secondsSinceDexcomEpoch should be non-null.");
      Instant instantInUTC = DexcomG4Constants.DEXCOM_EPOCH.plusSeconds(secondsSinceDexcomEpoch);
      return LocalDateTime.ofInstant(instantInUTC, ZoneId.of("UTC"));
    }
  };

  private Function<Integer, Float> DEXCOM_GLUCOSE_VALUE_TO_GLUCOSE_VALUE =
      new Function<Integer, Float>() {
        @Nullable
        @Override
        public Float apply(@Nullable Integer readValue) {
          checkNotNull(readValue, "readValue should be non-null.");

          boolean isDisplayOnly = (readValue & GLUCOSE_DISPLAY_ONLY_MASK) != 0;

          if (isDisplayOnly) {
            return INVALID_GLUCOSE_RECORD_VALUE;
          } else {
            int actualValue = readValue & GLUCOSE_READ_VALUE_MASK;
            if (SPECIAL_GLUCOSE_VALUES.contains(actualValue)) {
              return INVALID_GLUCOSE_RECORD_VALUE;
            } else {
              return readValue.floatValue();
            }
          }
        }
      };

  private Function<GlucoseReadRecord, GlucoseRead> DEXCOM_GLUCOSE_RECORD_TO_GLUCOSE_READ =
      new Function<GlucoseReadRecord, GlucoseRead>() {
        @Override
        public GlucoseRead apply(@javax.annotation.Nullable GlucoseReadRecord glucoseReadRecord) {
          checkNotNull(glucoseReadRecord, "glucoseReadRecord should be non-null");

          Instant internalTimeUTC =
              DEXCOM_SYSTEM_TIME_TO_INSTANT.apply(glucoseReadRecord.getInternalSecondsSinceDexcomEpoch());
          LocalDateTime displayTime =
              DEXCOM_DISPLAY_TIME_TO_LOCAL_DATE_TIME.apply(glucoseReadRecord.getLocalSecondsSinceDexcomEpoch());

          float glucoseValue =
              DEXCOM_GLUCOSE_VALUE_TO_GLUCOSE_VALUE.apply(glucoseReadRecord.getGlucoseValueWithFlags());

          // TODO: remove the hardcoded unit and replace by the actual unit as per the configuration settings of the
          // receiver
          return new GlucoseRead(internalTimeUTC, displayTime, glucoseValue, GlucoseRead.Unit.MG_PER_DL);
        }
      };

  private Function<ManufacturingParameters, DeviceInfo> DEXCOM_MANUFACTURING_PARAMS_TO_DEVICE_INFO =
      new Function<ManufacturingParameters, DeviceInfo>() {
        @Override
        public DeviceInfo apply(@javax.annotation.Nullable ManufacturingParameters manufacturingParameters) {
          checkNotNull(manufacturingParameters, "manufacturingParameters should be non-null");

          return new DeviceInfo(manufacturingParameters.getSerialNumber(),  manufacturingParameters.getHardwareId(),
              manufacturingParameters.getHardwareRevision());
        }
      };

  private Function<UserEventRecord, InsulinInjection> USER_EVENT_RECORD_TO_INSULIN_INJECTION =
      new Function<UserEventRecord, InsulinInjection>() {
        @Override
        public InsulinInjection apply(@javax.annotation.Nullable UserEventRecord insulinEvent) {
          checkNotNull(insulinEvent, "insulinEvent should be non-null");
          checkArgument(insulinEvent.getEventType() == INSULIN);

          Instant internalTimeUTC =
              DEXCOM_SYSTEM_TIME_TO_INSTANT.apply(insulinEvent.getInternalSecondsSinceDexcomEpoch());
          LocalDateTime localRecordedTime =
              DEXCOM_DISPLAY_TIME_TO_LOCAL_DATE_TIME.apply(insulinEvent.getLocalSecondsSinceDexcomEpoch());
          LocalDateTime eventLocalTime =
              DEXCOM_DISPLAY_TIME_TO_LOCAL_DATE_TIME.apply(insulinEvent.getEventSecondsSinceDexcomEpoch());

          float unitValue = insulinEvent.getEventValue() / 100.f;

          return new InsulinInjection(internalTimeUTC, localRecordedTime, eventLocalTime, unitValue, UNKNOWN, "N/A");
        }
      };

  private Function<UserEventRecord, FoodEvent> USER_EVENT_RECORD_TO_FOOD_EVENT =
      new Function<UserEventRecord, FoodEvent>() {
        @Override
        public FoodEvent apply(@javax.annotation.Nullable UserEventRecord carbEvent) {
          checkNotNull(carbEvent, "insulinEvent should be non-null");
          checkArgument(carbEvent.getEventType() == CARBS);

          Instant internalTimeUTC =
              DEXCOM_SYSTEM_TIME_TO_INSTANT.apply(carbEvent.getInternalSecondsSinceDexcomEpoch());
          LocalDateTime localRecordedTime =
              DEXCOM_DISPLAY_TIME_TO_LOCAL_DATE_TIME.apply(carbEvent.getLocalSecondsSinceDexcomEpoch());
          LocalDateTime eventLocalTime =
              DEXCOM_DISPLAY_TIME_TO_LOCAL_DATE_TIME.apply(carbEvent.getEventSecondsSinceDexcomEpoch());

          float unitValue = carbEvent.getEventValue();

          return new FoodEvent(internalTimeUTC, localRecordedTime, eventLocalTime, unitValue, 0f);
        }
      };

  private Function<UserEventRecord.ExerciseIntensity, ExerciseSession.Intensity> DEXCOM_EXERCISE_INTENSITY_TO_INTENSITY = new Function<UserEventRecord.ExerciseIntensity, ExerciseSession.Intensity>() {
    @Nullable
    @Override
    public ExerciseSession.Intensity apply(@Nullable UserEventRecord.ExerciseIntensity exerciseIntensity) {
      checkNotNull(exerciseIntensity, "exerciseIntensity should be non-null");

      switch (exerciseIntensity) {
        case LIGHT:
          return ExerciseSession.Intensity.LIGHT;
        case MEDIUM:
          return ExerciseSession.Intensity.MEDIUM;
        case HEAVY:
          return ExerciseSession.Intensity.HEAVY;
        default:
          return null;
      }
    }
  };

  private Function<UserEventRecord, ExerciseSession> USER_EVENT_RECORD_TO_EXERCISE_SESSION =
      new Function<UserEventRecord, ExerciseSession>() {
        @Override
        public ExerciseSession apply(@javax.annotation.Nullable UserEventRecord exerciseSession) {
          checkNotNull(exerciseSession, "exerciseSession should be non-null");
          checkArgument(exerciseSession.getEventType() == EXERCISE);

          Instant internalTimeUTC =
              DEXCOM_SYSTEM_TIME_TO_INSTANT.apply(exerciseSession.getInternalSecondsSinceDexcomEpoch());
          LocalDateTime localRecordedTime =
              DEXCOM_DISPLAY_TIME_TO_LOCAL_DATE_TIME.apply(exerciseSession.getLocalSecondsSinceDexcomEpoch());
          LocalDateTime eventLocalTime =
              DEXCOM_DISPLAY_TIME_TO_LOCAL_DATE_TIME.apply(exerciseSession.getEventSecondsSinceDexcomEpoch());

          long duration = exerciseSession.getEventValue();

          UserEventRecord.ExerciseIntensity exerciseIntensity =
              UserEventRecord.ExerciseIntensity.fromId(exerciseSession.getEventSubType());
          ExerciseSession.Intensity intensity =
              DEXCOM_EXERCISE_INTENSITY_TO_INTENSITY.apply(exerciseIntensity);

          return new ExerciseSession(internalTimeUTC, localRecordedTime, eventLocalTime,
              intensity, Duration.ofMinutes(duration), "");
        }
      };


  @Override
  public SyncData convertData(DexcomSyncData source) {
    List<GlucoseRead> glucoseReads = newArrayList(Collections2.filter(Collections2.transform(source.getGlucoseReads(),
        DEXCOM_GLUCOSE_RECORD_TO_GLUCOSE_READ), VALID_READS_FILTER));

    DeviceInfo deviceInfo = DEXCOM_MANUFACTURING_PARAMS_TO_DEVICE_INFO.apply(source.getManufacturingParameters());

    Collection<UserEventRecord> insulinEvents = Collections2.filter(source.getUserEvents(), INSULIN_EVENT_FILTER);
    Collection<UserEventRecord> exerciseEvents = Collections2.filter(source.getUserEvents(), EXERCISE_EVENT_FILTER);
    Collection<UserEventRecord> carbEvents = Collections2.filter(source.getUserEvents(), CARB_EVENT_FILTER);

    List<InsulinInjection> injections =
        newArrayList(Collections2.transform(insulinEvents, USER_EVENT_RECORD_TO_INSULIN_INJECTION));
    List<ExerciseSession> exerciseSessions =
        newArrayList(Collections2.transform(exerciseEvents, USER_EVENT_RECORD_TO_EXERCISE_SESSION));
    List<FoodEvent> foodEvents =
        newArrayList(Collections2.transform(carbEvents, USER_EVENT_RECORD_TO_FOOD_EVENT));

    return new SyncData(glucoseReads, injections, foodEvents, exerciseSessions, deviceInfo);
  }
}
