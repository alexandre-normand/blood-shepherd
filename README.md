[![Build Status](https://travis-ci.org/alexandre-normand/blood-shepherd.png?branch=master)](https://travis-ci.org/alexandre-normand/blood-shepherd)
blood-shepherd
===========

blood-shepherd is my playground of exploration for a sync org.glukit.sync.api to the
[Dexcom Platinum G4](http://dexcom.com/dexcom-g4-platinum) receiver. 
If you don't know what that is, chances are that it's because you don't have diabetes and it's a good thing.
If you do have diabetes, check it out, the [dexcom](http://dexcom.com/) is a nice tool. 

So, there isn't much to see here at the moment but hopefully, this will be actually sync data from the receiver in the near
future.

This will likely be ported to C/Go when this is done but exploring with Java first.

Quick start
-----------
```mvn clean install```

Run it
------
```java -jar ./blood-shepherd-main/target/*jar-with-dependencies.jar```

Status
------
It's rough but it works as both a pure command-line tool and also wrapped as a SWT system tray application. 

Notes
-----
Everything is little-endian. Thankfully, google-guava has a nice
[LittleEndianDataOutputStream](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/io/LittleEndianDataOutputStream.html)
that provides the same convenient interface while breaking Java's contract of big-endianness.

Resources
---------
Here's the successor to blood-shepherd as a objective-c library. It's not exactly a superset of blood-sheperd but it does have a better general support. It's called [blood-sheltie](https://github.com/alexandre-normand/blood-sheltie).
