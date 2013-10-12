package org.glukit.dexcom.sync.requests;

import org.glukit.dexcom.sync.DataOutputFactory;
import org.glukit.dexcom.sync.ReceiverCommand;

/**
 * This is supposed to be the command that we send to check if the serial port queried is the firmware/receiver.
 *
 * @author alexandre.normand
 */
public class Ping extends BaseCommand {

  public Ping(DataOutputFactory dataOutputFactory) {
    super(dataOutputFactory);
  }

  @Override
  public ReceiverCommand getCommand() {
    return ReceiverCommand.Ping;
  }

  @Override
  protected byte[] getContent() {
    return new byte[0];
  }
}
