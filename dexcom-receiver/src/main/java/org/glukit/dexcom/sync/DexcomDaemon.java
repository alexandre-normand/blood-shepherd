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
import de.ailis.usb4java.libusb.LibUsb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.usb.UsbServices;
import java.util.Properties;

/**
 * The daemon that will start the syncing services.
 *
 * @author alexandre.normand
 */
public class DexcomDaemon {
  private static Logger LOGGER = LoggerFactory.getLogger(DexcomDaemon.class);
  private final UsbServices usbServices;
  private final DexcomWatcher watcher;

  @Inject
  public DexcomDaemon(UsbServices usbServices, DexcomWatcher watcher) {
    this.usbServices = usbServices;
    this.watcher = watcher;
  }

  public void start() {
    this.usbServices.addUsbServicesListener(watcher);
    LibUsb.init(null);
  }

  public void stop() {
    this.usbServices.removeUsbServicesListener(watcher);
    LibUsb.exit(null);
  }
}
