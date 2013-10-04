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
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import jssc.SerialPort;
import org.apache.hadoop.hbase.util.Bytes;
import org.glukit.dexcom.sync.responses.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * Response reader.
 *
 * @author alexandre.normand
 */
public class ResponseReader {
  private static Logger LOGGER = LoggerFactory.getLogger(ResponseReader.class);

  private DataInputFactory dataInputFactory;

  @Inject
  public ResponseReader(DataInputFactory dataInputFactory) {
    this.dataInputFactory = dataInputFactory;
  }

  public <T extends Response> T read(Class<T> type, SerialPort serialPort) {
    try {
      T response = type.getConstructor(DataInputFactory.class).newInstance(this.dataInputFactory);
      byte[] responseAsBytes = serialPort.readBytes(response.getExpectedSize());
      LOGGER.debug(format("Read bytes from port: %s", Bytes.toStringBinary(responseAsBytes)));
      response.fromBytes(responseAsBytes);
      return response;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
