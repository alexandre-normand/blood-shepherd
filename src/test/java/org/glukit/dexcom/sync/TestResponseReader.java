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

package org.glukit.dexcom.sync;

import jssc.SerialPort;
import org.glukit.dexcom.sync.responses.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.glukit.dexcom.sync.DecodingUtils.fromHexString;
import static org.glukit.dexcom.sync.ResponseReader.HEADER_SIZE;
import static org.glukit.dexcom.sync.ResponseReader.TRAILER_SIZE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link ResponseReader}
 *
 * @author alexandre.normand
 */
@RunWith(MockitoJUnitRunner.class)
public class TestResponseReader {
  @Mock
  SerialPort serialPort;

  @Test
  public void readGenericResponseShouldSucceed() throws Exception {
    when(serialPort.readBytes(HEADER_SIZE)).thenReturn(fromHexString("01 03 01 01"));
    when(serialPort.readBytes(259 - (HEADER_SIZE + TRAILER_SIZE))).thenReturn(fromHexString("3C 46 69 72 6D 77 61 72 65 48 65 61 64 65 72 20 53 63 68 65 6D 61 56 65 72 73 69 6F 6E 3D 27 31 27 20 41 70 69 56 65 72 73 69 6F 6E 3D 27 32 2E 32 2E 30 2E 30 27 20 54 65 73 74 41 70 69 56 65 72 73 69 6F 6E 3D 27 32 2E 34 2E 30 2E 30 27 20 50 72 6F 64 75 63 74 49 64 3D 27 47 34 52 65 63 65 69 76 65 72 27 20 50 72 6F 64 75 63 74 4E 61 6D 65 3D 27 44 65 78 63 6F 6D 20 47 34 20 52 65 63 65 69 76 65 72 27 20 53 6F 66 74 77 61 72 65 4E 75 6D 62 65 72 3D 27 53 57 31 30 30 35 30 27 20 46 69 72 6D 77 61 72 65 56 65 72 73 69 6F 6E 3D 27 32 2E 30 2E 31 2E 31 30 34 27 20 50 6F 72 74 56 65 72 73 69 6F 6E 3D 27 34 2E 36 2E 34 2E 34 35 27 20 52 46 56 65 72 73 69 6F 6E 3D 27 31 2E 30 2E 30 2E 32 37 27 20 44 65 78 42 6F 6F 74 56 65 72 73 69 6F 6E 3D 27 33 27 2F 3E"));
    when(serialPort.readBytes(TRAILER_SIZE)).thenReturn(fromHexString("D8 D4"));

    ResponseReader responseReader = new ResponseReader(new LittleEndianDataInputFactory());
    Utf8PayloadGenericResponse genericResponse = responseReader.read(Utf8PayloadGenericResponse.class, this.serialPort);

    assertThat(genericResponse, not(nullValue()));
    assertThat(genericResponse.asString(), is("<FirmwareHeader SchemaVersion='1' ApiVersion='2.2.0.0' TestApiVersion='2.4.0.0' ProductId='G4Receiver' ProductName='Dexcom G4 Receiver' SoftwareNumber='SW10050' FirmwareVersion='2.0.1.104' PortVersion='4.6.4.45' RFVersion='1.0.0.27' DexBootVersion='3'/>"));
  }

  @Test
  public void readPageRangeResponseShouldMatchExample() throws Exception {
    when(serialPort.readBytes(HEADER_SIZE)).thenReturn(fromHexString("01 0E 00 01"));
    when(serialPort.readBytes(14 - HEADER_SIZE - TRAILER_SIZE)).thenReturn(fromHexString("01 00 00 00 02 00 00 00"));
    when(serialPort.readBytes(TRAILER_SIZE)).thenReturn(fromHexString("97 11"));

    ResponseReader responseReader = new ResponseReader(new LittleEndianDataInputFactory());
    PageRangeResponse pageRangeResponse = responseReader.read(PageRangeResponse.class, this.serialPort);

    assertThat(pageRangeResponse, not(nullValue()));
    assertThat(pageRangeResponse.getFirstPage(), is(1L));
    assertThat(pageRangeResponse.getLastPage(), is(2L));
  }

  @Test
  public void readDatabasePagesShouldSucceed() throws Exception {
    when(serialPort.readBytes(HEADER_SIZE)).thenReturn(fromHexString("01 16 02 01"));
    when(serialPort.readBytes(534 - HEADER_SIZE - TRAILER_SIZE)).thenReturn(fromHexString("00 00 00 00 01 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3A 7D 7D F6 89 07 FD 85 89 07 3C 4D 61 6E 75 66 61 63 74 75 72 69 6E 67 50 61 72 61 6D 65 74 65 72 73 20 53 65 72 69 61 6C 4E 75 6D 62 65 72 3D 22 73 6D 33 30 31 34 30 37 35 32 22 20 48 61 72 64 77 61 72 65 50 61 72 74 4E 75 6D 62 65 72 3D 22 4D 44 31 30 36 30 2D 4D 54 32 30 36 34 39 22 20 48 61 72 64 77 61 72 65 52 65 76 69 73 69 6F 6E 3D 22 31 34 22 20 44 61 74 65 54 69 6D 65 43 72 65 61 74 65 64 3D 22 32 30 31 33 2D 30 31 2D 30 33 20 31 33 3A 35 34 3A 30 35 2E 35 33 36 20 2D 30 38 3A 30 30 22 20 48 61 72 64 77 61 72 65 49 64 3D 22 7B 37 35 42 37 43 38 38 36 2D 46 45 31 30 2D 34 32 30 46 2D 42 35 31 31 2D 32 44 33 46 39 42 39 42 45 45 37 45 7D 22 20 2F 3E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F4 FB"));
    when(serialPort.readBytes(TRAILER_SIZE)).thenReturn(fromHexString("7F 04"));

    ResponseReader responseReader = new ResponseReader(new LittleEndianDataInputFactory());
    DatabasePagesResponse pagesResponse = responseReader.read(DatabasePagesResponse.class, this.serialPort);

    assertThat(pagesResponse, not(nullValue()));
  }

  @Test
  public void readManufacturingDataDatabasePagesShouldSucceed() throws Exception {
    when(serialPort.readBytes(HEADER_SIZE)).thenReturn(fromHexString("01 16 02 01"));
    when(serialPort.readBytes(534 - HEADER_SIZE - TRAILER_SIZE)).thenReturn(fromHexString("00 00 00 00 01 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3A 7D 7D F6 89 07 FD 85 89 07 3C 4D 61 6E 75 66 61 63 74 75 72 69 6E 67 50 61 72 61 6D 65 74 65 72 73 20 53 65 72 69 61 6C 4E 75 6D 62 65 72 3D 22 73 6D 33 30 31 34 30 37 35 32 22 20 48 61 72 64 77 61 72 65 50 61 72 74 4E 75 6D 62 65 72 3D 22 4D 44 31 30 36 30 2D 4D 54 32 30 36 34 39 22 20 48 61 72 64 77 61 72 65 52 65 76 69 73 69 6F 6E 3D 22 31 34 22 20 44 61 74 65 54 69 6D 65 43 72 65 61 74 65 64 3D 22 32 30 31 33 2D 30 31 2D 30 33 20 31 33 3A 35 34 3A 30 35 2E 35 33 36 20 2D 30 38 3A 30 30 22 20 48 61 72 64 77 61 72 65 49 64 3D 22 7B 37 35 42 37 43 38 38 36 2D 46 45 31 30 2D 34 32 30 46 2D 42 35 31 31 2D 32 44 33 46 39 42 39 42 45 45 37 45 7D 22 20 2F 3E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F4 FB"));
    when(serialPort.readBytes(TRAILER_SIZE)).thenReturn(fromHexString("7F 04"));

    ResponseReader responseReader = new ResponseReader(new LittleEndianDataInputFactory());
    ManufacturingDataDatabasePagesResponse pagesResponse =
            responseReader.read(ManufacturingDataDatabasePagesResponse.class, this.serialPort);

    assertThat(pagesResponse, not(nullValue()));
    assertThat(pagesResponse.getManufacturingParameters().size(), is(1));
  }
}
