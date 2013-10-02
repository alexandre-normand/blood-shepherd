package org.glukit.dexcom.sync.requests;

import java.nio.ByteBuffer;

/**
 * Interface for a request command.
 *
 * @author alexandre.normand
 */
public interface Command {
  ByteBuffer asByteBuffer();
  byte[] asBytes();
}
