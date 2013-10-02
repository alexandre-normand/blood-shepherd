package org.glukit.dexcom.sync.requests;

import com.google.common.base.Throwables;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.glukit.dexcom.sync.DecodingUtils.getCrc16;
import static org.glukit.dexcom.sync.DecodingUtils.unsignedShort;

/**
 * Base request class.
 * @author alexandre.normand
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
      output.writeShort(getCrc16(outputStream.toByteArray(), 0, contentSize));
//      output.write((byte) 0x01);
//      output.write((byte) 0x07);
//      output.write((byte) 0x00);
//      output.write((byte) 0x10);
//      output.write((byte) 0x04);
//      output.write((byte) 0x8b);
//      output.write((byte) 0xb8);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }



  public abstract byte getCommandId();
  public abstract byte getSizeOfField();
  public abstract short getSize();
}
