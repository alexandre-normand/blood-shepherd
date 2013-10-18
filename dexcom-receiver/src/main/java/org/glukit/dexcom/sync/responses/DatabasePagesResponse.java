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
import org.glukit.dexcom.sync.model.DatabasePage;
import org.glukit.dexcom.sync.model.DatabasePageHeader;
import org.glukit.dexcom.sync.model.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.toHexString;

/**
 * Response for {@link org.glukit.dexcom.sync.model.ReceiverCommand#ReadDatabasePages}.
 *
 * @author alexandre.normand
 */
public class DatabasePagesResponse extends GenericResponse {
  private static Logger LOGGER = LoggerFactory.getLogger(DatabasePagesResponse.class);

  private static final int PAGE_HEADER_SIZE = 28;
  private static final int PAGE_DATA_SIZE = 500;

  private List<DatabasePage> pages;

  public DatabasePagesResponse(DataInputFactory dataInputFactory) {
    super(dataInputFactory);
  }

  @Override
  public void fromBytes(byte[] responseAsBytes) {
    this.pages = newArrayList();

    ByteArrayInputStream inputStream = new ByteArrayInputStream(responseAsBytes);
    DataInput dataInput = this.dataInputFactory.create(inputStream);

    try {
      int available = inputStream.available();
      while (available > 0) {
        LOGGER.debug(format("Available bytes remaining [%d]", available));
        if (available < PAGE_HEADER_SIZE + PAGE_DATA_SIZE) {
          String message =
                  format("Some bytes are still available but not enough for a page, something is buggy. " +
                          "Remaining count: [%d]", available);
          throw new IllegalStateException(message);
        }
        byte[] headerBytes = new byte[PAGE_HEADER_SIZE];
        dataInput.readFully(headerBytes, 0, PAGE_HEADER_SIZE);
        LOGGER.debug(format("Parsing header from bytes [%s]", toHexString(headerBytes)));
        DatabasePageHeader pageHeader = readPageHeader(headerBytes);

        byte[] pageData = new byte[PAGE_DATA_SIZE];
        dataInput.readFully(pageData, 0, PAGE_DATA_SIZE);
        available = inputStream.available();

        LOGGER.debug(format("Parsing page data from bytes [%s]", toHexString(pageData)));
        DatabasePage page = new DatabasePage(pageHeader, pageData);

        pages.add(page);
      }
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private DatabasePageHeader readPageHeader(byte[] headerBytes) {
    try {
      DataInput dataInput = this.dataInputFactory.create(new ByteArrayInputStream(headerBytes));

      long firstRecordIndex = UnsignedInts.toLong(dataInput.readInt());
      long numberOfRecords = UnsignedInts.toLong(dataInput.readInt());
      RecordType recordType = RecordType.fromId(dataInput.readByte());
      byte revision = dataInput.readByte();
      long pageNumber = UnsignedInts.toLong(dataInput.readInt());
      long reserved2 = UnsignedInts.toLong(dataInput.readInt());
      long reserved3 = UnsignedInts.toLong(dataInput.readInt());
      long reserved4 = UnsignedInts.toLong(dataInput.readInt());
      int crc = dataInput.readUnsignedShort();

      int expectedCrc = DecodingUtils.getCrc16(headerBytes, 0, PAGE_HEADER_SIZE - 2);

      if (crc != expectedCrc) {
        throw new IllegalStateException(format("Invalid crc, expected [%s], received [%s]",
                Integer.toHexString(expectedCrc), Integer.toHexString(crc)));
      }

      return new DatabasePageHeader(firstRecordIndex, numberOfRecords, recordType, revision,
              pageNumber, reserved2, reserved3, reserved4, crc);

    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  protected List<DatabasePage> getPages() {
    return pages;
  }
}
