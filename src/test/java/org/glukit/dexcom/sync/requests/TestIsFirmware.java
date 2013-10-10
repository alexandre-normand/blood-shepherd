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

import org.glukit.dexcom.sync.DecodingUtils;
import org.glukit.dexcom.sync.LittleEndianDataOutputFactory;
import org.junit.Test;

import static org.glukit.dexcom.sync.DecodingUtils.fromHexString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Unit test of {@link IsFirmware}.
 *
 * @author alexandre.normand
 */
public class TestIsFirmware {

  /**
   * Validate that the bytes generated from this command match the expectations (from trace examples).
   */
  @Test
  public void isFirmwareShouldGenerateCorrectBytes() {
    IsFirmware isFirmware = new IsFirmware(new LittleEndianDataOutputFactory());
    assertThat(isFirmware.asBytes(), equalTo(fromHexString("01 1B 00 00 26 C5")));
  }
}
