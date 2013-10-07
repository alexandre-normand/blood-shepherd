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

A lot of packets start with 01. Then, it's followed by the size of the packet (everything is little endian). The last two
bytes are for the CRC16 short value. So in examples like those, the command ids would be the single byte following ```06 00```:

```01 06 00 0A 5E 65``` (0A: Write bios)

```01 06 00 0B 7F 75``` (0B: ReadBiosHeader) 

Except that it doesn't seem to fit with most examples. 

For example: Sending AmIFirmware (0x1b) with ```01 06 00 1b 4e 67``` gives back a 8 byte response ```01 08 00 01 09 04 a1 8a``` instead of the expected 7 bytes response with ACK (0x06) and 1 byte payload.
