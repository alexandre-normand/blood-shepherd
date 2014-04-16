[![Build Status](https://travis-ci.org/alexandre-normand/blood-shepherd.png?branch=master)](https://travis-ci.org/alexandre-normand/blood-shepherd)
blood-shepherd
===========

blood-shepherd is my playground of exploration for a sync org.glukit.sync.api to the
[Dexcom Platinum G4](http://dexcom.com/dexcom-g4-platinum) receiver. 
If you don't know what that is, chances are that it's because you don't have diabetes and it's a good thing.
If you do have diabetes, check it out, the [dexcom](http://dexcom.com/) is a nice tool. 

So, there isn't much to see here at the moment but hopefully, this will be actually sync data from the receiver in the near future.

Quick start
-----------
```mvn clean install```

Run it
------
```java -jar ./blood-shepherd-main/target/*jar-with-dependencies.jar```

Status
------
It's rough but it works as both a pure command-line tool and also wrapped as a SWT system tray application. 

The next generation
-------------------
A better version of a sync api for Mac OS X can be found here:  [bloodSheltie](https://github.com/alexandre-normand/bloodSheltie) (disclosure: I also wrote that one).

`bloodSheltie` has more robust support for keeping track of last updates and it has support for slightly more data.

Notes
-----
Everything is little-endian. Thankfully, google-guava has a nice
[LittleEndianDataOutputStream](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/io/LittleEndianDataOutputStream.html)
that provides the same convenient interface while breaking Java's contract of big-endianness.
