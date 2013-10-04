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

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * All possible receiver commands.
 * @author alexandre.normand
 */
public enum ReceiverCommand {
  ACK((byte) 6),
  AmIFirmware((byte) 0x1b),
  ClearRtcResetTime((byte) 0x4c),
  ClearSensorRecords((byte) 0x3e),
  EnableTransmitter((byte) 0x18),
  EnterFunctionalTestMode((byte) 0x3f),
  EraseBlock((byte) 3),
  GetSensorRecords((byte) 0x3d),
  InvalidCommand((byte) 0x16),
  InvalidParam((byte) 20),
  IsBlinded((byte) 0x2f),
  IsErrorLogEmpty((byte) 0x1a),
  IsEventLogEmpty((byte) 0x19),
  NAK((byte) 0x15),
  PC_Test((byte) 30),
  PeekMemory((byte) 0x23),
  PokeMemory((byte) 0x24),
  ReadADC((byte) 0x1f),
  ReadBatteryVoltage((byte) 0x4e),
  ReadBios((byte) 9),
  ReadBiosHeader((byte) 11),
  ReadBlock((byte) 1),
  ReadBootFailureReasons((byte) 0x20),
  ReadDatabase((byte) 0x17),
  ReadDatabaseRevision((byte) 0x1c),
  ReadDisplayTimeOffset((byte) 0x37),
  ReadErrorLogInfo((byte) 0x4d),
  ReadFirmware((byte) 4),
  ReadFirmwareHeader((byte) 12),
  ReadGMT((byte) 0x39),
  ReadHardwareBoardId((byte) 0x40),
  ReadHighGlucoseThreshold((byte) 0x2d),
  ReadHWConfig((byte) 7),
  ReadInternalTime((byte) 0x13),
  ReadLastButtonPressed((byte) 0x22),
  ReadLowGlucoseThreshold((byte) 0x2b),
  ReadNumberOf3DayLicenses((byte) 0x27),
  ReadNumberOf7DayLicenses((byte) 40),
  ReadRTC((byte) 13),
  ReadSensorImplantDate((byte) 0x25),
  ReadSettings((byte) 0x10),
  ReadTransmitterId((byte) 0x29),
  ResetReceiver((byte) 15),
  RF_TestPacket((byte) 0x1d),
  SleepUntilReset((byte) 0x21),
  Unknown((byte) 0),
  WriteBios((byte) 10),
  WriteBlindedMode((byte) 0x30),
  WriteBlock((byte) 2),
  WriteDisplayTimeOffset((byte) 0x38),
  WriteDownRate((byte) 0x4b),
  WriteDownRateAlertType((byte) 70),
  WriteFirmware((byte) 5),
  WriteGMT((byte) 0x3a),
  WriteHighGlucoseThreshold((byte) 0x2e),
  WriteHWConfig((byte) 8),
  WriteLowGlucoseThreshold((byte) 0x2c),
  WriteOtherAlertType((byte) 0x44),
  WriteOutOfRangeAlertType((byte) 0x43),
  WriteOutOfRangeTime((byte) 0x49),
  WriteRTC((byte) 14),
  WriteSensorImplantData((byte) 0x26),
  WriteSettings((byte) 0x11),
  WriteTransmitterId((byte) 0x2a),
  WriteTrim((byte) 0x12),
  WriteUpRate((byte) 0x4a),
  WriteUpRateAlertType((byte) 0x45),
  WriteUserHighAlertType((byte) 0x42),
  WriteUserHighSnooze((byte) 0x48),
  WriteUserLowAlertType((byte) 0x41),
  WriteUserLowSnooze((byte) 0x47);

  private byte id;
  private static Map<Byte, ReceiverCommand> mappings;

  private ReceiverCommand(byte id) {
    this.id = id;
    addMapping(id, this);
  }

  private static void addMapping(byte id, ReceiverCommand receiverCommand) {
    if (mappings == null) {
      mappings = newHashMap();
    }
    mappings.put(id, receiverCommand);
  }

  public static ReceiverCommand fromId(byte id) {
    return mappings.get(id);
  }

  public byte getId() {
    return id;
  }
}
