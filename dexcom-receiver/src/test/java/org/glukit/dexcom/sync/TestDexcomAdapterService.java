package org.glukit.dexcom.sync;

import org.glukit.dexcom.sync.g4.DexcomG4Constants;
import org.glukit.dexcom.sync.model.DexcomSyncData;
import org.glukit.dexcom.sync.model.GlucoseReadRecord;
import org.glukit.dexcom.sync.model.ManufacturingParameters;
import org.glukit.sync.api.DeviceInfo;
import org.glukit.sync.api.GlucoseRead;
import org.glukit.sync.api.SyncData;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static org.glukit.dexcom.sync.DexcomAdapterService.SPECIAL_GLUCOSE_VALUES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test of {@link DexcomAdapterService}.
 *
 * @author alexandre.normand
 */
public class TestDexcomAdapterService {

  private static final String SERIAL_NUMBER = "serial";
  private static final String HARDWARE_REVISION = "revision1";
  private static final String HARDWARE_ID = "hardwareId";
  private static final Integer NORMAL_READ_TEST_VALUE = 83;
  private static final List<GlucoseRead> EMPTY_GLUCOSE_READS = Collections.emptyList();
  private static final List<GlucoseReadRecord> EMPTY_GLUCOSE_READ_RECORDS = Collections.emptyList();

  @Test
  public void noReadsShouldConvertSuccessfully() throws Exception {
    DexcomAdapterService dexcomAdapterService = new DexcomAdapterService();

    SyncData syncData = dexcomAdapterService.convertData(new DexcomSyncData(EMPTY_GLUCOSE_READ_RECORDS,
        new ManufacturingParameters(SERIAL_NUMBER, "partNumber", HARDWARE_REVISION, "2013-10-18 10:10", HARDWARE_ID)));

    SyncData expectedSyncData = new SyncData(EMPTY_GLUCOSE_READS, new DeviceInfo(SERIAL_NUMBER,
        HARDWARE_ID, HARDWARE_REVISION));

    assertThat(syncData, is(equalTo(expectedSyncData)));
  }

  @Test
  public void singleReadShouldConvertSuccessfully() throws Exception {
    DexcomAdapterService dexcomAdapterService = new DexcomAdapterService();
    List<GlucoseReadRecord> glucoseRecords = Arrays.asList(new GlucoseReadRecord(1000, 1000, NORMAL_READ_TEST_VALUE, (byte) 0, 1L, 1L));
    SyncData syncData = dexcomAdapterService.convertData(new DexcomSyncData(glucoseRecords,
        new ManufacturingParameters(SERIAL_NUMBER, "partNumber", HARDWARE_REVISION, "2013-10-18 10:10", HARDWARE_ID)));

    GlucoseRead expectedRead = new GlucoseRead(
        DexcomG4Constants.DEXCOM_EPOCH.plusSeconds(1000L),
        LocalDateTime.ofInstant(DexcomG4Constants.DEXCOM_EPOCH.plusSeconds(1000L), ZoneId.of("UTC")),
        NORMAL_READ_TEST_VALUE.floatValue(),
        GlucoseRead.Unit.MG_PER_DL);
    SyncData expectedSyncData = new SyncData(Arrays.asList(expectedRead), new DeviceInfo(SERIAL_NUMBER,
        HARDWARE_ID, HARDWARE_REVISION));

    assertThat(syncData, is(equalTo(expectedSyncData)));
  }

  @Test
  public void multipleReadsShouldAllBeConverted() throws Exception {
    DexcomAdapterService dexcomAdapterService = new DexcomAdapterService();
    List<GlucoseReadRecord> glucoseRecords = Arrays.asList(new GlucoseReadRecord(1000, 1000, NORMAL_READ_TEST_VALUE, (byte) 0, 1L, 1L),
        new GlucoseReadRecord(2000, 2000, NORMAL_READ_TEST_VALUE, (byte) 0, 1L, 1L));
    SyncData syncData = dexcomAdapterService.convertData(new DexcomSyncData(glucoseRecords,
        new ManufacturingParameters(SERIAL_NUMBER, "partNumber", HARDWARE_REVISION, "2013-10-18 10:10", HARDWARE_ID)));

    GlucoseRead expectedRead1 = new GlucoseRead(
        DexcomG4Constants.DEXCOM_EPOCH.plusSeconds(1000L),
        LocalDateTime.ofInstant(DexcomG4Constants.DEXCOM_EPOCH.plusSeconds(1000L), ZoneId.of("UTC")),
        NORMAL_READ_TEST_VALUE.floatValue(),
        GlucoseRead.Unit.MG_PER_DL);

    GlucoseRead expectedRead2 = new GlucoseRead(
        DexcomG4Constants.DEXCOM_EPOCH.plusSeconds(2000L),
        LocalDateTime.ofInstant(DexcomG4Constants.DEXCOM_EPOCH.plusSeconds(2000L), ZoneId.of("UTC")),
        NORMAL_READ_TEST_VALUE.floatValue(),
        GlucoseRead.Unit.MG_PER_DL);
    SyncData expectedSyncData = new SyncData(Arrays.asList(expectedRead1, expectedRead2), new DeviceInfo(SERIAL_NUMBER,
        HARDWARE_ID, HARDWARE_REVISION));

    assertThat(syncData, is(equalTo(expectedSyncData)));
  }

  @Test
  public void displayOnlyReadShouldNotBeIncluded() throws Exception {
    DexcomAdapterService dexcomAdapterService = new DexcomAdapterService();
    List<GlucoseReadRecord> glucoseRecords = Arrays.asList(new GlucoseReadRecord(1000, 1000, 32781, (byte) 0, 1L, 1L));
    SyncData syncData = dexcomAdapterService.convertData(new DexcomSyncData(glucoseRecords,
        new ManufacturingParameters(SERIAL_NUMBER, "partNumber", HARDWARE_REVISION, "2013-10-18 10:10", HARDWARE_ID)));

    SyncData expectedSyncData = new SyncData(EMPTY_GLUCOSE_READS, new DeviceInfo(SERIAL_NUMBER,
        HARDWARE_ID, HARDWARE_REVISION));

    assertThat(syncData, is(equalTo(expectedSyncData)));
  }

  @Test
  public void specialValuesShouldNotBeIncluded() throws Exception {
    DexcomAdapterService dexcomAdapterService = new DexcomAdapterService();

    // Test all special values
    for (int specialGlucoseValue : SPECIAL_GLUCOSE_VALUES) {
      List<GlucoseReadRecord> glucoseRecords = Arrays.asList(new GlucoseReadRecord(1000, 1000, specialGlucoseValue,
          (byte) 0, 1L, 1L));
      SyncData syncData = dexcomAdapterService.convertData(new DexcomSyncData(glucoseRecords,
          new ManufacturingParameters(SERIAL_NUMBER, "partNumber", HARDWARE_REVISION, "2013-10-18 10:10", HARDWARE_ID)));

      SyncData expectedSyncData = new SyncData(EMPTY_GLUCOSE_READS, new DeviceInfo(SERIAL_NUMBER,
          HARDWARE_ID, HARDWARE_REVISION));

      assertThat(format("Glucose value [%d] should not be included in the conversion result", specialGlucoseValue),
          syncData, is(equalTo(expectedSyncData)));
    }
  }
}
