package org.glukit.dexcom.sync.commands;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: skippyjon
 * Date: 2013-09-25
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetPageRange implements Command {
  @Override
  public ByteBuffer asByteBuffer() {
    byte[] pageRangeRequest = asBytes();
    return ByteBuffer.wrap(pageRangeRequest);
  }

  @Override
  public byte[] asBytes() {
    byte[] pageRangeRequest = new byte[7];
    pageRangeRequest[0] = 0x01;
    pageRangeRequest[1] = 0x07;
    pageRangeRequest[3] = 0x10;
    pageRangeRequest[4] = 0x04;
    pageRangeRequest[5] = (byte) 0x8b;
    pageRangeRequest[6] = (byte) 0xb8;
    return pageRangeRequest;
  }
}
