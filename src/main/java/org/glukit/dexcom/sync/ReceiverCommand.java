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
 *
 * @author alexandre.normand
 */
public enum ReceiverCommand {
  Ack((byte) 0x01),
  EnterFirmwareUpgradeMode((byte) 0x32),
  EnterSambaAccessMode((byte) 0x35),
  EraseDatabase((byte) 0x2d),
  IncompletePacketReceived((byte) 0x05),
  InvalidCommand((byte) 0x03),
  InvalidMode((byte) 0x07),
  InvalidParam((byte) 0x04),
  MaxCommand((byte) 0x3b),
  MaxPossibleCommand((byte) 0xff),
  Nak((byte) 0x02),
  Null((byte) 0x00),
  Ping((byte) 0x0A),
  ReadBatteryLevel((byte) 0x21),
  ReadBatteryState((byte) 0x30),
  ReadBlindedMode((byte) 0x27),
  ReadClockMode((byte) 0x29),
  ReadDatabasePageHeader((byte) 0x12),
  ReadDatabasePageRange((byte) 0x10),
  ReadDatabasePages((byte) 0x11),
  ReadDatabaseParitionInfo((byte) 0x0F),
  ReadDeviceMode((byte) 0x2b),
  ReadDisplayTimeOffset((byte) 0x1d),
  ReadEnableSetUpWizardFlag((byte) 0x37),
  ReadFirmwareHeader((byte) 0x0B),
  ReadFirmwareSettings((byte) 0x36),
  ReadFlashPage((byte) 0x33),
  ReadGlucoseUnit((byte) 0x25),
  ReadHardwareBoardId((byte) 0x31),
  ReadLanguage((byte) 0x1b),
  ReadRTC((byte) 0x1f),
  ReadSetUpWizardState((byte) 0x39),
  ReadSystemTime((byte) 0x22),
  ReadSystemTimeOffset((byte) 0x23),
  ReadTransmitterID((byte) 0x19),
  ReceiverError((byte) 0x06),
  ResetReceiver((byte) 0x20),
  ShutdownReceiver((byte) 0x2e),
  WriteBlindedMode((byte) 0x28),
  WriteClockMode((byte) 0x2a),
  WriteDisplayTimeOffset((byte) 0x1E),
  WriteEnableSetUpWizardFlag((byte) 0x38),
  WriteFlashPage((byte) 0x34),
  WriteGlucoseUnit((byte) 0x26),
  WriteLanguage((byte) 0x1c),
  WritePCParameters((byte) 0x2f),
  WriteSetUpWizardState((byte) 0x3a),
  WriteSystemTime((byte) 0x24),
  WriteTransmitterID((byte) 0x1a);

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
