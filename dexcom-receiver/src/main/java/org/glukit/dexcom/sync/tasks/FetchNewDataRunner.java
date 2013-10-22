/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Alexandre Normand
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.glukit.dexcom.sync.tasks;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.glukit.dexcom.sync.DataInputFactory;
import org.glukit.dexcom.sync.DataOutputFactory;
import org.glukit.dexcom.sync.DatabasePagesPager;
import org.glukit.dexcom.sync.ResponseReader;
import org.glukit.dexcom.sync.model.*;
import org.glukit.dexcom.sync.requests.*;
import org.glukit.dexcom.sync.responses.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.toHexString;
import static org.glukit.dexcom.sync.g4.DexcomG4Constants.*;
import static org.glukit.dexcom.sync.model.RecordType.EGVData;
import static org.glukit.dexcom.sync.model.RecordType.ManufacturingData;
import static org.glukit.dexcom.sync.model.RecordType.UserEventData;

/**
 * Fetches the new data since last sync.
 *
 * @author alexandre.normand
 */
public class FetchNewDataRunner {
  private static Logger LOGGER = LoggerFactory.getLogger(FetchNewDataRunner.class);
  private final DataOutputFactory dataOutputFactory;
  private final DataInputFactory dataInputFactory;
  private final ResponseReader responseReader;

  @Inject
  public FetchNewDataRunner(DataOutputFactory dataOutputFactory,
                            DataInputFactory dataInputFactory,
                            ResponseReader responseReader) {
    this.dataOutputFactory = dataOutputFactory;
    this.dataInputFactory = dataInputFactory;
    this.responseReader = responseReader;
  }


  /**
   * Fetches the data from the dexcom
   *
   * @param serialPort the serial port of the dexcom receiver
   * @param since      unused at the moment, this optimization might come later
   * @return the synced data, it's the whole thing of what's still in the receiver memory.
   */
  public DexcomSyncData fetchData(SerialPort serialPort, Instant since) {
    try {
      serialPort.openPort();
      if (!serialPort.isOpened()) {
        throw new RuntimeException("Can't open receiver to get the data.");
      }

      LOGGER.info(format("Opened port [%s]: %b", serialPort.getPortName(), serialPort.isOpened()));
      serialPort.setParams(FIRMWARE_BAUD_RATE, DATA_BITS, STOP_BITS, NO_PARITY);

      ManufacturingParameters manufacturingData = getManufacturingData(serialPort);
      List<GlucoseReadRecord> glucoseReads = getGlucoseReads(serialPort);
      List<UserEventRecord> userEvents = getUserEvents(serialPort);

      return new DexcomSyncData(glucoseReads, userEvents, manufacturingData);
    } catch (SerialPortException e) {
      throw Throwables.propagate(e);
    }
  }

  private ManufacturingParameters getManufacturingData(SerialPort serialPort) throws SerialPortException {
    ManufacturingParameters manufacturingData = null;

    DatabasePagesPager manufacturingDataPager = getPagerForRecordType(serialPort, ManufacturingData);

    for (DatabaseReadRequestSpec readRequestSpec : manufacturingDataPager) {
      ManufacturingDataDatabasePagesResponse manufacturingDataDbResponse =
              readDatabasePage(ManufacturingDataDatabasePagesResponse.class,
                      serialPort, readRequestSpec, ManufacturingData);

      // We're assuming we'll always have just one or that the most recent is always going to be the one
      // we want to keep.
      List<ManufacturingParameters> manufacturingParameters =
              manufacturingDataDbResponse.getManufacturingParameters();
      if (!manufacturingParameters.isEmpty()) {
        manufacturingData = manufacturingParameters.iterator().next();
      }
    }

    return manufacturingData;
  }

  private DatabasePagesPager getPagerForRecordType(SerialPort serialPort,
                                                   RecordType recordType) throws SerialPortException {
    PageRangeResponse pageRange = readManufacturingDataPageRange(serialPort, recordType);
    return new DatabasePagesPager(pageRange.getFirstPage(), pageRange.getLastPage());
  }

  private <T extends DatabasePagesResponse> T readDatabasePage(Class<T> responseClass,
                                                               SerialPort serialPort,
                                                               DatabaseReadRequestSpec readRequestSpec,
                                                               RecordType recordType)
          throws SerialPortException {
    ReadDatabasePagesCommand readDatabasePagesCommand =
            new ReadDatabasePagesCommand(this.dataOutputFactory, recordType, readRequestSpec.getStartPage(),
                    readRequestSpec.getNumberOfPages());

    byte[] packet = readDatabasePagesCommand.asBytes();
    LOGGER.info(format("Sending read database pages for %s: %s", recordType.name(), toHexString(packet)));
    serialPort.writeBytes(packet);

    return this.responseReader.read(responseClass, serialPort);
  }

  private PageRangeResponse readManufacturingDataPageRange(SerialPort serialPort,
                                                           RecordType recordType) throws SerialPortException {
    ReadDatabasePageRange readDatabasePageRange =
            new ReadDatabasePageRange(this.dataOutputFactory, recordType);
    byte[] packet = readDatabasePageRange.asBytes();
    LOGGER.info(format("Sending read database page range for %s: %s", recordType.name(),
            toHexString(packet)));
    serialPort.writeBytes(packet);

    PageRangeResponse pageRangeResponse =
            this.responseReader.read(PageRangeResponse.class, serialPort);
    LOGGER.info(format("Page range for %s: [%d] to [%d]", recordType.name(), pageRangeResponse.getFirstPage(),
            pageRangeResponse.getLastPage()));

    return pageRangeResponse;
  }

  private List<GlucoseReadRecord> getGlucoseReads(SerialPort serialPort) throws SerialPortException {
    List<GlucoseReadRecord> fullGlucoseReadRecords = newArrayList();

    DatabasePagesPager manufacturingDataPager = getPagerForRecordType(serialPort, EGVData);

    for (DatabaseReadRequestSpec readRequestSpec : manufacturingDataPager) {
      GlucoseReadsDatabasePagesResponse glucoseReadResponse =
              readDatabasePage(GlucoseReadsDatabasePagesResponse.class, serialPort, readRequestSpec, EGVData);

      List<GlucoseReadRecord> glucoseReadRecords = glucoseReadResponse.getRecords();
      fullGlucoseReadRecords.addAll(glucoseReadRecords);
    }

    return fullGlucoseReadRecords;
  }

  private List<UserEventRecord> getUserEvents(SerialPort serialPort) throws SerialPortException {
      List<UserEventRecord> allUserEvents = newArrayList();

      DatabasePagesPager manufacturingDataPager = getPagerForRecordType(serialPort, UserEventData);

      for (DatabaseReadRequestSpec readRequestSpec : manufacturingDataPager) {
        UserEventsDatabasePagesResponse userEventRecordPage =
                readDatabasePage(UserEventsDatabasePagesResponse.class, serialPort, readRequestSpec, UserEventData);

        List<UserEventRecord> glucoseReadRecords = userEventRecordPage.getRecords();
        allUserEvents.addAll(glucoseReadRecords);
      }

      return allUserEvents;
    }

  private Utf8PayloadGenericResponse readFirmwareHeader(SerialPort serialPort) throws SerialPortException {
    ReadFirmwareHeader readFirmwareHeader = new ReadFirmwareHeader(this.dataOutputFactory);
    byte[] packet = readFirmwareHeader.asBytes();
    LOGGER.info(format("Sending read firmware header: %s", toHexString(packet)));
    serialPort.writeBytes(packet);

    Utf8PayloadGenericResponse utf8PayloadGenericResponse =
            this.responseReader.read(Utf8PayloadGenericResponse.class, serialPort);
    LOGGER.info(format("Receiver plugged with firmware: %s", utf8PayloadGenericResponse.asString()));
    return utf8PayloadGenericResponse;
  }

  private PageRangeResponse readGlucosePageRange(SerialPort serialPort) throws SerialPortException {
    ReadDatabasePageRange readGlucoseReadDatabasePageRange = new ReadDatabasePageRange(this.dataOutputFactory, EGVData);
    byte[] packet = readGlucoseReadDatabasePageRange.asBytes();
    LOGGER.info(format("Sending read database page range for glucose reads: %s", toHexString(packet)));
    serialPort.writeBytes(packet);

    PageRangeResponse glucosePageRangeResponse =
            this.responseReader.read(PageRangeResponse.class, serialPort);
    LOGGER.info(format("Page range for glucose reads: [%d] to [%d]", glucosePageRangeResponse.getFirstPage(),
            glucosePageRangeResponse.getLastPage()));

    return glucosePageRangeResponse;
  }
}
