Special values for glucose reads:
```
        AbsoluteAberration = 9,
        CountsAberration = 6,
        MinimallyEGVAberration = 2,
        NoAntenna = 3,
        None = 0,
        PowerAberration = 10,
        RFBadStatus = 12,
        SensorNotActive = 1,
        SensorOutOfCal = 5
```

None (0) is one I've seen but all of them should be read as not a valid read. 

Summary of current packet layout
================================
Click [here](http://interactive.blockdiag.com/packetdiag/image?compression=deflate&encoding=base64&src=eJyr5lJQUDCwUgj2dwOxDK0UnPNzcxPzUjxTQHwjXWMrhYDEypz8xJTgzKpUkJiJrilQVZCzoRlXLQDihg-U) for visual representation.

```01```: sof (not sure what the meaning is but it's almost a constant)

```xx```: command id

```xx xx```: size of payload little endian

```xx xx```: crc16 (short, 2 bytes, little endian)

Serializing commands as empty packets (not realistic since some commands actually have a payload)
-------------------------------------------------------------------------------------------------
```
Unknown => 01 00 00 00 B4 76
ReadBlock => 01 01 00 00 84 41
WriteBlock => 01 02 00 00 D4 18
EraseBlock => 01 03 00 00 E4 2F
ReadFirmware => 01 04 00 00 74 AA
WriteFirmware => 01 05 00 00 44 9D
ACK => 01 06 00 00 14 C4
ReadHWConfig => 01 07 00 00 24 F3
WriteHWConfig => 01 08 00 00 15 DF
ReadBios => 01 09 00 00 25 E8
WriteBios => 01 0A 00 00 75 B1
ReadBiosHeader => 01 0B 00 00 45 86
ReadFirmwareHeader => 01 0C 00 00 D5 03
ReadRTC => 01 0D 00 00 E5 34
WriteRTC => 01 0E 00 00 B5 6D
ResetReceiver => 01 0F 00 00 85 5A
ReadSettings => 01 10 00 00 D7 35
WriteSettings => 01 11 00 00 E7 02
WriteTrim => 01 12 00 00 B7 5B
ReadInternalTime => 01 13 00 00 87 6C
InvalidParam => 01 14 00 00 17 E9
NAK => 01 15 00 00 27 DE
InvalidCommand => 01 16 00 00 77 87
ReadDatabase => 01 17 00 00 47 B0
EnableTransmitter => 01 18 00 00 76 9C
IsEventLogEmpty => 01 19 00 00 46 AB
IsErrorLogEmpty => 01 1A 00 00 16 F2
AmIFirmware => 01 1B 00 00 26 C5
ReadDatabaseRevision => 01 1C 00 00 B6 40
RF_TestPacket => 01 1D 00 00 86 77
PC_Test => 01 1E 00 00 D6 2E
ReadADC => 01 1F 00 00 E6 19
ReadBootFailureReasons => 01 20 00 00 72 F0
SleepUntilReset => 01 21 00 00 42 C7
ReadLastButtonPressed => 01 22 00 00 12 9E
PeekMemory => 01 23 00 00 22 A9
PokeMemory => 01 24 00 00 B2 2C
ReadSensorImplantDate => 01 25 00 00 82 1B
WriteSensorImplantData => 01 26 00 00 D2 42
ReadNumberOf3DayLicenses => 01 27 00 00 E2 75
ReadNumberOf7DayLicenses => 01 28 00 00 D3 59
ReadTransmitterId => 01 29 00 00 E3 6E
WriteTransmitterId => 01 2A 00 00 B3 37
ReadLowGlucoseThreshold => 01 2B 00 00 83 00
WriteLowGlucoseThreshold => 01 2C 00 00 13 85
ReadHighGlucoseThreshold => 01 2D 00 00 23 B2
WriteHighGlucoseThreshold => 01 2E 00 00 73 EB
IsBlinded => 01 2F 00 00 43 DC
WriteBlindedMode => 01 30 00 00 11 B3
ReadDisplayTimeOffset => 01 37 00 00 81 36
WriteDisplayTimeOffset => 01 38 00 00 B0 1A
ReadGMT => 01 39 00 00 80 2D
WriteGMT => 01 3A 00 00 D0 74
GetSensorRecords => 01 3D 00 00 40 F1
ClearSensorRecords => 01 3E 00 00 10 A8
EnterFunctionalTestMode => 01 3F 00 00 20 9F
ReadHardwareBoardId => 01 40 00 00 19 6B
WriteUserLowAlertType => 01 41 00 00 29 5C
WriteUserHighAlertType => 01 42 00 00 79 05
WriteOutOfRangeAlertType => 01 43 00 00 49 32
WriteOtherAlertType => 01 44 00 00 D9 B7
WriteUpRateAlertType => 01 45 00 00 E9 80
WriteDownRateAlertType => 01 46 00 00 B9 D9
WriteUserLowSnooze => 01 47 00 00 89 EE
WriteUserHighSnooze => 01 48 00 00 B8 C2
WriteOutOfRangeTime => 01 49 00 00 88 F5
WriteUpRate => 01 4A 00 00 D8 AC
WriteDownRate => 01 4B 00 00 E8 9B
ClearRtcResetTime => 01 4C 00 00 78 1E
ReadErrorLogInfo => 01 4D 00 00 48 29
ReadBatteryVoltage => 01 4E 00 00 18 70
```
