package org.glukit.dexcom.sync;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.glukit.dexcom.sync.g4.DexcomG4Constants;
import org.glukit.dexcom.sync.model.DexcomSyncData;
import org.glukit.dexcom.sync.model.GlucoseReadRecord;
import org.glukit.dexcom.sync.model.ManufacturingParameters;
import org.glukit.sync.AdapterService;
import org.glukit.sync.api.DeviceInfo;
import org.glukit.sync.api.GlucoseRead;
import org.glukit.sync.api.SyncData;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

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

  @Override
  public SyncData convertData(DexcomSyncData source) {
    List<GlucoseRead> glucoseReads = newArrayList(Collections2.filter(Collections2.transform(source.getGlucoseReads(),
        DEXCOM_GLUCOSE_RECORD_TO_GLUCOSE_READ), VALID_READS_FILTER));

    DeviceInfo deviceInfo = DEXCOM_MANUFACTURING_PARAMS_TO_DEVICE_INFO.apply(source.getManufacturingParameters());

    return new SyncData(glucoseReads, deviceInfo);
  }
}
