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

import com.google.common.base.Throwables;
import org.glukit.dexcom.sync.DataOutputFactory;
import org.glukit.dexcom.sync.model.DatabaseReadRequestSpec;
import org.glukit.dexcom.sync.model.ReceiverCommand;
import org.glukit.dexcom.sync.model.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static org.glukit.dexcom.sync.model.ReceiverCommand.ReadDatabasePages;

/**
 * ReadDatabasePages commands
 * @author alexandre.normand
 */
public class ReadDatabasePagesCommand extends BaseCommand {
  private static Logger LOGGER = LoggerFactory.getLogger(ReadDatabasePagesCommand.class);

  private RecordType recordType;
  private long pageNumber;
  private byte numberOfPages;

  public ReadDatabasePagesCommand(DataOutputFactory dataOutputFactory,
                                     RecordType recordType,
                                     long pageNumber,
                                     byte numberOfPages) {
    super(dataOutputFactory);
    checkArgument(numberOfPages > 0 && numberOfPages <= DatabaseReadRequestSpec.MAX_PAGES_PER_COMMAND, "Command is limited to [%s] pages or " +
            "less, given invalid value of [%s]", DatabaseReadRequestSpec.MAX_PAGES_PER_COMMAND, numberOfPages);
    this.recordType = recordType;
    this.pageNumber = pageNumber;
    this.numberOfPages = numberOfPages;
  }

  @Override
  protected byte[] getContent() {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      DataOutput dataOutput = this.dataOutputFactory.create(outputStream);
      dataOutput.write(this.recordType.getId());
      if (this.pageNumber > Integer.MAX_VALUE) {
        LOGGER.warn(format("Page number [%d] is larger than [%d], there might be something weird happening, " +
                "be on the lookout", this.pageNumber, Integer.MAX_VALUE));
      }

      // TODO: sigh, this might not actually work exactly as it should, needs review
      dataOutput.writeInt((int) this.pageNumber);
      dataOutput.write(this.numberOfPages);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public ReceiverCommand getCommand() {
    return ReadDatabasePages;
  }
}
