package org.glukit.dexcom.sync.commands;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: skippyjon
 * Date: 2013-09-25
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Command {
  ByteBuffer asByteBuffer();
  byte[] asBytes();
}
