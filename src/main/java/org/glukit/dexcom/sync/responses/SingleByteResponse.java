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

package org.glukit.dexcom.sync.responses;

import com.google.common.base.Objects;
import org.glukit.dexcom.sync.DataInputFactory;

/**
 * Dexcom has something that is a dummy token response of 1 byte content (plus envelope). The content byte
 * doesn't seem to have any meaning but the fact that the response is of one byte seems relevant.
 *
 * @author alexandre.normand
 */
public class SingleByteResponse extends BaseResponse {
  private byte payload;

  public SingleByteResponse(DataInputFactory dataInputFactory) {
    super(dataInputFactory);
  }

  @Override
  protected int getContentSize() {
    return 1;
  }

  @Override
  protected void contentFromBytes(byte[] content) {
    this.payload = content[0];
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
            .add("payload", payload)
            .toString();
  }
}
