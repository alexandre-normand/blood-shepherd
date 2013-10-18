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

import org.glukit.dexcom.sync.LittleEndianDataOutputFactory;
import org.junit.Test;

import static org.glukit.dexcom.sync.DecodingUtils.fromHexString;
import static org.glukit.dexcom.sync.model.RecordType.EGVData;
import static org.glukit.dexcom.sync.model.RecordType.ManufacturingData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Unit test of {@link ReadDatabasePagesCommand}
 *
 * @author alexandre.normand
 */
public class TestReadDatabasePagesCommand {
  @Test
  public void readDatabasePagesShouldMatchExample() throws Exception {
    ReadDatabasePagesCommand readDatabasePagesCommand =
            new ReadDatabasePagesCommand(new LittleEndianDataOutputFactory(), ManufacturingData, 0L, (byte) 1);
    assertThat(readDatabasePagesCommand.asBytes(), equalTo(fromHexString("01 0C 00 11 00 00 00 00 00 01 6E 45")));
  }

  @Test
    public void readGlucoseDatabasePagesShouldMatchExample() throws Exception {
      ReadDatabasePagesCommand readDatabasePagesCommand =
              new ReadDatabasePagesCommand(new LittleEndianDataOutputFactory(), EGVData, 1465L, (byte) 4);
      assertThat(readDatabasePagesCommand.asBytes(), equalTo(fromHexString("01 0C 00 11 04 B9 05 00 00 04 6D 29")));
    }
}
