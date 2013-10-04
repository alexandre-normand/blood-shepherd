package org.glukit.dexcom.sync.requests;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import org.glukit.dexcom.sync.DataOutputFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.glukit.dexcom.sync.DecodingUtils.getCrc16;

/**
 * Base request class.
 * @author alexandre.normand
 */
public abstract class BaseCommand implements Command {
  private DataOutputFactory dataOutputFactory;

  @Inject
  protected BaseCommand(DataOutputFactory dataOutputFactory) {
    this.dataOutputFactory = dataOutputFactory;
  }

  @Override
  public ByteBuffer asByteBuffer() {
    return ByteBuffer.wrap(asBytes());
  }

  @Override
  public byte[] asBytes() {
    try {
      // Order is: SizeOfField, Size of packet, command id, and crc16
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(getSize());
      DataOutput output = this.dataOutputFactory.create(outputStream);
      output.write(getSizeOfField());
      output.writeShort(getSize());
      output.write(getCommandId());
      int contentSize = getSize() - 2;
      output.writeShort(getCrc16(outputStream.toByteArray(), 0, contentSize));
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }



  public abstract byte getCommandId();
  public abstract byte getSizeOfField();
  public abstract short getSize();
}
