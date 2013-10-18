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
 * DatabasePage Header
 * @author alexandre.normand
 */
@EqualsAndHashCode
@ToString
public class DatabasePageHeader {
  private long firstRecordIndex;
  private long numberOfRecords;
  private RecordType recordType;
  private byte revision;
  private long pageNumber;
  private long reserved2;
  private long reversed3;
  private long reserved4;
  private int crc;

  public DatabasePageHeader(long firstRecordIndex, long numberOfRecords, RecordType recordType, byte revision,
                            long pageNumber, long reserved2, long reversed3, long reserved4, int crc) {
    this.firstRecordIndex = firstRecordIndex;
    this.numberOfRecords = numberOfRecords;
    this.recordType = recordType;
    this.revision = revision;
    this.pageNumber = pageNumber;
    this.reserved2 = reserved2;
    this.reversed3 = reversed3;
    this.reserved4 = reserved4;
    this.crc = crc;
  }

  public long getFirstRecordIndex() {
    return firstRecordIndex;
  }

  public long getNumberOfRecords() {
    return numberOfRecords;
  }

  public RecordType getRecordType() {
    return recordType;
  }

  public byte getRevision() {
    return revision;
  }

  public long getPageNumber() {
    return pageNumber;
  }

  public long getReserved2() {
    return reserved2;
  }

  public long getReversed3() {
    return reversed3;
  }

  public long getReserved4() {
    return reserved4;
  }

  public int getCrc() {
    return crc;
  }
}
