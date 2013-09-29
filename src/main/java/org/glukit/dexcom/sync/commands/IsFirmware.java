package org.glukit.dexcom.sync.commands;

/**
 * Created with IntelliJ IDEA.
 * User: skippyjon
 * Date: 2013-09-27
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class IsFirmware extends BaseCommand {
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
