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

package org.glukit.dexcom.sync.requests;

import java.nio.ByteBuffer;

/**
 * GetPageRange request command.
 * @author alexandre.normand
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
