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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.fromHexString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test of {@link DecodingUtils}.
 * It uses actual requests/responses seen in traces such as this one:
 * https://github.com/bewest/decoding-dexcom/blob/master/alexandre-normand/hex-lines.txt
 *
 * @author alexandre.normand
 */
@RunWith(Parameterized.class)
public class TestDecodingUtils {

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{{"WriteBios", fromHexString("01 06 00 0A 5E 65")},
            {"AmIFirmwareRequest", fromHexString("01 06 00 1b 4e 67")},
            {"AmIFirmwareResponse", fromHexString("01 08 00 01 09 04 a1 8a")},
            {"ReadBiosHeader", fromHexString("01 06 00 0B 7F 75")},
            {"ReadLastPageOrSomethingLikeThat", fromHexString("01 07 00 10 04 8b b8")},
            {"SomeRequestThatTriggersGlucoseInResponse", fromHexString("01 0c 00 11 04 b9 05 00 00 04 6d 29")},
            {"ReadBiosResponse", fromHexString("01 03 01 01 3C 46 69 72 6D 77 61 72 65 48 65 61 64 65 72 20 53 63 " +
                    "68 65 6D 61 56 65 72 73 69 6F 6E 3D 27 31 27 20 41 70 69 56 65 72 73 69 6F 6E 3D 27 32 2E 32 2E " +
                    "30 2E 30 27 20 54 65 73 74 41 70 69 56 65 72 73 69 6F 6E 3D 27 32 2E 34 2E 30 2E 30 27 20 50 " +
                    "72 6F 64 75 63 74 49 64 3D 27 47 34 52 65 63 65 69 76 65 72 27 20 50 72 6F 64 75 63 74 4E 61 " +
                    "6D 65 3D 27 44 65 78 63 6F 6D 20 47 34 20 52 65 63 65 69 76 65 72 27 20 53 6F 66 74 77 61 72 " +
                    "65 4E 75 6D 62 65 72 3D 27 53 57 31 30 30 35 30 27 20 46 69 72 6D 77 61 72 65 56 65 72 73 69 " +
                    "6F 6E 3D 27 32 2E 30 2E 31 2E 31 30 34 27 20 50 6F 72 74 56 65 72 73 69 6F 6E 3D 27 34 2E 36 " +
                    "2E 34 2E 34 35 27 20 52 46 56 65 72 73 69 6F 6E 3D 27 31 2E 30 2E 30 2E 32 37 27 20 44 65 78 " +
                    "42 6F 6F 74 56 65 72 73 69 6F 6E 3D 27 33 27 2F 3E D8 D4")}});
  }

  private final String commandId;
  private byte[] packet;

  public TestDecodingUtils(String commandId, byte[] packet) {
    this.commandId = commandId;
    this.packet = packet;
  }

  /**
   * The inputs are full packets. Each packet includes a CRC16 of the packets that precede. The test
   * ensures that our CRC16 math validates against the actual CRC values from real packets.
   */
  @Test
  public void crc16CalculationShouldMatchLastTwoBytesOfPacket() throws Exception {
    int crc16 = DecodingUtils.getCrc16(this.packet, 0, this.packet.length - 2);

    assertThat(format("CRC16 validation for packet [%s] failed", this.commandId), (short) crc16,
            equalTo(getPacketCrcValue()));
  }

  private short getPacketCrcValue() {
    ByteBuffer buffer = ByteBuffer.wrap(this.packet, this.packet.length - 2, 2);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    return buffer.getShort();
  }
}
