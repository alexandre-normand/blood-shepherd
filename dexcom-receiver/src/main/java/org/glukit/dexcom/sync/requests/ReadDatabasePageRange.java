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

import org.glukit.dexcom.sync.DataOutputFactory;
import org.glukit.dexcom.sync.model.ReceiverCommand;
import org.glukit.dexcom.sync.model.RecordType;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ReadDatabasePageRange request command.
 *
 * @author alexandre.normand
 */
public class ReadDatabasePageRange extends BaseCommand {

  private final RecordType recordType;

  public ReadDatabasePageRange(DataOutputFactory dataOutputFactory, RecordType recordType) {
    super(dataOutputFactory);
    checkNotNull(recordType, "recordType should be non-null");
    this.recordType = recordType;
  }

  @Override
  public ReceiverCommand getCommand() {
    return ReceiverCommand.ReadDatabasePageRange;
  }

  @Override
  protected byte[] getContent() {
    return new byte[]{this.recordType.getId()};
  }
}
