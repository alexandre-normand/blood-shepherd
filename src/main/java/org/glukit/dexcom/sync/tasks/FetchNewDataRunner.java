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
import org.glukit.dexcom.sync.*;
import org.glukit.dexcom.sync.requests.*;
import org.glukit.dexcom.sync.responses.GenericResponse;
import org.glukit.dexcom.sync.responses.PageRangeResponse;
import org.glukit.dexcom.sync.responses.Utf8PayloadGenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.Map;

import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.toHexString;
import static org.glukit.dexcom.sync.g4.DexcomG4Constants.*;
import static org.glukit.dexcom.sync.model.RecordType.EGVData;

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


  public Map<String, String> fetchData(SerialPort serialPort, Instant since) {
    try {
      // TODO: This opening/closing of ports should be wrapped in some class
      serialPort.openPort();
      if (!serialPort.isOpened()) {
        throw new RuntimeException("Can't open receiver to get the data.");
      }

      LOGGER.info(format("Opened port [%s]: %b", serialPort.getPortName(), serialPort.isOpened()));
      serialPort.setParams(FIRMWARE_BAUD_RATE, DATA_BITS, STOP_BITS, NO_PARITY);

      LOGGER.info(format("This will eventually get the data since %s", since.toString()));

      Utf8PayloadGenericResponse firmwareHeader = readFirmwareHeader(serialPort);
      PageRangeResponse manufacturingDataPageRange = readManufacturingDataPageRange(serialPort);
      PageRangeResponse glucosePageRange = readGlucosePageRange(serialPort);

      ReadDatabasePagesCommand readDatabasePagesCommand =
              new ReadDatabasePagesCommand(this.dataOutputFactory, EGVData, glucosePageRange.getFirstPage(),
                      (byte) (6));
      byte[] packet = readDatabasePagesCommand.asBytes();
      LOGGER.info(format("Sending read database pages for glucose reads: %s", toHexString(packet)));
      serialPort.writeBytes(packet);

      GenericResponse genericResponse =
              this.responseReader.read(GenericResponse.class, serialPort);
      LOGGER.info(format("Response for database pages of glucose reads: [%s]",
              toHexString(genericResponse.getPayload())));
    } catch (SerialPortException e) {
      throw Throwables.propagate(e);
    }
    return null;
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

  private PageRangeResponse readManufacturingDataPageRange(SerialPort serialPort) throws SerialPortException {
    ReadDatabasePageRange readDatabasePageRange = new ReadManufacturingDataDatabasePageRange(this.dataOutputFactory);
    byte[] packet = readDatabasePageRange.asBytes();
    LOGGER.info(format("Sending read database page range for manufacturing data: %s",
            toHexString(packet)));
    serialPort.writeBytes(packet);

    PageRangeResponse pageRangeResponse =
            this.responseReader.read(PageRangeResponse.class, serialPort);
    LOGGER.info(format("Page range for manufacturing data: [%d] to [%d]", pageRangeResponse.getFirstPage(),
            pageRangeResponse.getLastPage()));

    return pageRangeResponse;
  }

  private PageRangeResponse readGlucosePageRange(SerialPort serialPort) throws SerialPortException {
    ReadGlucoseReadDatabasePageRange readGlucoseReadDatabasePageRange =
            new ReadGlucoseReadDatabasePageRange(this.dataOutputFactory);
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
