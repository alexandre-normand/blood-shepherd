package org.glukit.dexcom.sync.commands;

import com.google.common.base.Throwables;
import sun.misc.CRC16;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: skippyjon
 * Date: 2013-09-27
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseCommand implements Command {
  @Override
  public ByteBuffer asByteBuffer() {
    return ByteBuffer.wrap(asBytes());
  }

  @Override
  public byte[] asBytes() {
    try {
      ByteArrayOutputStream outputStream = null;
      outputStream = new ByteArrayOutputStream(getSize());
      DataOutput output = new DataOutputStream(outputStream);
      output.write(getSizeOfField());
      output.write(getCommandId());
      output.writeShort(getSize());
      int contentSize = getSize() - 2;
      byte[] content = new byte[contentSize];
      System.arraycopy(outputStream.toByteArray(), 0, content, 0, contentSize);
      output.writeShort(unsignedShort(getCrc16(content)));
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  protected static int unsignedShort(short value) {
    return value & 0xFF;
  }

  protected static short getCrc16(byte[] bytes) {
    CRC16 crc = new CRC16();
    for (byte value : bytes) {
      crc.update(value);
    }

    return (short) crc.value;
  }

  public abstract byte getCommandId();
  public abstract byte getSizeOfField();
  public abstract short getSize();
}
