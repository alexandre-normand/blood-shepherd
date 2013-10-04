package org.glukit.dexcom.sync.requests;

import org.glukit.dexcom.sync.DataOutputFactory;

/**
 * This is supposed to be the command that we send to check if the serial port queried is the firmware/receiver.
 *
 * @author alexandre.normand
 */
public class IsFirmware extends BaseCommand {

  public IsFirmware(DataOutputFactory dataOutputFactory) {
    super(dataOutputFactory);
  }

  @Override
  public byte getCommandId() {
    return 0x1b;
  }

  @Override
  public byte getSizeOfField() {
    return 1;
  }

  @Override
  public short getSize() {
    return 6;
  }
}
