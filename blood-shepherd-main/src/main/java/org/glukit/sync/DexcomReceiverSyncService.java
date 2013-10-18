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

package org.glukit.sync;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.glukit.dexcom.sync.DexcomDaemon;

import javax.usb.UsbException;
import java.util.concurrent.TimeUnit;

/**
 * Main class for the bloodsucker sync dexcomDaemon.
 *
 * @author alexandre.normand
 */
public class DexcomReceiverSyncService {

  private DexcomDaemon dexcomDaemon;

  @Inject
  public DexcomReceiverSyncService(DexcomDaemon dexcomDaemon) {
    this.dexcomDaemon = dexcomDaemon;
  }

  public void run() {
    this.dexcomDaemon.start();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        dexcomDaemon.stop();
      }
    });

    while (true) {
      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
    }
  }

  public static void main(String[] args) throws UsbException {
    Injector injector = Guice.createInjector(new DexcomModule());
    DexcomReceiverSyncService dexcomReceiverSyncService = injector.getInstance(DexcomReceiverSyncService.class);

    JCommander jCommander = new JCommander(dexcomReceiverSyncService, args);
    try {
      jCommander.parse(args);
    } catch (ParameterException e) {
      jCommander.usage();
      System.exit(1);
    }

    dexcomReceiverSyncService.run();
  }

}
