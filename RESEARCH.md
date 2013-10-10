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
Click [here](http://interactive.blockdiag.com/packetdiag/image?compression=deflate&encoding=base64&src=eJyr5lJQUDCwUgj2dwOxDHWNrBQCEpOzU0uCM6tSQULGVgrO-bm5iXkpnikgvomuKVAkyNnQjKsWAM-iDyI) for visual representation.

```01```: sof (not sure what the meaning is but it's almost a constant)

```xx xx```: size of payload little endian

```xx```: command id

```xx xx```: crc16 (short, 2 bytes, little endian)


