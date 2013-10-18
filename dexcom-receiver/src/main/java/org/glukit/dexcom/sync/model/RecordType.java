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

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Type of record
 *
 * @author alexandre.normand
 */
public enum RecordType {
  Aberration((byte) 0x06),
  CalSet((byte) 0x05),
  EGVData((byte) 0x04),
  FirmwareParameterData((byte) 0x01),
  InsertionTime((byte) 0x07),
  ManufacturingData((byte) 0x00),
  MaxValue((byte) 0x0D),
  MeterData((byte) 0x0A),
  PCSoftwareParameter((byte) 0x02),
  ReceiverErrorData((byte) 0x09),
  ReceiverLogData((byte) 0x08),
  SensorData((byte) 0x03),
  UserEventData((byte) 0x0B),
  UserSettingData((byte) 0x0C);

  private byte id;
    private static Map<Byte, RecordType> mappings;

    private RecordType(byte id) {
      this.id = id;
      addMapping(id, this);
    }

    private static void addMapping(byte id, RecordType recordType) {
      if (mappings == null) {
        mappings = newHashMap();
      }
      mappings.put(id, recordType);
    }

    public static RecordType fromId(byte id) {
      return mappings.get(id);
    }

    public byte getId() {
      return id;
    }
}
