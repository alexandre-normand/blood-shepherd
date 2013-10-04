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

package org.glukit.dexcom.sync;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.inject.Inject;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.apache.hadoop.hbase.util.Bytes;
import org.glukit.dexcom.sync.requests.IsFirmware;
import org.glukit.dexcom.sync.responses.SingleByteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * Checks if a given serial device is actually the dexcom receiver.
 *
 * @author alexandre.normand
 */
public class IsReceiverOnThisPortRunner {
  private static Logger LOGGER = LoggerFactory.getLogger(SerialTest.class);
  public static final int DATA_BITS = 8;
  public static final int STOP_BITS = 1;
  public static final int NO_PARITY = 0;
  public static final int FIRMWARE_BAUD_RATE = 0x9600;

  private DataOutputFactory dataOutputFactory;
  private DataInputFactory dataInputFactory;
  private ResponseReader responseReader;

  @Inject
  public IsReceiverOnThisPortRunner(DataOutputFactory dataOutputFactory,
                                    DataInputFactory dataInputFactory,
                                    ResponseReader responseReader) {
    this.dataOutputFactory = dataOutputFactory;
    this.dataInputFactory = dataInputFactory;
    this.responseReader = responseReader;
  }

  public boolean isReceiver(String portName) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    final SerialPort serialPort = new SerialPort(portName);
    try {

      SimpleTimeLimiter timeout = new SimpleTimeLimiter(executor);
      Boolean result = timeout.callWithTimeout(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          return isFirmware(serialPort);
        }
      }, 5, TimeUnit.SECONDS, true);


      return result;
    } catch (Exception e) {
      LOGGER.info("Receiver not running on this port since we had an exception while checking.", e);
      return false;
    } finally {
      executor.shutdown();
      if (serialPort.isOpened()) {
        try {
          LOGGER.debug(format("Closing port %s", serialPort.getPortName()));
          serialPort.closePort();
        } catch (SerialPortException e) {
          LOGGER.debug("Error closing port, ignoring.", e);
        }
      }
    }
  }

  private boolean isFirmware(SerialPort serialPort) throws SerialPortException, SerialPortTimeoutException {
    serialPort.openPort();

    if (!serialPort.isOpened()) {
      LOGGER.info(format("Couldn't open port %s, assuming this is not the receiver", serialPort.getPortName()));
      return false;
    }

    printLineStatus(serialPort.getLinesStatus());
    LOGGER.debug(format("Opened port [%s]: %b", serialPort.getPortName(), serialPort.isOpened()));
    serialPort.setParams(FIRMWARE_BAUD_RATE, DATA_BITS, STOP_BITS, NO_PARITY);

    byte[] request = new IsFirmware(this.dataOutputFactory).asBytes();
    LOGGER.debug(format("Writing [%d] bytes: [%s]", request.length, Bytes.toStringBinary(request)));

    boolean status = serialPort.writeBytes(request);
    LOGGER.info(format("Wrote success: %b", status));

    SingleByteResponse singleByteResponse = this.responseReader.read(SingleByteResponse.class, serialPort);
    LOGGER.info(format("Received successful ACK response [%s]", singleByteResponse));
    return true;
  }

  private void printLineStatus(int[] statuses) {
    for (int status: statuses) {
      LOGGER.debug(format("Line status is %d", status));
    }
  }
}
