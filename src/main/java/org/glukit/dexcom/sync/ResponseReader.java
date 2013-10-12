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

import com.google.common.base.Throwables;
import com.google.common.primitives.Bytes;
import com.google.inject.Inject;
import jssc.SerialPort;
import org.glukit.dexcom.sync.responses.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;

import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.CRC16_SIZE;
import static org.glukit.dexcom.sync.DecodingUtils.getCrc16;
import static org.glukit.dexcom.sync.DecodingUtils.toHexString;

/**
 * Response reader.
 *
 * @author alexandre.normand
 */
public class ResponseReader {
  private static Logger LOGGER = LoggerFactory.getLogger(ResponseReader.class);

  static final int HEADER_SIZE = 4;
  static final int TRAILER_SIZE = 2;


  private DataInputFactory dataInputFactory;

  @Inject
  public ResponseReader(DataInputFactory dataInputFactory) {
    this.dataInputFactory = dataInputFactory;
  }

  public <T extends Response> T read(Class<T> type, SerialPort serialPort) {
    try {
      T response = type.getConstructor(DataInputFactory.class).newInstance(this.dataInputFactory);
      byte[] header = serialPort.readBytes(HEADER_SIZE);
      LOGGER.debug(format("Read header from port: %s", toHexString(header)));
      ResponseHeader responseHeader = readHeader(header);

      int expectedPayloadSize = responseHeader.getPacketSize() - (HEADER_SIZE + TRAILER_SIZE);
      LOGGER.debug(format("Expected payload of [%d] bytes", expectedPayloadSize));

      byte[] payload = new byte[0];
      if (expectedPayloadSize > 0) {
        payload = serialPort.readBytes(expectedPayloadSize);
        LOGGER.debug(format("Read payload from port: %s", toHexString(payload)));
        response.fromBytes(payload);
      } else {
        LOGGER.debug("No payload expected, skipping to trailer...");
      }

      byte[] crc16 = serialPort.readBytes(TRAILER_SIZE);
      LOGGER.debug(format("Read crc16 from port: %s", toHexString(crc16)));
      validateCrc(Bytes.concat(header, payload, crc16), crc16);

      return response;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private void validateCrc(byte[] packet, byte[] crcBytes) throws IOException {
    DataInput input = this.dataInputFactory.create(new ByteArrayInputStream(crcBytes));
    int crc = input.readUnsignedShort();
    // Validate CRC16 matches what we got
    int computedCrc16 = getCrc16(packet, 0, packet.length - TRAILER_SIZE);

    if (crc != computedCrc16) {
      throw new IllegalStateException(format("Invalid crc, expected [%s], received [%s]",
              Integer.toHexString(computedCrc16), Integer.toHexString(crc)));
    }
  }

  private ResponseHeader readHeader(byte[] headerBytes) {
    try {
      DataInput input = this.dataInputFactory.create(new ByteArrayInputStream(headerBytes));
      byte sof = input.readByte();
      if (sof != 1) {
        throw new IllegalStateException(format("Received bad SOF value of [%s], something is wrong",
                toHexString(new byte[]{sof})));
      }

      int packetSize = input.readUnsignedShort();

      byte commandId = input.readByte();
      ReceiverCommand command = ReceiverCommand.fromId(commandId);

      return new ResponseHeader(command, packetSize);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private static class ResponseHeader {
    ReceiverCommand command;
    int packetSize;

    private ResponseHeader(ReceiverCommand command, int packetSize) {
      this.command = command;
      this.packetSize = packetSize;
    }

    private ReceiverCommand getCommand() {
      return command;
    }

    private int getPacketSize() {
      return packetSize;
    }
  }
}
