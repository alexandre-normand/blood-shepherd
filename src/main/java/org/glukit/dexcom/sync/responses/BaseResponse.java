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

package org.glukit.dexcom.sync.responses;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import org.glukit.dexcom.sync.DataInputFactory;
import org.glukit.dexcom.sync.ReceiverCommand;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;

import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.CRC16_SIZE;
import static org.glukit.dexcom.sync.DecodingUtils.getCrc16;

/**
 * This is the base response that deals with some common structure to responses.
 *
 * @author alexandre.normand
 */
public abstract class BaseResponse implements Response {

  protected static final int ENVELOPPE_SIZE = 6;

  private byte sizeOfField;
  private ReceiverCommand command;
  private short size;
  private int crc;

  private DataInputFactory dataInputFactory;

  @Inject
  protected BaseResponse(DataInputFactory dataInputFactory) {
    this.dataInputFactory = dataInputFactory;
  }

  @Override
  public int getExpectedSize() {
    return ENVELOPPE_SIZE + getContentSize();
  }

  @Override
  public void fromBytes(byte[] responseAsBytes) {
    try {
      DataInput input = this.dataInputFactory.create(new ByteArrayInputStream(responseAsBytes));
      this.sizeOfField = input.readByte();
      byte commandId = input.readByte();
      this.command = ReceiverCommand.fromId(commandId);
      this.size = input.readShort();
      byte[] payload = new byte[getContentSize()];
      input.readFully(payload, 0, getContentSize());
      contentFromBytes(payload);
      this.crc = input.readUnsignedShort();

      // Validate CRC16 matches what we got
      int computedCrc16 = getCrc16(responseAsBytes, 0, getExpectedSize() - CRC16_SIZE);

      if (this.crc != computedCrc16) {
        throw new IllegalStateException(format("Invalid crc, expected [%d], received [%d]", computedCrc16, this.crc));
      }
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  protected abstract int getContentSize();
  protected abstract void contentFromBytes(byte[] content);

  public byte getSizeOfField() {
    return sizeOfField;
  }

  public ReceiverCommand getCommand() {
    return command;
  }

  public short getSize() {
    return size;
  }

  public int getCrc() {
    return crc;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
            .add("sizeOfField", sizeOfField)
            .add("command", command)
            .add("size", size)
            .add("crc", crc)
            .toString();
  }
}
