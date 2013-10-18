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

import com.google.inject.Inject;
import jssc.SerialPortList;
import org.glukit.dexcom.sync.tasks.IsReceiverOnThisPortRunner;

import java.util.regex.Pattern;

/**
 * Finds the {@link jssc.SerialPort} for the Dexcom receiver
 * @author alexandre.normand
 */
public class DexcomReceiverFinder {
  public static final Pattern DEVICE_FILTER = Pattern.compile(".*\\.usbmodem.*");
  private final IsReceiverOnThisPortRunner isReceiverOnThisPortRunner;

  @Inject
  public DexcomReceiverFinder(IsReceiverOnThisPortRunner isReceiverOnThisPortRunner) {
    this.isReceiverOnThisPortRunner = isReceiverOnThisPortRunner;
  }

  public String findReceiverPort() {
    String[] portNames = SerialPortList.getPortNames(DEVICE_FILTER);
    if (portNames == null || portNames.length == 0) {
      throw new IllegalStateException("Receiver serial port can't be found");
    }

    for (String port : portNames) {
      if (this.isReceiverOnThisPortRunner.isReceiver(port)) {
        return port;
      }
    }

    throw new IllegalStateException("Found some matching devices but none of them identified as the dexcom receiver. " +
            "Maybe another application is holding the port?");
  }
}
