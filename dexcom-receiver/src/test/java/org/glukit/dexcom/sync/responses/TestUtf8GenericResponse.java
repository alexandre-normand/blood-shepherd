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

import org.glukit.dexcom.sync.LittleEndianDataInputFactory;
import org.junit.Test;

import static org.glukit.dexcom.sync.DecodingUtils.fromHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test of generic response decoding
 * @author alexandre.normand
 */
public class TestUtf8GenericResponse {

  @Test
  public void decodeShouldSucceed() throws Exception {
    byte input[] = fromHexString("3C 46 69 72 6D 77 61 72 65 48 65 61 64 65 72 20 53 63 68 65 6D 61 56 65 72 73 69 6F 6E 3D 27 31 27 20 41 70 69 56 65 72 73 69 6F 6E 3D 27 32 2E 32 2E 30 2E 30 27 20 54 65 73 74 41 70 69 56 65 72 73 69 6F 6E 3D 27 32 2E 34 2E 30 2E 30 27 20 50 72 6F 64 75 63 74 49 64 3D 27 47 34 52 65 63 65 69 76 65 72 27 20 50 72 6F 64 75 63 74 4E 61 6D 65 3D 27 44 65 78 63 6F 6D 20 47 34 20 52 65 63 65 69 76 65 72 27 20 53 6F 66 74 77 61 72 65 4E 75 6D 62 65 72 3D 27 53 57 31 30 30 35 30 27 20 46 69 72 6D 77 61 72 65 56 65 72 73 69 6F 6E 3D 27 32 2E 30 2E 31 2E 31 30 34 27 20 50 6F 72 74 56 65 72 73 69 6F 6E 3D 27 34 2E 36 2E 34 2E 34 35 27 20 52 46 56 65 72 73 69 6F 6E 3D 27 31 2E 30 2E 30 2E 32 37 27 20 44 65 78 42 6F 6F 74 56 65 72 73 69 6F 6E 3D 27 33 27 2F 3E");
    Utf8PayloadGenericResponse utf8GenericResponse = new Utf8PayloadGenericResponse(new LittleEndianDataInputFactory());
    utf8GenericResponse.fromBytes(input);

    assertThat(utf8GenericResponse.asString(), is("<FirmwareHeader SchemaVersion='1' ApiVersion='2.2.0.0' TestApiVersion='2.4.0.0' ProductId='G4Receiver' ProductName='Dexcom G4 Receiver' SoftwareNumber='SW10050' FirmwareVersion='2.0.1.104' PortVersion='4.6.4.45' RFVersion='1.0.0.27' DexBootVersion='3'/>"));
  }
}
