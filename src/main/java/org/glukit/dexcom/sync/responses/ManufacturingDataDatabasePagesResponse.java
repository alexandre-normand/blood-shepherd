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

import com.fasterxml.jackson.xml.XmlMapper;
import com.google.common.base.Throwables;
import com.google.common.primitives.UnsignedInts;
import org.glukit.dexcom.sync.DataInputFactory;
import org.glukit.dexcom.sync.DecodingUtils;
import org.glukit.dexcom.sync.ResponseReader;
import org.glukit.dexcom.sync.model.DatabasePage;
import org.glukit.dexcom.sync.model.ManufacturingParameters;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.*;
import static org.glukit.dexcom.sync.ResponseReader.TRAILER_SIZE;

/**
 * ManufacturingData {@link DatabasePagesResponse}
 *
 * @author alexandre.normand
 */
public class ManufacturingDataDatabasePagesResponse extends DatabasePagesResponse {
  public ManufacturingDataDatabasePagesResponse(DataInputFactory dataInputFactory) {
    super(dataInputFactory);
  }

  public List<ManufacturingParameters> getManufacturingParameters() {
    List<ManufacturingParameters> manufacturingParameters = newArrayList();
    try {
      for (DatabasePage page : getPages()) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(page.getPageData());
        DataInput input = this.dataInputFactory.create(inputStream);
        // TODO: something better than ignoring the data?
        long systemSeconds = UnsignedInts.toLong(input.readInt());
        long displaySeconds = UnsignedInts.toLong(input.readInt());

        byte[] xmlBytes = new byte[inputStream.available() - 2];
        input.readFully(xmlBytes);

        validateCrc(input.readUnsignedShort(), page.getPageData());

        XmlMapper xmlMapper = new XmlMapper();
        ManufacturingParameters parameterPage = xmlMapper.readValue(new String(xmlBytes, "UTF-8"),
                ManufacturingParameters.class);
        manufacturingParameters.add(parameterPage);
      }
      return manufacturingParameters;
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}
