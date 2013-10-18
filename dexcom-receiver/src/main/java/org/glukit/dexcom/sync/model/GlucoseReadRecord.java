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

package org.glukit.dexcom.sync.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A glucose read record. It's pretty important. It maps to {@link RecordType#EGVData}.
 *
 * @author alexandre.normand
 */
@EqualsAndHashCode
@ToString
public class GlucoseReadRecord {
  public static final int RECORD_LENGTH = 13;

  private long internalSecondsSinceDexcomEpoch;
  private long localSecondsSinceDexcomEpoch;
  private int glucoseValueWithFlags;
  private byte trendArrowAndNoise;
  private long recordNumber;
  private long pageNumber;

  public GlucoseReadRecord(long internalSecondsSinceDexcomEpoch,
                           long localSecondsSinceDexcomEpoch,
                           int glucoseValueWithFlags,
                           byte trendArrowAndNoise,
                           long recordNumber,
                           long pageNumber) {
    this.internalSecondsSinceDexcomEpoch = internalSecondsSinceDexcomEpoch;
    this.localSecondsSinceDexcomEpoch = localSecondsSinceDexcomEpoch;
    this.glucoseValueWithFlags = glucoseValueWithFlags;
    this.trendArrowAndNoise = trendArrowAndNoise;
    this.recordNumber = recordNumber;
    this.pageNumber = pageNumber;
  }

  public long getInternalSecondsSinceDexcomEpoch() {
    return internalSecondsSinceDexcomEpoch;
  }

  public long getLocalSecondsSinceDexcomEpoch() {
    return localSecondsSinceDexcomEpoch;
  }

  public int getGlucoseValueWithFlags() {
    return glucoseValueWithFlags;
  }

  public byte getTrendArrowAndNoise() {
    return trendArrowAndNoise;
  }

  public long getRecordNumber() {
    return recordNumber;
  }

  public long getPageNumber() {
    return pageNumber;
  }
}
