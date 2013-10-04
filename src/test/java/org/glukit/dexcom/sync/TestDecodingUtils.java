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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test of {@link DecodingUtils}
 *
 * @author alexandre.normand
 */
public class TestDecodingUtils {
  /**
   * This example comes from the trace here:
   * https://github.com/bewest/decoding-dexcom/blob/master/alexandre-normand/hex-lines.txt
   */
  @Test
  public void crc16ShouldMatchAlgoFromReceiver() throws Exception {
    byte[] array = new byte[] { (byte) 0x01, (byte) 0x06, (byte) 0x00, (byte) 0x0A, (byte) 0x5E, (byte) 0x65};
    int crc16 = DecodingUtils.getCrc16(array, 0, array.length - 2);
    assertThat(crc16, equalTo(25950));
  }

  @Test
  public void crc16ShouldMatchExample2() throws Exception {
    byte[] array = new byte[] { (byte) 0x01, (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x09, (byte) 0x04,
            (byte) 0xA1, (byte) 0x8A};

    int crc16 = DecodingUtils.getCrc16(array, 0, array.length - 2);
    assertThat(crc16, equalTo(35489));
  }
}
