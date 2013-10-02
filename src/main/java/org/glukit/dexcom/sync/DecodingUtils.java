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

package org.glukit.dexcom.sync;

import sun.misc.CRC16;

/**
 * Some useful methods for decoding.
 *
 * @author alexandre.normand
 */
public final class DecodingUtils {
  public static final int CRC16_SIZE = 2;

  public static int getCrc16(byte[] bytes, int offset, int length) {
    int contentSize = length - offset;
    byte[] content = new byte[contentSize];
    System.arraycopy(bytes, offset, content, 0, contentSize);
    CRC16 crc = new CRC16();
    for (byte value : content) {
      crc.update(value);
    }

    return unsignedShort((short) crc.value);
  }

  public static int unsignedShort(short value) {
    return (short) (value & 0xFF);
  }
}
