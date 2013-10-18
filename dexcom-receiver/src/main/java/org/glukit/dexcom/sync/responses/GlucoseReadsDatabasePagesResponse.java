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

import com.google.common.base.Throwables;
import com.google.common.primitives.UnsignedInts;
import org.glukit.dexcom.sync.DataInputFactory;
import org.glukit.dexcom.sync.DecodingUtils;
import org.glukit.dexcom.sync.g4.DexcomG4Constants;
import org.glukit.dexcom.sync.model.DatabasePage;
import org.glukit.dexcom.sync.model.DatabasePageHeader;
import org.glukit.dexcom.sync.model.GlucoseReadRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.toHexString;
import static org.glukit.dexcom.sync.DecodingUtils.validateCrc;

/**
 * Database pages response for {@link org.glukit.dexcom.sync.model.RecordType#EGVData} (GlucoseReads).
 *
 * @author alexandre.normand
 */
public class GlucoseReadsDatabasePagesResponse extends DatabasePagesResponse {
  private static Logger LOGGER = LoggerFactory.getLogger(GlucoseReadsDatabasePagesResponse.class);

  public GlucoseReadsDatabasePagesResponse(DataInputFactory dataInputFactory) {
    super(dataInputFactory);
  }

  public List<GlucoseReadRecord> getGlucoseReads() {
    List<GlucoseReadRecord> glucoseReads = newArrayList();
    try {
      for (DatabasePage page : getPages()) {
        DatabasePageHeader header = page.getPageHeader();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(page.getPageData());
        DataInput input = this.dataInputFactory.create(inputStream);

        LOGGER.debug(format("Parsing [%d] records...", header.getNumberOfRecords()));
        for (int i = 0; i < header.getNumberOfRecords(); i++) {
          byte[] recordBytes = new byte[GlucoseReadRecord.RECORD_LENGTH];
          input.readFully(recordBytes, 0, GlucoseReadRecord.RECORD_LENGTH);

          GlucoseReadRecord glucoseReadRecord = parseRecord(recordBytes, header, header.getFirstRecordIndex() + i);

          glucoseReads.add(glucoseReadRecord);
        }
      }
      return glucoseReads;
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private GlucoseReadRecord parseRecord(byte[] recordBytes, DatabasePageHeader header,
                                        long recordNumber) throws IOException {
    DataInput input = this.dataInputFactory.create(new ByteArrayInputStream(recordBytes));
    LOGGER.debug(format("Parsing glucose record from bytes [%s]", toHexString(recordBytes)));

    long systemSeconds = UnsignedInts.toLong(input.readInt());
    long displaySeconds = UnsignedInts.toLong(input.readInt());
    int glucoseValueWithFlags = input.readUnsignedShort();
    byte trendAndArrowNoise = input.readByte();
    int actualReceiverCrc = input.readUnsignedShort();

    GlucoseReadRecord glucoseReadRecord = new GlucoseReadRecord(systemSeconds, displaySeconds,
            glucoseValueWithFlags, trendAndArrowNoise, recordNumber, header.getPageNumber());
    LOGGER.debug(format("Parsed GlucoseRead: [%s]", glucoseReadRecord));
    validateCrc(actualReceiverCrc, recordBytes);

    return glucoseReadRecord;
  }
}
