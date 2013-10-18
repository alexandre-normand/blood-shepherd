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

import com.google.common.primitives.UnsignedInts;
import org.glukit.dexcom.sync.DataInputFactory;
import org.glukit.dexcom.sync.model.DatabasePageHeader;
import org.glukit.dexcom.sync.model.GlucoseReadRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;

import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.toHexString;
import static org.glukit.dexcom.sync.DecodingUtils.validateCrc;

/**
 * Database pages response for {@link org.glukit.dexcom.sync.model.RecordType#EGVData} (GlucoseReads).
 *
 * @author alexandre.normand
 */
public class GlucoseReadsDatabasePagesResponse extends GenericRecordDatabasePagesResponse<GlucoseReadRecord> {
  private static Logger LOGGER = LoggerFactory.getLogger(GlucoseReadsDatabasePagesResponse.class);

  public GlucoseReadsDatabasePagesResponse(DataInputFactory dataInputFactory) {
    super(dataInputFactory);
  }

  protected GlucoseReadRecord parseRecord(byte[] recordBytes, DatabasePageHeader header,
                                        long recordNumber) throws IOException {
    DataInput input = this.dataInputFactory.create(new ByteArrayInputStream(recordBytes));
    LOGGER.debug(format("Parsing glucose record from bytes [%s]", toHexString(recordBytes)));

    long systemSeconds = UnsignedInts.toLong(input.readInt());
    long displaySeconds = UnsignedInts.toLong(input.readInt());
    int glucoseValueWithFlags = input.readUnsignedShort();
    byte trendAndArrowNoise = input.readByte();
    int actualReceiverCrc = input.readUnsignedShort();

    validateCrc(actualReceiverCrc, recordBytes);

    GlucoseReadRecord glucoseReadRecord = new GlucoseReadRecord(systemSeconds, displaySeconds,
            glucoseValueWithFlags, trendAndArrowNoise, recordNumber, header.getPageNumber());
    LOGGER.debug(format("Parsed GlucoseRead: [%s]", glucoseReadRecord));

    return glucoseReadRecord;
  }

  @Override
  protected int getRecordLength() {
    return GlucoseReadRecord.RECORD_LENGTH;
  }
}
