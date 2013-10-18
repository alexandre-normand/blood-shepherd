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

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import jssc.SerialPort;
import org.glukit.dexcom.sync.tasks.FetchNewDataRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;

import static java.lang.String.format;

/**
 * This will listen on USB for device connection/disconnections and start a sync process
 * when it sees the Dexcom receiver plugged in.
 * @author alexandre.normand
 */
public class DexcomWatcher implements UsbServicesListener {
  private static Logger LOGGER = LoggerFactory.getLogger(DexcomDaemon.class);

  private final DeviceFilter deviceFilter;
  private final DexcomReceiverFinder receiverFinder;
  private final FetchNewDataRunner fetchNewDataRunner;

  @Inject
  public DexcomWatcher(DeviceFilter deviceFilter,
                       DexcomReceiverFinder receiverFinder,
                       FetchNewDataRunner fetchNewDataRunner) {
    this.deviceFilter = deviceFilter;
    this.receiverFinder = receiverFinder;
    this.fetchNewDataRunner = fetchNewDataRunner;
  }

  @Override
  public void usbDeviceAttached(UsbServicesEvent usbServicesEvent) {
    UsbDevice usbDevice = usbServicesEvent.getUsbDevice();
    UsbDeviceDescriptor deviceDescriptor = usbDevice.getUsbDeviceDescriptor();
    if (this.deviceFilter.isHighlander(deviceDescriptor)) {
      try {
        String message = format("Device connected [%s], isConfigured [%b]", usbDevice.getManufacturerString(),
                usbDevice.isConfigured());
        LOGGER.info(message);

        String receiverPort = this.receiverFinder.findReceiverPort();

        // TODO: store time of last sync
        this.fetchNewDataRunner.fetchData(new SerialPort(receiverPort), Instant.now());
      } catch (Exception e) {
        Throwables.propagate(e);
      }
    } else {
      String message = format("Device plugged was ignored: vendor id [%d], product id [%d]", deviceDescriptor.idVendor(),
              deviceDescriptor.idProduct());
      LOGGER.debug(message);
    }
  }

  @Override
  public void usbDeviceDetached(UsbServicesEvent usbServicesEvent) {
    UsbDeviceDescriptor deviceDescriptor = usbServicesEvent.getUsbDevice().getUsbDeviceDescriptor();
    if (this.deviceFilter.isHighlander(deviceDescriptor)) {
      LOGGER.info("Dexcom disconnected!");
    } else {
      String message = format("Device unplugged was ignore: vendor id [%d], product id [%d]",
              deviceDescriptor.idVendor(), deviceDescriptor.idProduct());
      LOGGER.debug(message);
    }
  }
}
